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
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.TownService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class TalkEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onTalk(NpcAI2 npcAI, Creature creature) {
		onSimpleTalk(npcAI, creature);

		if (creature instanceof Player) {
			Player player = (Player) creature;
			if (QuestEngine.getInstance().onDialog(new QuestEnv(npcAI.getOwner(), player, 0, -1)))
				return;
			// only player villagers can use villager npcs in oriel/pernon
			switch (npcAI.getOwner().getObjectTemplate().getTitleId()) {
				case 462877:
					int playerTownId = TownService.getInstance().getTownResidence(player);
					int currentTownId = TownService.getInstance().getTownIdByPosition(player);
					if (playerTownId != currentTownId) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 44));
						return;
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
						return;
					}
				default:
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					break;
			}
		}

	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onSimpleTalk(NpcAI2 npcAI, Creature creature) {
		npcAI.setSubStateIfNot(AISubState.TALK);
		npcAI.getOwner().setTarget(creature);
	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onFinishTalk(NpcAI2 npcAI, Creature creature) {
		Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId())) {
			if (npcAI.getState() != AIState.FOLLOWING)
				owner.setTarget(null);
			npcAI.think();
		}
	}

	/**
	 * No SM_LOOKATOBJECT broadcast
	 * 
	 * @param npcAI
	 * @param creature
	 */
	public static void onSimpleFinishTalk(NpcAI2 npcAI, Creature creature) {
		Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()) && npcAI.setSubStateIfNot(AISubState.NONE)) {
			owner.setTarget(null);
		}
	}

}
