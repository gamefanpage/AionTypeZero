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

package quest.theobomos;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Cheatkiller
 *
 */
public class _3090InSearchOfPippiThePorgus extends QuestHandler {

	private final static int questId = 3090;

	public _3090InSearchOfPippiThePorgus() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(798182).addOnQuestStart(questId);
		qe.registerQuestNpc(798182).addOnTalkEvent(questId);
		qe.registerQuestNpc(798193).addOnTalkEvent(questId);
		qe.registerQuestNpc(700420).addOnTalkEvent(questId);
		qe.registerQuestNpc(700421).addOnTalkEvent(questId);
		qe.registerQuestNpc(206085).addOnAtDistanceEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798182) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798193) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
					else if(qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (dialog == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1);
				}
				else if (dialog == DialogAction.SETPRO3) {
					if(player.getInventory().getKinah() >= 10000) {
						giveQuestItem(env, 182208050, 1);
						player.getInventory().decreaseKinah(10000);
						return defaultCloseDialog(env, 2, 3);
					}
					else
						return sendQuestDialog(env, 1779);
				}
				else if (dialog == DialogAction.SELECT_ACTION_1779) {
					return sendQuestDialog(env, 1779);
				}
			}
			if (targetId == 700420) {
				int var = qs.getQuestVarById(0);
				int var2 = qs.getQuestVarById(2);
				if (var == 1 && var2 == 0) {
					changeQuestStep(env, 0, 1, false, 2);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(true, 1111007, player.getObjectId(), 0));
					changeStep(qs, env);
					return true;
				}
			}
			if (targetId == 700421) {
				if (dialog == DialogAction.USE_OBJECT) {
					if(qs.getQuestVarById(0) == 3) {
						return sendQuestDialog(env, 2034);
					}
				}
				else if (dialog == DialogAction.SET_SUCCEED) {
					Npc npc = (Npc) env.getVisibleObject();
					npc.getController().scheduleRespawn();
					npc.getController().onDelete();
					removeQuestItem(env, 182208050, 1);
					giveQuestItem(env, 182208051, 1);
					return defaultCloseDialog(env, 3, 3, true, false);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798182) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					default: {
						removeQuestItem(env, 182208051, 1);
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onAtDistanceEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (var == 1 && var1 == 0) {
				changeQuestStep(env, 0, 1, false, 1);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(true, 1111006, player.getObjectId(), 0));
				changeStep(qs, env);
				return true;
			}
  	}
		return false;
	}

	private void changeStep(QuestState qs, QuestEnv env) {
		if(qs.getQuestVarById(1) == 1 && qs.getQuestVarById(2) == 1) {
			qs.setQuestVarById(1, 0);
			qs.setQuestVarById(2, 0);
			qs.setQuestVar(2);
			updateQuestStatus(env);
		}
	}
}
