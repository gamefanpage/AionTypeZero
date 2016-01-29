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

package org.typezero.gameserver.ai2.manager;

import org.typezero.gameserver.ai2.AI2Logger;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class SimpleAttackManager {

	/**
	 * @param npcAI
	 * @param delay
	 */
	public static void performAttack(NpcAI2 npcAI, int delay) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "performAttack");
		}
		if (npcAI.getOwner().getGameStats().isNextAttackScheduled()) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Attack already sheduled");
			}
			scheduleCheckedAttackAction(npcAI, delay);
			return;
		}

		if (!isTargetInAttackRange(npcAI.getOwner())) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Attack will not be scheduled because of range");
			}
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
			return;
		}
		npcAI.getOwner().getGameStats().setNextAttackTime(System.currentTimeMillis() + delay);
		if (delay > 0) {
			ThreadPoolManager.getInstance().schedule(new SimpleAttackAction(npcAI), delay);
		}
		else {
			attackAction(npcAI);
		}
	}

	/**
	 * @param npcAI
	 * @param delay
	 */
	private static void scheduleCheckedAttackAction(NpcAI2 npcAI, int delay) {
		if (delay < 2000) {
			delay = 2000;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "Scheduling checked attack " + delay);
		}
		ThreadPoolManager.getInstance().schedule(new SimpleCheckedAttackAction(npcAI), delay);
	}

	public static boolean isTargetInAttackRange(Npc npc) {
		if (npc.getAi2().isLogging()) {
			float distance = npc.getDistanceToTarget();
			AI2Logger.info((AbstractAI) npc.getAi2(), "isTargetInAttackRange: " + distance);
		}
		if (npc.getTarget() == null || !(npc.getTarget() instanceof Creature))
			return false;
		return MathUtil.isInAttackRange(npc, (Creature) npc.getTarget(), npc.getGameStats().getAttackRange().getCurrent() / 1000f);
		//return distance <= npc.getController().getAttackDistanceToTarget() + NpcMoveController.MOVE_CHECK_OFFSET;
	}

	/**
	 * @param npcAI
	 */
	protected static void attackAction(final NpcAI2 npcAI) {
		if (!npcAI.isInState(AIState.FIGHT)) {
			return;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "attackAction");
		}
		Npc npc = npcAI.getOwner();
		Creature target = (Creature) npc.getTarget();
		if (target != null && !target.getLifeStats().isAlreadyDead()) {
			if (!npc.canSee(target) || !GeoService.getInstance().canSee(npc, target)) { //delete check geo when the Path Finding
             if (npc.getNpcId() != 218085 && npc.getNpcId() != 211040) {
				npc.getController().cancelCurrentSkill();
				npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
				return;
			}
			}
			if (isTargetInAttackRange(npc)) {
				npc.getController().attackTarget(target, 0);
				npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
				return;
			}
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
		}
		else {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
	}

	private final static class SimpleAttackAction implements Runnable {

		private NpcAI2 npcAI;

		SimpleAttackAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}

		@Override
		public void run() {
			attackAction(npcAI);
			npcAI = null;
		}

	}

	private final static class SimpleCheckedAttackAction implements Runnable {

		private NpcAI2 npcAI;

		SimpleCheckedAttackAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}

		@Override
		public void run() {
			if (!npcAI.getOwner().getGameStats().isNextAttackScheduled()) {
				attackAction(npcAI);
			}
			else {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "Scheduled checked attacked confirmed");
				}
			}
			npcAI = null;
		}

	}

}
