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

package ai.instance.dragonLordsRefuge;

import java.util.ArrayList;
import java.util.List;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.Future;

/**
 * @author Cheatkiller
 *
 */
@AIName("gravitycrusher")
public class GravityCrusherAI2 extends AggressiveNpcAI2 {

   private Future<?> skillTask;
   private Future<?> transformTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		transform();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackPlayer();
			}
		}, 2000);
	}

	private void transform() {
		transformTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
				   if (skillTask != null)
					  skillTask.cancel(true);
					AI2Actions.useSkill(GravityCrusherAI2.this, 20967); //self destruct

					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							NpcShoutsService.getInstance().sendMsg(getOwner(), 1401554);
							spawn(283140, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
							AI2Actions.deleteOwner(GravityCrusherAI2.this);
						}
					}, 3000);

				}
			}
		}, 30000);
	}

	@Override
	public void handleMoveArrived() {
	   super.handleMoveArrived();
	   skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
					AI2Actions.useSkill(GravityCrusherAI2.this, 20987);
				}
			},0, 5000);
	}

	private void attackPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 200)) {
				players.add(player);
			}
		}
		Player player = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
		getOwner().setTarget(player);
		setStateIfNot(AIState.WALKING);
		getOwner().setState(1);
		getOwner().getMoveController().moveToTargetObject();
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
	}

	private void cancelTask() {
	   if (skillTask != null && !skillTask.isCancelled())
		  skillTask.cancel(true);
	   if (transformTask != null && !transformTask.isCancelled())
		  transformTask.cancel(true);
	}

	@Override
	public void handleDied() {
	   super.handleDied();
	   cancelTask();
	}

	@Override
	public void handleDespawned() {
	   super.handleDespawned();
	   cancelTask();
	}

	@Override
	public int modifyMaccuracy(int value) {
		return 1200;
	}

	@Override
	public boolean canThink() {
	   return false;
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
