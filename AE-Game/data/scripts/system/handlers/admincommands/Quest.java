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

package admincommands;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.QuestStateList;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.FinishedQuestCond;
import org.typezero.gameserver.model.templates.quest.XMLStartCondition;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MrPoke
 */
public class Quest extends AdminCommand {

	public Quest() {
		super("quest");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set|show|delete>");
			return;
		}
		Player target = null;
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Incorrect target!");
			return;
		}

		if (params[0].equals("start")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}
			int id;
			try {
				String quest = params[1];
				Pattern questId = Pattern.compile("\\[quest:([^%]+)]");
				Matcher result = questId.matcher(quest);
				if (result.find())
					id = Integer.parseInt(result.group(1));
				else
					id = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}

			QuestEnv env = new QuestEnv(null, target, id, 0);

			if (QuestService.startQuest(env)) {
				PacketSendUtility.sendMessage(admin, "Quest started.");
			}
			else {
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(id);
				List<XMLStartCondition> preconditions = template.getXMLStartConditions();
				if (preconditions != null && preconditions.size() > 0) {
					for (XMLStartCondition condition : preconditions) {
						List<FinishedQuestCond> finisheds = condition.getFinishedPreconditions();
						if (finisheds != null && finisheds.size() > 0) {
							for (FinishedQuestCond fcondition : finisheds) {
								QuestState qs1 = admin.getQuestStateList().getQuestState(fcondition.getQuestId());
								if (qs1 == null || qs1.getStatus() != QuestStatus.COMPLETE) {
									PacketSendUtility.sendMessage(admin, "You have to finish " + fcondition.getQuestId() + " first!");
								}
							}
						}
					}
				}
				PacketSendUtility.sendMessage(admin, "Quest not started. Some preconditions failed");
			}
		}
		else if (params[0].equals("set")) {
			int questId, var;
			int varNum = 0;
			QuestStatus questStatus;
			try {
				String quest = params[1];
				Pattern id = Pattern.compile("\\[quest:([^%]+)]");
				Matcher result = id.matcher(quest);
				if (result.find())
					questId = Integer.parseInt(result.group(1));
				else
					questId = Integer.parseInt(params[1]);

				String statusValue = params[2];
				if ("START".equals(statusValue)) {
					questStatus = QuestStatus.START;
				}
				else if ("NONE".equals(statusValue)) {
					questStatus = QuestStatus.NONE;
				}
				else if ("COMPLETE".equals(statusValue)) {
					questStatus = QuestStatus.COMPLETE;
				}
				else if ("REWARD".equals(statusValue)) {
					questStatus = QuestStatus.REWARD;
				}
				else {
					PacketSendUtility.sendMessage(admin, "<status is one of START, NONE, REWARD, COMPLETE>");
					return;
				}
				var = Integer.valueOf(params[3]);
				if (params.length == 5 && params[4] != null && params[4] != "") {
					varNum = Integer.valueOf(params[4]);
				}
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest set <questId status var [varNum]>");
				return;
			}
			QuestState qs = target.getQuestStateList().getQuestState(questId);
			if (qs == null) {
				qs = new QuestState(questId, questStatus, 0, 0, new Timestamp(0), 0, new Timestamp(0));
				PacketSendUtility.sendMessage(admin, "<QuestState has been newly initialized.>");
				return;
			}
			qs.setStatus(questStatus);
			if (varNum != 0) {
				qs.setQuestVarById(varNum, var);
			}
			else {
				qs.setQuestVar(var);
			}
			PacketSendUtility.sendPacket(target, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			if (questStatus == QuestStatus.COMPLETE) {
				qs.setCompleteCount(qs.getCompleteCount() + 1);
				target.getController().updateNearbyQuests();
			}
		}
		if (params[0].equals("delete")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest delete <quest id>");
				return;
			}
			int id;
			try {
				id = Integer.valueOf(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest delete <quest id>");
				return;
			}

			QuestStateList list = admin.getQuestStateList();
			if (list == null || list.getQuestState(id) == null) {
				PacketSendUtility.sendMessage(admin, "Quest not deleted.");
			}
			else {
				QuestState qs = list.getQuestState(id);
				qs.setQuestVar(0);
				qs.setCompleteCount(0);
				qs.setStatus(null);
				if (qs.getPersistentState() != PersistentState.NEW)
					qs.setPersistentState(PersistentState.DELETED);
				PacketSendUtility.sendPacket(admin, new SM_QUEST_COMPLETED_LIST(admin.getQuestStateList().getAllFinishedQuests()));
				admin.getController().updateNearbyQuests();
			}
		}
		else if (params[0].equals("show")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest show <quest id>");
				return;
			}
			ShowQuestInfo(target, admin, params[1]);
		}
		else
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set|show|delete>");
	}

	private void ShowQuestInfo(Player player, Player admin, String param) {
		int id;
		try {
			id = Integer.valueOf(param);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "syntax //quest show <quest id>");
			return;
		}
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null) {
			PacketSendUtility.sendMessage(admin, "Quest state: NULL");
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 5; i++)
				sb.append(Integer.toString(qs.getQuestVarById(i)) + " ");
			PacketSendUtility.sendMessage(admin, "Quest state: " + qs.getStatus().toString() + "; vars: " + sb.toString()
					+ qs.getQuestVarById(5));
			sb.setLength(0);
			sb = null;
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //quest <start|set|show|delete>");
	}

}
