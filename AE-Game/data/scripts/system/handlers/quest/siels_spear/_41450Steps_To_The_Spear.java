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

package quest.siels_spear;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Cheatkiller
 *
 */
public class _41450Steps_To_The_Spear extends QuestHandler {
	private final static int questId = 41450;

	private final static int[] npc_ids = {205798, 205799, 205800, 205801, 205579 , 730527, 800280 , 800298 };

	public _41450Steps_To_The_Spear(){
		super(questId);
	}
		@Override
		public void register() {
			qe.registerOnDie(questId);
			qe.registerOnLogOut(questId);
			qe.registerOnQuestTimerEnd(questId);
			qe.registerQuestNpc(205579).addOnQuestStart(questId);
			for(int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}

		@Override
		public boolean onDialogEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			DialogAction dialog = env.getDialog();
			int targetId = env.getTargetId();

			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (targetId == 205579) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 4762);
					}
					else if(dialog == DialogAction.QUEST_ACCEPT_SIMPLE) {
						QuestService.questTimerStart(env, 780);
						return sendQuestStartDialog(env);
					}
					else {
						return sendQuestStartDialog(env);
					}
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			 if (targetId == 205798) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else if (dialog == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1);
				}
			 }
			 if (targetId == 205799 && var == 1) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 1352);
					}
					else if (dialog == DialogAction.SETPRO2) {
						return defaultCloseDialog(env, 1, 2);
					}
				 }
			 if (targetId == 205800 && var == 2) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 1693);
					}
					else if (dialog == DialogAction.SETPRO3) {
						return defaultCloseDialog(env, 2, 3);
					}
				 }
			 if (targetId == 205801 && var == 3) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 2034);
					}
					else if (dialog == DialogAction.SETPRO4) {
						return defaultCloseDialog(env, 3, 4);
					}
				 }
			 if (targetId == 205579 && var == 4) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 2375);
					}
					else if (dialog == DialogAction.SETPRO5) {
						return defaultCloseDialog(env, 4, 5);
					}
				 }
			 if (targetId == 205579 && var == 5) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 2716);
					}
					else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM_SIMPLE) {
						return checkQuestItems(env, 5, 6, true, 10002, 10001);
					}
				 }
			}
			 else if (qs.getStatus() == QuestStatus.REWARD) {
					if (targetId == 205579) {
						if (dialog == DialogAction.SELECT_QUEST_REWARD) {
							QuestService.questTimerEnd(env);
							return sendQuestDialog(env, 5);
						}
						else {
							return sendQuestEndDialog(env);
						}
					}
				}
			return false;
		}
		@Override
		public boolean onQuestTimerEndEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 1) {
					qs.setStatus(QuestStatus.NONE);
					qs.setQuestVar(0);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
			return false;
		}
		@Override
		public boolean onDieEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var >= 0) {
					qs.setStatus(QuestStatus.NONE);
					qs.setQuestVar(0);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean onLogOutEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var >= 0) {
					qs.setStatus(QuestStatus.NONE);
					qs.setQuestVar(0);
					updateQuestStatus(env);
					return true;
				}
			}
			return false;
		}
}






