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
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.AttackIntention;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 */
public class AttackManager {

	/**
	 * @param npcAI
	 */
	public static void startAttacking(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: startAttacking");
		}
		npcAI.getOwner().getGameStats().setFightStartingTime();
		EmoteManager.emoteStartAttacking(npcAI.getOwner());
		scheduleNextAttack(npcAI);
	}

	/**
	 * @param npcAI
	 */
	public static void scheduleNextAttack(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: scheduleNextAttack");
		}
		// don't start attack while in casting substate
		AISubState subState = npcAI.getSubState();
		if (subState == AISubState.NONE) {
			chooseAttack(npcAI, npcAI.getOwner().getGameStats().getNextAttackInterval());
		}
		else {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Will not choose attack in substate" + subState);
			}
		}
	}

	/**
	 * choose attack type
	 */
	protected static void chooseAttack(NpcAI2 npcAI, int delay) {
		AttackIntention attackIntention = npcAI.chooseAttackIntention();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: chooseAttack " + attackIntention + " delay " + delay);
		}
		if (!npcAI.canThink()) {
			return;
		}
		switch (attackIntention) {
			case SIMPLE_ATTACK:
				SimpleAttackManager.performAttack(npcAI, delay);
				break;
			case SKILL_ATTACK:
				SkillAttackManager.performAttack(npcAI, delay);
				break;
			case FINISH_ATTACK:
				npcAI.think();
				break;
			default:
				break;
		}
	}

	/**
	 * @param npcAI
	 */
	public static void targetTooFar(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: attackTimeDelta " + npc.getGameStats().getLastAttackTimeDelta());
		}

		// switch target if there is more hated creature
		if (npc.getGameStats().getLastChangeTargetTimeDelta() > 5) {
			Creature mostHated = npc.getAggroList().getMostHated();
			if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead() && !npc.isTargeting(mostHated.getObjectId())) {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "AttackManager: switching target during chase");
				}
				npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
				return;
			}
		}
		if (!npc.canSee((Creature) npc.getTarget())) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		if (checkGiveupDistance(npcAI)) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		if (npcAI.isMoveSupported()) {
			npc.getMoveController().moveToTargetObject();
			return;
		}
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
	}

	private static boolean checkGiveupDistance(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		// if target run away too far
		float distanceToTarget = npc.getDistanceToTarget();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: distanceToTarget " + distanceToTarget);
		}
		// TODO may be ask AI too
		int chaseTarget = npc.isBoss() ? 50 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo()
			.getChaseTarget();
		if (distanceToTarget > chaseTarget) {
			return true;
		}
		double distanceToHome = npc.getDistanceToSpawnLocation();
		// if npc is far away from home
		int chaseHome = npc.isBoss() ? 150 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo()
			.getChaseHome();
		if (distanceToHome > chaseHome) {
			return true;
		}
		// start thinking about home after 100 meters and no attack for 10 seconds (only for default monsters)
		if (chaseHome <= 200) { // TODO: Check Client and use chase_user_by_trace value
			if ((npc.getGameStats().getLastAttackTimeDelta() > 20 && npc.getGameStats().getLastAttackedTimeDelta() > 20)
				|| (distanceToHome > chaseHome / 2 && npc.getGameStats().getLastAttackedTimeDelta() > 10)) {
				return true;
			}
		}
		return false;
	}
}
