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

import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author Rolandas
 */
public class FreezeEventHandler {

	public static void onUnfreeze(AbstractAI ai) {
		if (ai.isInSubState(AISubState.FREEZE)) {
			ai.setSubStateIfNot(AISubState.NONE);
			if (ai instanceof NpcAI2) {
				Npc npc = ((NpcAI2) ai).getOwner();
				if (npc.getWalkerGroup() != null) {
					ai.setStateIfNot(AIState.WALKING);
					ai.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
				} else if (npc.getSpawn().getRandomWalk() > 0) {
					ai.setStateIfNot(AIState.WALKING);
					ai.setSubStateIfNot(AISubState.WALK_RANDOM);
				}
				npc.updateKnownlist();
			}
			ai.think();
		}
	}

	public static void onFreeze(AbstractAI ai) {
		if (ai.isInState(AIState.WALKING)) {
			WalkManager.stopWalking((NpcAI2) ai);
		}
		ai.setStateIfNot(AIState.IDLE);
		ai.setSubStateIfNot(AISubState.FREEZE);
		ai.think();
		if (ai instanceof NpcAI2) {
			Npc npc = ((NpcAI2) ai).getOwner();
			npc.updateKnownlist();
			npc.getAggroList().clear();
			npc.getEffectController().removeAllEffects();
		}
	}
}
