/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xTz
 */
@AIName("restored_hetgolem")
public class RestoredHetgolemAI2 extends AggressiveNpcAI2 {

	private Future<?> lifeTask;
	private AtomicBoolean isStartEvent = new AtomicBoolean(false);

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					getMoveController().abortMove();
					setSubStateIfNot(AISubState.WALK_RANDOM);
					setStateIfNot(AIState.WALKING);
					float direction = Rnd.get(0, 199) / 100f;
					float x1 = (float) (Math.cos(Math.PI * direction) * 8);
					float y1 = (float) (Math.sin(Math.PI * direction) * 8);
					WorldPosition p = getPosition();
					if ( p != null && p.getWorldMapInstance() != null) {
						getMoveController().moveToPoint(p.getX() + x1, p.getY() + y1, p.getZ());
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
					}
				}
			}

		}, 3000);
		startLifeTask();
	}

	private void startLifeTask() {
		lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					spawnEvent();
				}
			}

		}, 5000);
	}

	private void cancelTask() {
		if (lifeTask != null && !lifeTask.isDone()) {
			lifeTask.cancel(true);
		}
	}

	private void spawnEvent() {
		if (isStartEvent.compareAndSet(false, true)) {
			WorldPosition p = getPosition();
			if (p != null && p.getWorldMapInstance() != null) {
				spawn(282308, p.getX(), p.getY(), p.getZ(), p.getHeading());
				Npc npc = (Npc)spawn(282465, p.getX(), p.getY(), p.getZ(), p.getHeading());
				NpcActions.delete(npc);
			}
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}

	@Override
	public void handleDied() {
		cancelTask();
		spawnEvent();
		super.handleDied();
	}

	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_RESIST_ABNORMAL:
				return AIAnswers.POSITIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
