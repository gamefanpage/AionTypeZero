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
 * Talk with Cornelius (203780). Talk with Sabotes (203781). Collect Tear of Luck (182206098) (20) and take them to
 * Cornelius. Take the Oath Stone (186000080) to High Priest Jucleas (203752) and ask him to perform the ritual of
 * affirmation. Talk with Lavirintos (203701).
 *
 * @author Nanou
 * @reworked vlog
 * @modified Gigi
 */
public class _3939PersistenceAndLuck extends QuestHandler {

	private final static int questId = 3939;

	public _3939PersistenceAndLuck() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203701, 203780, 203781, 203752, 700537 };
		qe.registerQuestNpc(203701).addOnQuestStart(questId);
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

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) { // Lavirintos
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
				case 203780: { // Cornelius
					switch (dialog) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							if (var == 2)
								return sendQuestDialog(env, 1693);
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
						case CHECK_USER_HAS_QUEST_ITEM:
							return checkQuestItems(env, 2, 3, false, 10000, 10001, 182206099, 1); // 3
						case FINISH_DIALOG:
							return defaultCloseDialog(env, var, var);
					}
					break;
				}
				case 203781: { // Sabotes
					switch (dialog) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case SELECT_ACTION_1354: {
							if (var == 1 && player.getInventory().tryDecreaseKinah(3400000)) {
								return defaultCloseDialog(env, 1, 2, 122001274, 1, 0, 0); // 2
							}
							else {
								return sendQuestDialog(env, 1438);
							}
						}
						case FINISH_DIALOG:
							return defaultCloseDialog(env, 1, 1);
					}
					break;
				}
				case 700537:
					if (dialog == DialogAction.USE_OBJECT && var == 2) {
						return useQuestObject(env, 2, 2, false, 0);
					}
					break;
				case 203752: { // Jucleas
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SET_SUCCEED: {
							if (player.getInventory().getItemCountByItemId(186000080) >= 1) {
								removeQuestItem(env, 186000080, 1);
								return defaultCloseDialog(env, 3, 3, true, false, 0);
							}
							else {
								return sendQuestDialog(env, 2120);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203701) { // Lavirintos
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
