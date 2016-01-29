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
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @author Xitanium
 */
public class _1430ATeleportationExperiment extends QuestHandler {

	private final static int questId = 1430;

	public _1430ATeleportationExperiment() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203919).addOnQuestStart(questId); // Onesimus
		qe.registerQuestNpc(203919).addOnTalkEvent(questId); // Onesimus
		qe.registerQuestNpc(203337).addOnTalkEvent(questId); // Sonirim
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203919) // Onesimus
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		else if (targetId == 203337) // Sonirim
		{

			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else if (env.getDialog() == DialogAction.SETPRO1) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					qs.setStatus(QuestStatus.REWARD);
					TeleportService2.teleportTo(player, 220020000, 1, 638, 2337, 425, (byte) 20);
				}
				else
					return sendQuestStartDialog(env);
			}

			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4080);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					qs.setQuestVar(2);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
				else
					return sendQuestEndDialog(env);
			}

		}
		return false;
	}
}
