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

package org.typezero.gameserver.ai2.handler;

import org.typezero.gameserver.ai2.AI2Logger;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.manager.AttackManager;
import org.typezero.gameserver.ai2.manager.FollowManager;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 */
public class TargetEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onTargetReached(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetReached");
		}

		AIState currentState = npcAI.getState();
		switch (currentState) {
			case FIGHT:
				npcAI.getOwner().getMoveController().abortMove();
				AttackManager.scheduleNextAttack(npcAI);
				if (npcAI.getOwner().getMoveController().isFollowingTarget())
					npcAI.getOwner().getMoveController().storeStep();
				break;
			case RETURNING:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().recallPreviousStep();
				if (npcAI.getOwner().isAtSpawnLocation())
					npcAI.onGeneralEvent(AIEventType.BACK_HOME);
				else
					npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
				break;
			case FOLLOWING:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().storeStep();
				break;
			case FEAR:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.getOwner().getMoveController().storeStep();
				break;
			case WALKING:
				WalkManager.targetReached(npcAI);
				checkAggro(npcAI);
				break;
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetTooFar(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetTooFar");
		}
		switch (npcAI.getState()) {
			case FIGHT:
				AttackManager.targetTooFar(npcAI);
				break;
			case FOLLOWING:
				FollowManager.targetTooFar(npcAI);
				break;
			case FEAR:
				break;
			default:
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "default onTargetTooFar");
				}
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetGiveup(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetGiveup");
		}
		VisibleObject target = npcAI.getOwner().getTarget();
		if (target != null) {
			npcAI.getOwner().getAggroList().stopHating(target);
		}
		if (npcAI.isMoveSupported()) {
			npcAI.getOwner().getMoveController().abortMove();
		}
		if (!npcAI.isAlreadyDead())
			npcAI.think();
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetChange(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetChange");
		}
		if (npcAI.isInState(AIState.FIGHT)) {
			npcAI.getOwner().setTarget(creature);
			AttackManager.scheduleNextAttack(npcAI);
		}
	}

	private static void checkAggro(NpcAI2 npcAI) {
		for (VisibleObject obj : npcAI.getOwner().getKnownList().getKnownObjects().values()) {
			if (obj instanceof Creature) {
				CreatureEventHandler.checkAggro(npcAI, (Creature) obj);
			}
		}
	}
}
