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

package quest.abyss_entry;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Hellboy, aion4Free
 * @modified Gigi
 * @reworked vlog On 2.5 the quest has dialogs only for one choice (underground arena)
 */
public class _1922DeliveronYourPromises extends QuestHandler {

	private final static int questId = 1922;
	private int choice = 0;

	public _1922DeliveronYourPromises() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
		qe.registerQuestNpc(203901).addOnTalkEvent(questId);
		// qe.registerQuestNpc(210802).addOnKillEvent(questId);
		// qe.registerQuestNpc(210794).addOnKillEvent(questId);
		// qe.registerQuestNpc(210791).addOnKillEvent(questId);
		// qe.registerQuestNpc(210781).addOnKillEvent(questId);
		qe.registerQuestNpc(203764).addOnTalkEvent(questId);
		// qe.registerQuestNpc(700368).addOnTalkEvent(questId);
		// qe.registerQuestNpc(700369).addOnTalkEvent(questId);
		qe.registerQuestNpc(213582).addOnKillEvent(questId);
		qe.registerQuestNpc(213580).addOnKillEvent(questId);
		qe.registerQuestNpc(213581).addOnKillEvent(questId);
		// qe.registerQuestNpc(700264).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203830: { // Fuchsia
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 4) {
								return sendQuestSelectionDialog(env);
							}
						}
						case SETPRO12: {
							choice = 1;
							return defaultCloseDialog(env, 0, 4); // 4
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
						// case SETPRO11:
						// return defaultCloseDialog(env, 0, 1); // 1
						// case SETPRO13:
						// return defaultCloseDialog(env, 0, 9); // 9
						// case CHECK_USER_HAS_QUEST_ITEM:
						// return sendQuestDialog(env, 2375);
						// case SELECT_ACTION_1013:
						// qs.setQuestVar(0);
						// updateQuestStatus(env);
						// return sendQuestDialog(env, 1013);
					}
					break;
				}
				case 203901: { // Telemachus
					switch (env.getDialog()) {
						case USE_OBJECT: {
							// if (var == 1)
							// return sendQuestDialog(env, 1352);
							// else if (var == 2)
							// return sendQuestDialog(env, 3398);
							if (var == 7) {
								return sendQuestDialog(env, 3739);
							}
							// else if (var == 9) {
							// if (QuestService.collectItemCheck(env, true)) {
							// qs.setStatus(QuestStatus.REWARD);
							// updateQuestStatus(env);
							// return sendQuestDialog(env, 7);
							// }
							// else
							// return sendQuestDialog(env, 4080);
							// }
						}
						// case SETPRO2:
						// return defaultCloseDialog(env, 1, 2); // 2
						case SELECT_QUEST_REWARD:
							// if (var == 2) {
							// qs.setStatus(QuestStatus.REWARD);
							// updateQuestStatus(env);
							// return sendQuestDialog(env, 5);
							// }
							if (var == 7) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 6);
							}
					}
					break;
				}
				case 203764: { // Epeios
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 4) {
								return sendQuestDialog(env, 1693);
							}
							else if (qs.getQuestVarById(4) == 10) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO3: {
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310080000);
							InstanceService.registerPlayerWithInstance(newInstance, player);
							TeleportService2.teleportTo(player, 310080000, newInstance.getInstanceId(), 276, 293, 163, (byte) 90);
							changeQuestStep(env, 4, 5, false); // 5
							return closeDialogWindow(env);
						}
						case SETPRO4: {
							qs.setQuestVar(7);
							updateQuestStatus(env);
							return defaultCloseDialog(env, 7, 7); // 7
						}
					}
					break;
				}
				// case 700264: {
				// switch (env.getDialog()) {
				// case USE_OBJECT:
				// if (var == 9) {
				// return true; // loot
				// }
				// }
				// }
				// break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) { // Telemachus
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}

	// @Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 5) {
				int var4 = qs.getQuestVarById(4);
				int[] mobs = { 213580, 213581, 213582 };
				if (var4 < 9) {
					return defaultOnKillEvent(env, mobs, 0, 9, 4); // 4: 1 - 9
				}
				else if (var4 == 9) {
					defaultOnKillEvent(env, mobs, 9, 10, 4); // 4: 10
					QuestService.questTimerEnd(env);
					TeleportService2.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, (byte) 86);
					return true;
				}
			}
		}

		// int targetId = 0;
		// int var = 0;
		// if (env.getVisibleObject() instanceof Npc)
		// targetId = ((Npc) env.getVisibleObject()).getNpcId();
		// switch (targetId) {
		// case 210802:
		// var = qs.getQuestVarById(1);
		// if (var < 3) {
		// qs.setQuestVarById(1, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210794:
		// var = qs.getQuestVarById(1);
		// if (var < 3) {
		// qs.setQuestVarById(1, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210791:
		// var = qs.getQuestVarById(2);
		// if (var < 3) {
		// qs.setQuestVarById(2, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210781:
		// var = qs.getQuestVarById(3);
		// if (var < 3) {
		// qs.setQuestVarById(3, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 213580:
		// case 213581:
		// case 213582:
		// var = qs.getQuestVarById(4);
		// if (var < 9) {
		// qs.setQuestVarById(4, var + 1);
		// updateQuestStatus(env);
		// }
		// else if (var == 9) {
		// QuestService.questTimerEnd(env);
		// // movie
		// TeleportService.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, 86);
		// qs.setQuestVarById(4, 10);
		// updateQuestStatus(env);
		// }
		// break;
		// }
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var4 = qs.getQuestVarById(4);
			if (var4 < 10) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				TeleportService2.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, (byte) 86);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var4 = qs.getQuestVars().getVarById(4);
			if (var == 5 && var4 != 10) {
				if (player.getWorldId() != 310080000) {
					QuestService.questTimerEnd(env);
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
				else {
					QuestService.questTimerStart(env, 240);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1921);
	}
}
