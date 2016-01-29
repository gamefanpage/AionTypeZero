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
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.geometry.Point3D;

/**
 * @author ATracer
 */
public class ReturningEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onNotAtHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onNotAtHome");
		}
		if (npcAI.setStateIfNot(AIState.RETURNING)) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "returning and restoring");
			}
			EmoteManager.emoteStartReturning(npcAI.getOwner());
		}
		if (npcAI.isInState(AIState.RETURNING)) {
			Npc npc = (Npc) npcAI.getOwner();
			if (npc.hasWalkRoutes()) {
				WalkManager.startWalking(npcAI);
			}
			else {
				Point3D prevStep = npcAI.getOwner().getMoveController().recallPreviousStep();
				npcAI.getOwner().getMoveController().moveToPoint(prevStep.getX(), prevStep.getY(), prevStep.getZ());
			}
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onBackHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onBackHome");
		}
		npcAI.getOwner().getMoveController().clearBackSteps();
		if (npcAI.setStateIfNot(AIState.IDLE)) {
			EmoteManager.emoteStartIdling(npcAI.getOwner());
			ThinkEventHandler.thinkIdle(npcAI);
		}
		Npc npc = (Npc) npcAI.getOwner();
		npc.getController().onReturnHome();
	}

}
