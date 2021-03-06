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
 */
public class _3934TheQuestForTemplars extends QuestHandler {

	private final static int questId = 3934;

	public _3934TheQuestForTemplars() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 798359, 798360, 798361, 798362, 798363, 798364, 798365, 798366, 203752, 203701 };
		qe.registerQuestNpc(203701).addOnQuestStart(questId);// Lavirintos
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		// 0 - Start to Lavirintos
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) {
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
				// 1 - Talk with Nianalo
				case 798359:
					switch (dialog) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 1011);
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				// 2 - Talk with Navid
				case 798360:
					if (var == 1) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 1352);
							case SETPRO2:
								return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				// 3 - Talk with Pavel
				case 798361:
					if (var == 2) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 1693);
							case SETPRO3:
								return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				// 4 - Talk with Pendaon
				case 798362:
					if (var == 3) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 2034);
							case SETPRO4:
								return defaultCloseDialog(env, 3, 4); // 4
						}
					}
					break;
				// 5 - Talk with Poevius
				case 798363:
					if (var == 4) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 2375);
							case SETPRO5:
								return defaultCloseDialog(env, 4, 5); // 5
						}
					}
					break;
				// 6 - Talk with Belicanon
				case 798364:
					if (var == 5) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 2716);
							case SETPRO6:
								return defaultCloseDialog(env, 5, 6); // 6
						}
					}
					break;
				// 7 - Talk with Mahelnu
				case 798365:
					if (var == 6) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 3057);
							case SETPRO7:
								return defaultCloseDialog(env, 6, 7); // 7
						}
					}
					break;
				// 8 - Talk with Pater
				case 798366:
					if (var == 7) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 3398);
							case SETPRO8:
								return defaultCloseDialog(env, 7, 8); // 8
						}
					}
					break;
				// 9 - Report the result to Jucleas with the Oath Stone
				case 203752:
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 8) {
								return sendQuestDialog(env, 3739);
							}
						}
						case SET_SUCCEED: {
							if (player.getInventory().getItemCountByItemId(186000080) >= 1) {
								removeQuestItem(env, 186000080, 1);
								return defaultCloseDialog(env, 8, 8, true, false, 0);
							}
							else {
								return sendQuestDialog(env, 3825);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203701) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
