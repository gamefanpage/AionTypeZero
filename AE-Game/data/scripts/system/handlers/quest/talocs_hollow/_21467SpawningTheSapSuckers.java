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

package quest.talocs_hollow;

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
public class _21467SpawningTheSapSuckers extends QuestHandler {

	private final static int questId = 21467;

	public _21467SpawningTheSapSuckers() {
		super(questId);
	}
		@Override
		public void register() {
			qe.registerOnDie(questId);
			qe.registerOnLogOut(questId);
			qe.registerOnQuestTimerEnd(questId);
			qe.registerQuestNpc(799527).addOnQuestStart(questId);
			qe.registerQuestNpc(799503).addOnTalkEvent(questId);
			qe.registerQuestNpc(215480).addOnKillEvent(questId);
		}

		@Override
		public boolean onDialogEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			DialogAction dialog = env.getDialog();
			int targetId = env.getTargetId();

			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				if (targetId == 799527) {
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 4762);
					}
					else if(dialog == DialogAction.QUEST_ACCEPT_1) {
						QuestService.questTimerStart(env, 480);
						return sendQuestStartDialog(env);
					}
					else {
						return sendQuestStartDialog(env);
					}
				}
			}
		  else if (qs.getStatus() == QuestStatus.REWARD) {
		  	int var = qs.getQuestVarById(0);
		  	int rewInd = 0;
		  	if(var == 1)
		  		rewInd = 1;
		  	else if(var == 2)
		  		rewInd = 2;
		  	else if(var == 3)
		  		rewInd = 3;
		  	if (targetId == 799503) {
					if (dialog == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 10002);
					}
					else {
						return sendQuestEndDialog(env, rewInd);
					}
				}
			}
		return false;
	}

		@Override
		public boolean onKillEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int targetId = env.getTargetId();
				int var = qs.getQuestVarById(0);
				if(targetId == 215480) {
					QuestService.questTimerEnd(env);
					changeQuestStep(env, var, var, true);
				  return true;
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
				if(var == 0) {
					QuestService.questTimerStart(env, 240);
					changeQuestStep(env, 0, 1, false);
				}
				else if(var == 1) {
					QuestService.questTimerStart(env, 240);
					changeQuestStep(env, 1, 2, false);
				}
				else if(var == 3) {
					changeQuestStep(env, 2, 3, false);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean onDieEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				qs.setStatus(QuestStatus.NONE);
				qs.setQuestVar(0);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
			return false;
		}

		@Override
		public boolean onLogOutEvent(QuestEnv env) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				qs.setStatus(QuestStatus.NONE);
				qs.setQuestVar(0);
				updateQuestStatus(env);
			}
			return false;
		}
}






