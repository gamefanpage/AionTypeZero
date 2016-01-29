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

package ai.instance.steelRake;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xTz
 */
@AIName("golden_eye_mantutu")
public class GoldenEyeMantutuAI2 extends AggressiveNpcAI2 {

	private boolean canThink = true;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> hungerTask;

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if (eventId == 1 && args != null) {
			canThink = false;
			getMoveController().abortMove();
			EmoteManager.emoteStopAttacking(getOwner());
			Npc npc = (Npc) args[0];
			getOwner().setTarget(npc);
			setStateIfNot(AIState.FOLLOWING);
			getMoveController().moveToTargetObject();
			getOwner().setState(1);
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
		}
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		if (!canThink) {
			VisibleObject target = getTarget();
			getMoveController().abortMove();
			if (target != null && target.isSpawned() && target instanceof Npc) {
				Npc npc = (Npc) target;
				int npcId = npc.getNpcId();
				if (npcId == 281128 || npcId == 281129) {
					startFeedTime(npc);
				}
			}
		}
	}

	private void startFeedTime(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead() && npc != null) {
					switch (npc.getNpcId()) {
						case 281128:
							// Feed Supply Device
							getEffectController().removeEffect(20489);
							spawn(701386, 716.508f, 508.571f, 939.607f, (byte) 119);
							break;
						case 281129:
							// Water Supply Device
							spawn(701387, 716.389f, 494.207f, 939.607f, (byte) 119);
							getEffectController().removeEffect(20490);
							break;
					}
					NpcActions.delete(npc);
					canThink = true;
					Creature creature = getAggroList().getMostHated();
					if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
						setStateIfNot(AIState.FIGHT);
						think();
					}
					else {
						getOwner().setTarget(creature);
						getOwner().getGameStats().renewLastAttackTime();
						getOwner().getGameStats().renewLastAttackedTime();
						getOwner().getGameStats().renewLastChangeTargetTime();
						getOwner().getGameStats().renewLastSkillTime();
						setStateIfNot(AIState.FIGHT);
						handleMoveValidate();
					}
				}
			}

		}, 6000);
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
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			doSchedule();
		}
	}

	@Override
	protected void handleDespawned() {
		cancelHungerTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancelHungerTask();
		Npc npc = getPosition().getWorldMapInstance().getNpc(219037);
		if (npc != null && !NpcActions.isAlreadyDead(npc)) {
			npc.getEffectController().removeEffect(18189);
		}
		super.handleDied();
	}

	@Override
	protected void handleBackHome() {
		cancelHungerTask();
		getEffectController().removeEffect(20489);
		getEffectController().removeEffect(20490);
		canThink = true;
		isHome.set(true);
		super.handleBackHome();
	}

	private void doSchedule() {
		hungerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				int rnd = Rnd.get(1, 2);
				int skill = 0;
				switch (rnd) {
					case 1:
						skill = 20489; // Hunger
						break;
					case 2:
						skill = 20490; // Thirst
						break;
				}
				SkillEngine.getInstance().getSkill(getOwner(), skill, 20, getOwner()).useNoAnimationSkill();
			}

		}, 10000, 30000);
	}

	private void cancelHungerTask() {
		if (hungerTask != null && !hungerTask.isDone()) {
			hungerTask.cancel(true);
		}
	}

}
