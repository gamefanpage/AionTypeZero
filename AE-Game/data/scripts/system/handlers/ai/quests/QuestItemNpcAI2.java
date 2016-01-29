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

package ai.quests;

import java.util.ArrayList;
import java.util.List;

import ai.ActionItemNpcAI2;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Actions.SelectDialogResult;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.CreatureEventHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestActionType;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.drop.DropService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 *
 */
@AIName("quest_use_item")
public class QuestItemNpcAI2 extends ActionItemNpcAI2 {

	private List<Player> registeredPlayers = new ArrayList<Player>();

	@Override
	protected void handleDialogStart(Player player) {
		if (!(QuestEngine.getInstance().onCanAct(new QuestEnv(getOwner(), player, 0, 0),
			getObjectTemplate().getTemplateId(), QuestActionType.ACTION_ITEM_USE))) {
			return;
		}
		super.handleDialogStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, -1);
		if (!dialogResult.isSuccess()) {
			if (isDialogNpc()) {
				// show default dialog
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), DialogAction.SELECT_ACTION_1011.id()));
			}
			return;
		}
		QuestEnv questEnv = dialogResult.getEnv();
		if (QuestService.getQuestDrop(getNpcId()).isEmpty()) {
			return;
		}

		if (registeredPlayers.isEmpty()) {
			AI2Actions.scheduleRespawn(this);
			if (player.isInGroup2()) {
				registeredPlayers = QuestService.getEachDropMembersGroup(player.getPlayerGroup2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty()) {
					registeredPlayers.add(player);
				}
			}
			else if (player.isInAlliance2()) {
				registeredPlayers = QuestService.getEachDropMembersAlliance(player.getPlayerAlliance2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty()) {
					registeredPlayers.add(player);
				}
			}
			else {
				registeredPlayers.add(player);
			}
			AI2Actions.registerDrop(this, player, registeredPlayers);
			DropService.getInstance().requestDropList(player, getObjectId());
		}
		else if (registeredPlayers.contains(player)) {
			DropService.getInstance().requestDropList(player, getObjectId());
		}
	}

	private boolean isDialogNpc() {
		return getObjectTemplate().isDialogNpc();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		registeredPlayers.clear();
	}

	@Override
	protected void handleCreatureSee(Creature creature) {
		CreatureEventHandler.onCreatureSee(this, creature);
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		CreatureEventHandler.onCreatureMoved(this, creature);
	}

}
