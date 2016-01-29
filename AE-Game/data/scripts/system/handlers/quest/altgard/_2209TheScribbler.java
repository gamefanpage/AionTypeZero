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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr. Poke
 */
public class _2209TheScribbler extends QuestHandler {

	private final static int questId = 2209;

	public _2209TheScribbler() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203555).addOnQuestStart(questId);
		qe.registerQuestNpc(203555).addOnTalkEvent(questId);
		qe.registerQuestNpc(203562).addOnTalkEvent(questId);
		qe.registerQuestNpc(203592).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			if (targetId == 203555) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203562: {
					if (qs.getQuestVarById(0) == 0) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 1352);
						else if (env.getDialog() == DialogAction.SETPRO1) {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
				}
					break;
				case 203572: {
					if (qs.getQuestVarById(0) == 1) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 1693);
						else if (env.getDialog() == DialogAction.SETPRO2) {
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
				}
					break;
				case 203592: {
					if (qs.getQuestVarById(0) == 2) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 2034);
						else if (env.getDialog() == DialogAction.SETPRO3) {
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
				}
					break;
				case 203555: {
					if (qs.getQuestVarById(0) == 3) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 2375);
						else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestEndDialog(env);
						}
						else
							return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203555)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
