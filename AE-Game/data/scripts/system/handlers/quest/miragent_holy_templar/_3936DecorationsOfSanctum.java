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

package quest.miragent_holy_templar;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou
 * @modified Gigi
 */
public class _3936DecorationsOfSanctum extends QuestHandler {

	private final static int questId = 3936;

	public _3936DecorationsOfSanctum() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203710).addOnQuestStart(questId);// Dairos
		qe.registerQuestNpc(203710).addOnTalkEvent(questId);// Dairos
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		// Start to Dairos
		if (qs == null || (qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE)) {
			if (targetId == 203710) {
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				// 1 - Report the result to Dairos.
				case 203710:
					switch (dialog) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							else if (var == 1)
								return sendQuestDialog(env, 1352);
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
						case CHECK_USER_HAS_QUEST_ITEM:
							long itemCount1 = player.getInventory().getItemCountByItemId(182206091);
							long itemCount2 = player.getInventory().getItemCountByItemId(182206092);
							long itemCount3 = player.getInventory().getItemCountByItemId(182206093);
							long itemCount4 = player.getInventory().getItemCountByItemId(182206094);
							if (itemCount1 >= 10 && itemCount2 >= 10 && itemCount3 >= 10 && itemCount4 >= 10) {
								removeQuestItem(env, 182206091, 10);
								removeQuestItem(env, 182206092, 10);
								removeQuestItem(env, 182206093, 10);
								removeQuestItem(env, 182206094, 10);
								changeQuestStep(env, 1, 1, true);
								return sendQuestDialog(env, 5);
							}
							else
								return sendQuestDialog(env, 10001);
					}
					break;
				// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203710)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
