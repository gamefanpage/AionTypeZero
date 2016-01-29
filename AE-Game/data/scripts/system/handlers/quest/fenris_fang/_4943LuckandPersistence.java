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

package quest.fenris_fang;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Nanou
 * @reworked Gigi
 */
public class _4943LuckandPersistence extends QuestHandler {

	private final static int questId = 4943;

	public _4943LuckandPersistence() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204096, 204097, 204075, 204053, 700538 };
		qe.registerQuestNpc(204053).addOnQuestStart(questId); // Kvasir
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		// 0 - Start to Kvasir
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204053) {
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
				// 1 - Talk with Latatusk
				case 204096:
					if (var == 0) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 1011);
							case SETPRO1:
								return defaultCloseDialog(env, 0, 1);
						}
					}
					if (var == 2) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 1693);
							case CHECK_USER_HAS_QUEST_ITEM:
								if (QuestService.collectItemCheck(env, true)) {
									removeQuestItem(env, 122001275, 1);
									changeQuestStep(env, 2, 3, false);
									return sendQuestDialog(env, 10000);
								}
								else
									return sendQuestDialog(env, 10001);
						}
					}
					break;
				// 2 - Talk with Relir.
				case 204097:
					if (var == 1) {
						switch (dialog) {
							case QUEST_SELECT:
								return sendQuestDialog(env, 1352);
							case SELECT_ACTION_1354:
								if (player.getInventory().tryDecreaseKinah(3400000)) {
									if (player.getInventory().getItemCountByItemId(122001275) == 0) {
										if (!giveQuestItem(env, 122001275, 1))
											return true;
									}
									changeQuestStep(env, 1, 2, false);
									return sendQuestDialog(env, 1354);
								}
								else {
									return sendQuestDialog(env, 1438);
								}
						}
					}
					break;
				case 700538:
					if (dialog == DialogAction.USE_OBJECT && var == 2) {
						return useQuestObject(env, 2, 2, false, 0);
					}
					break;
				// 4 - Better purify yourself! Take Glossy Holy Water and visit High Priest Balder
				case 204075:
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SET_SUCCEED: {
							if (player.getInventory().getItemCountByItemId(186000084) >= 1) {
								removeQuestItem(env, 186000084, 1);
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
				// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			// 5 - Talk with Kvasir
			if (targetId == 204053) {
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
