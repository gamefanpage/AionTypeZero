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

package quest.morheim;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr.Poke remod by Nephis and quest helper team
 */
public class _2484OurManInElysea extends QuestHandler {

	private final static int questId = 2484;

	public _2484OurManInElysea() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204407).addOnQuestStart(questId);
		qe.registerQuestNpc(204407).addOnTalkEvent(questId);
		qe.registerQuestNpc(700267).addOnTalkEvent(questId);
		qe.registerQuestNpc(203331).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204407) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					if (giveQuestItem(env, 182204205, 1))
						return sendQuestStartDialog(env);
					else
						return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 700267:
					if (qs.getQuestVarById(0) == 0 && env.getDialog() == DialogAction.USE_OBJECT) {
						qs.setQuestVarById(0, 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182204205, 1);
					}
				case 203331: {
					if (qs.getQuestVarById(0) == 1) {
						if (env.getDialogId() == DialogAction.SELECTED_QUEST_NOREWARD.id())
							return sendQuestDialog(env, 5);
						else if (env.getDialog() == DialogAction.QUEST_SELECT) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203331)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
