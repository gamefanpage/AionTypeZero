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

package org.typezero.gameserver.network.aion.gmhandler;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.QuestStateList;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.QuestCategory;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;

public class CmdDeleteQuest extends AbstractGMHandler {

	public CmdDeleteQuest(Player admin, String params) {
		super(admin, params);
		run();
	}

	private void run() {
        Player t = admin;

        if (admin.getTarget() != null && admin.getTarget() instanceof Player)
            t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));

		Integer questID = Integer.parseInt(params);
		if (questID <= 0) {
			return;
		}

		@SuppressWarnings("static-access")
		QuestTemplate qt = DataManager.getInstance().QUEST_DATA.getQuestById(questID);
		if (qt == null) {
			PacketSendUtility.sendMessage(admin, "Quest with ID: " + questID  + " was not found");
			return;
		}

		QuestStateList list = t.getQuestStateList();
		if (list == null || list.getQuestState(questID) == null) {
			PacketSendUtility.sendMessage(admin, "Quest not deleted for target " + t.getName());
			return;
		}

		QuestState qs = list.getQuestState(questID);
		qs.setQuestVar(0);
		qs.setCompleteCount(0);
		if (qt.getCategory() == QuestCategory.MISSION) {
			qs.setStatus(QuestStatus.START);
		} else {
			qs.setStatus(null);
		}
		if (qs.getPersistentState() != PersistentState.NEW) {
			qs.setPersistentState(PersistentState.DELETED);
		}
		PacketSendUtility.sendPacket(t, new SM_QUEST_COMPLETED_LIST(t.getQuestStateList().getAllFinishedQuests()));
		t.getController().updateNearbyQuests();
	}

}
