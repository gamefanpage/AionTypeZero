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

package quest.altgard;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Olenja (203606).<br>
 * Search Grave Robbers Den and find out what the Lepharist Revolutionaries are looking for (700136).<br>
 * Report the result to Olenja.<br>
 * Talk with Hunmir (203633).<br>
 * Search the Lepharist Escort Wagon (700135) on the path to the Grave Robbers Den.<br>
 * Report the result to Nokir (203631).<br>
 *
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2014ScoutitOut extends QuestHandler {

	private final static int questId = 2014;

	public _2014ScoutitOut() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203606, 700136, 203633, 203631 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerGetingItem(182203015, questId);
		qe.registerQuestNpc(700135).addOnKillEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203606: { // Olenja
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 2) {
								if (player.getInventory().getItemCountByItemId(182203015) == 0) {
									return sendQuestDialog(env, 1438);
								}
								else {
									return sendQuestDialog(env, 1352);
								}
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 2, 3, 0, 0, 182203015, 1); // 3
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 700136: { // Suspicious document
					if (dialog == DialogAction.USE_OBJECT && var == 1) {
						if (!player.getInventory().isFullSpecialCube()) {
							return true; // loot
						}
					}
					break;
				}
				case 203633: { // Hunmir
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 3, 4); // 4
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203631) {
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
		return defaultOnGetItemEvent(env, 1, 2, false); // 2
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 700135, 4, true); // reward
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2200, true);
	}
}
