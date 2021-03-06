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

package quest.raksang;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author zhkchi
 *
 */
public class _28710ScalingRewards  extends QuestHandler {

	private static final int questId = 28710;

	public _28710ScalingRewards() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799436).addOnQuestStart(questId);
		qe.registerQuestNpc(799436).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 799436) {
				switch (dialog) {
					case QUEST_SELECT:{
						QuestService.startQuest(env);
						return sendQuestDialog(env, 1011);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (targetId == 799436) {
				switch (dialog) {
					case USE_OBJECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1011:
						if (player.getInventory().getItemCountByItemId(182006427) >= 4) {
							removeQuestItem(env, 182006427, 4);
							qs.setQuestVar(1);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestDialog(env, 1009);
					case SELECT_ACTION_1352:
						if (player.getInventory().getItemCountByItemId(182006427) >= 7) {
							removeQuestItem(env, 182006427, 7);
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 6);
						}
						else
							return sendQuestDialog(env, 1009);
					case SELECT_ACTION_1693:
						if (player.getInventory().getItemCountByItemId(182006427) >= 10) {
							removeQuestItem(env, 182006427, 10);
							qs.setQuestVar(3);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 7);
						}
						else
							return sendQuestDialog(env, 1009);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799436) {
				int var = qs.getQuestVarById(0);
				switch (dialog) {
					case USE_OBJECT:
						if (var == 1)
							return sendQuestDialog(env, 5);
						else if (var == 2)
							return sendQuestDialog(env, 6);
						else if (var == 3)
							return sendQuestDialog(env, 7);
						else if (var == 4)
							return sendQuestDialog(env, 8);
					case SELECTED_QUEST_NOREWARD:
						QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
				}
			}
		}
		return false;
	}
}
