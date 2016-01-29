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
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 */
public class ThinkEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onThink(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "think");
		}
		if(npcAI.isAlreadyDead()){
			AI2Logger.info(npcAI, "can't think in dead state");
			return;
		}
		if(!npcAI.tryLockThink()){
			AI2Logger.info(npcAI, "can't acquire lock");
			return;
		}
		try {
			if (!npcAI.getOwner().getPosition().isMapRegionActive() || npcAI.getSubState() == AISubState.FREEZE) {
				thinkInInactiveRegion(npcAI);
				return;
			}
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "think state " + npcAI.getState());
			}
			switch (npcAI.getState()) {
				case FIGHT:
					thinkAttack(npcAI);
					break;
				case WALKING:
					thinkWalking(npcAI);
					break;
				case IDLE:
					thinkIdle(npcAI);
					break;
			}
		}
		finally {
			npcAI.unlockThink();
		}
	}

	/**
	 * @param npcAI
	 */
	private static void thinkInInactiveRegion(NpcAI2 npcAI) {

		if (!npcAI.canThink()) {
			return;
		}

		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "think in inactive region: " + npcAI.getState());
		}
		switch (npcAI.getState()) {
			case FIGHT:
				thinkAttack(npcAI);
				break;
			default:
				if (!npcAI.getOwner().isAtSpawnLocation()) {
					npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
				}
		}

	}

	/**
	 * @param npcAI
	 */
	public static void thinkAttack(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		Creature mostHated = npc.getAggroList().getMostHated();
		if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead()) {
			npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
		}
		else {
			npc.getMoveController().recallPreviousStep();
			npcAI.onGeneralEvent(AIEventType.ATTACK_FINISH);
			npcAI.onGeneralEvent(npc.isAtSpawnLocation() ? AIEventType.BACK_HOME : AIEventType.NOT_AT_HOME);
		}
	}

	/**
	 * @param npcAI
	 */
	public static void thinkWalking(NpcAI2 npcAI) {
		WalkManager.startWalking(npcAI);
	}

	/**
	 * @param npcAI
	 */
	public static void thinkIdle(NpcAI2 npcAI) {
		if (WalkManager.isWalking(npcAI)) {
			boolean startedWalking = WalkManager.startWalking(npcAI);
			if (!startedWalking) {
				npcAI.setStateIfNot(AIState.IDLE);
			}
		}
	}
}
