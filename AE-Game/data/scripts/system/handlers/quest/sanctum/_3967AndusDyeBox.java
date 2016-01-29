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

package quest.sanctum;

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
 * @author Rolandas
 */
public class _3967AndusDyeBox extends QuestHandler {

	private final static int questId = 3967;

	public _3967AndusDyeBox() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798391).addOnQuestStart(questId);// Andu
		qe.registerQuestNpc(798309).addOnTalkEvent(questId);// Arenzes
		qe.registerQuestNpc(798391).addOnTalkEvent(questId);// Andu
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;

		QuestState qs2 = player.getQuestStateList().getQuestState(3966);
		if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
			return false;

		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (targetId == 798391)// Andu
		{
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (targetId == 798309)// Arenzes
		{
			if (qs.getStatus() == QuestStatus.START && var == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == DialogAction.SETPRO1) {
					if (giveQuestItem(env, 182206122, 1)) {
						qs.setQuestVar(++var);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (targetId == 798391 && qs.getStatus() == QuestStatus.REWARD)// Andu
		{
			if (env.getDialog() == DialogAction.USE_OBJECT)
				return sendQuestDialog(env, 2375);
			else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
				removeQuestItem(env, 182206122, 1);
				return sendQuestEndDialog(env);
			}
			else
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
