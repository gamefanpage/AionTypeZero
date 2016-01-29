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

package quest.event_quests;

import org.typezero.gameserver.configs.main.EventsConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class _89999ItemGiving extends QuestHandler {

	private final static int questId = 89999;

	public _89999ItemGiving() {
		super(questId);
	}

	@Override
	public void register() {
		// Juice
		qe.registerQuestNpc(799702).addOnTalkEvent(questId); // Laylin (elyos)
		qe.registerQuestNpc(799703).addOnTalkEvent(questId); // Ronya (asmodian)
		// Cakes
		qe.registerQuestNpc(798414).addOnTalkEvent(questId); // Brios (elyos)
		qe.registerQuestNpc(798416).addOnTalkEvent(questId); // Bothen (asmodian)
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		int itemId = 0;
		Player player = env.getPlayer();

		if (env.getTargetId() == 799703 || env.getTargetId() == 799702)
			itemId = EventsConfig.EVENT_GIVEJUICE;
		else if (env.getTargetId() == 798416 || env.getTargetId() == 798414)
			itemId = EventsConfig.EVENT_GIVECAKE;

		if (itemId == 0)
			return false;

		int targetId = env.getVisibleObject().getObjectId();
		switch (env.getDialog()) {
			case USE_OBJECT:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1011, 0));
				return true;
			case SELECT_ACTION_1012: {
				Storage inventory = player.getInventory();
				if (inventory.getItemCountByItemId(itemId) > 0) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1097, 0));
					return true;
				}
				else {
					if (giveQuestItem(env, itemId, 1))
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1012, 0));
					return true;
				}
			}
		}
		return false;

	}
}
