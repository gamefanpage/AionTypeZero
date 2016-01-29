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

package quest.ascension;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @author MrPoke + Dune11
 * @reworked vlog
 */
public class _1007ACeremonyinSanctum extends QuestHandler {

	private final static int questId = 1007;

	public _1007ACeremonyinSanctum() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 790001, 203725, 203752, 203758, 203759, 203760, 203761, 801212, 801213 };
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVars().getQuestVars();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 790001: { // Pernos
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							changeQuestStep(env, 0, 1, false); // 1
							TeleportService2.teleportTo(player, 110010000, 1313f, 1512f, 568f);
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 203725: { // Leah
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SELECT_ACTION_1353: {
							return playQuestMovie(env, 92);
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 203752: { // Jucleas
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SELECT_ACTION_1694: {
							return playQuestMovie(env, 91);
						}
						case SETPRO3: {
							if (var == 2) {
								PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
								switch (playerClass) {
									case WARRIOR: {
										qs.setQuestVar(10);
										break;
									}
									case SCOUT: {
										qs.setQuestVar(20);
										break;
									}
									case MAGE: {
										qs.setQuestVar(30);
										break;
									}
									case PRIEST: {
										qs.setQuestVar(40);
										break;
									}
									case ENGINEER: {
										qs.setQuestVar(50);
										break;
									}
									case ARTIST: {
										qs.setQuestVar(60);
										break;
									}
								}
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
							}
						}
						break;
					}
				}
				break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203758 && var == 10) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2034);
					case 1009:
						return sendQuestDialog(env, 5);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 0)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 203759 && var == 20) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2375);
					case 1009:
						return sendQuestDialog(env, 6);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 1)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 203760 && var == 30) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2716);
					case 1009:
						return sendQuestDialog(env, 7);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 2)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 203761 && var == 40) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3057);
					case 1009:
						return sendQuestDialog(env, 8);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 3)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 801212 && var == 50) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3398);
					case 1009:
						return sendQuestDialog(env, 45);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 4)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 801213 && var == 60) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3739);
					case 1009:
						return sendQuestDialog(env, 46);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 5)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1006);
	}
}
