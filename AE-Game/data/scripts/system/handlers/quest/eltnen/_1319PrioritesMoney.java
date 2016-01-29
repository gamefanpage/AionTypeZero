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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Xitanium
 */
public class _1319PrioritesMoney extends QuestHandler // NEED FIX ITEM
{

	private final static int questId = 1319;

	public _1319PrioritesMoney() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203908).addOnQuestStart(questId); // Priorite
		qe.registerQuestNpc(203908).addOnTalkEvent(questId); // Priorite
		qe.registerQuestNpc(203923).addOnTalkEvent(questId); // Krato
		qe.registerQuestNpc(203910).addOnTalkEvent(questId); // Hebestis
		qe.registerQuestNpc(203906).addOnTalkEvent(questId); // Benos
		qe.registerQuestNpc(203915).addOnTalkEvent(questId); // Diokles
		qe.registerQuestNpc(203907).addOnTalkEvent(questId); // Tuskeos
		qe.registerQuestNpc(798050).addOnTalkEvent(questId); // Girrinerk
		qe.registerQuestNpc(798049).addOnTalkEvent(questId); // Shaoranyerk
		qe.registerQuestNpc(205240).addOnTalkEvent(questId); // Arnesonerk
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203908) // Priorite
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
		{
			if (env.getDialog() == DialogAction.QUEST_SELECT)
				return sendQuestDialog(env, 4080);
			else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
				qs.setQuestVar(8);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return sendQuestEndDialog(env);
			}
			else
				return sendQuestEndDialog(env);
		}

		else if (targetId == 203923) // Krato
		{

			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == DialogAction.SETPRO1) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}

		}

		else if (targetId == 203910) // Hebestis
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == DialogAction.SETPRO2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 203906) // Benos
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 2034);
				else if (env.getDialog() == DialogAction.SETPRO3) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 203915) // Diokles
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 2375);
				else if (env.getDialog() == DialogAction.SETPRO4) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 203907) // Tuskeos
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 2716);
				else if (env.getDialogId() == DialogAction.SETPRO5.id()) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 798050) // Girrinerk
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 3057);
				else if (env.getDialogId() == DialogAction.SETPRO6.id()) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 798049) // Shaoranranerk
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 3398);
				else if (env.getDialogId() == DialogAction.SETPRO7.id()) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 205240) // Arnesonerk
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 3739);
				else if (env.getDialogId() == DialogAction.SETPRO8.id() && qs.getStatus() != QuestStatus.COMPLETE
					&& qs.getStatus() != QuestStatus.NONE) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		return false;
	}
}
