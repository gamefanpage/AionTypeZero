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

package quest.eltnen;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _1037SecretsoftheTemple extends QuestHandler {

	private final static int questId = 1037;

	public _1037SecretsoftheTemple() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203965, 203967, 700151, 700154, 700150, 700153, 700152 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerGetingItem(182201027, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}

		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203965: { // Castor
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 203967: { // Axelion
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SELECT_ACTION_1694: {
							if (var == 2 && QuestService.collectItemCheck(env, true)) {
                                changeQuestStep(env, 2,3, false);
								return sendQuestDialog(env, 1694);
							}
							else {
								return sendQuestDialog(env, 1779);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case SETPRO3: {
							giveQuestItem(env, 182201027, 1);
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 700151: { // Flower Wall
					if (dialog == DialogAction.USE_OBJECT) {
						changeQuestStep(env, 3, 4, false); // 4
						return true;
					}
					break;
				}
				case 700154: { // Lightning Wall
					if (dialog == DialogAction.USE_OBJECT) {
						changeQuestStep(env, 4, 5, false); // 5
						return true;
					}
					break;
				}
				case 700150: { // Wave Wall
					if (dialog == DialogAction.USE_OBJECT) {
						changeQuestStep(env, 5, 6, false); // 6
						return true;
					}
					break;
				}
				case 700153: { // Wind Wall
					if (dialog == DialogAction.USE_OBJECT) {
						changeQuestStep(env, 6, 7, false); // 7
						return true;
					}
					break;
				}
				case 700152: { // Fire Wall
					if (dialog == DialogAction.USE_OBJECT) {
						changeQuestStep(env, 7, 7, true); // reward
						removeQuestItem(env, 182201027, 1);
						return true;
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203965) { // Castor
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2034);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		return defaultOnGetItemEvent(env, 2, 3, false); // 3
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1300, true);
	}
}
