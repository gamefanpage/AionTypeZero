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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Cheatkiller
 *
 */
public class _1908UlaguruSpeaks extends QuestHandler {

	private final static int questId = 1908;

	public _1908UlaguruSpeaks() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(203864).addOnQuestStart(questId);
		qe.registerQuestNpc(203864).addOnTalkEvent(questId);
		qe.registerQuestNpc(204120).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203864) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203890) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1352);
					}
				}
				else if (dialog == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1);
				}
			}
			else if (targetId == 203864) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 1) {
						return sendQuestDialog(env, 2375);
					}
				}
				else if (dialog == DialogAction.SETPRO21) {
					qs.setQuestVar(21);
					return sendQuestDialog(env, 2376);
				}
				else if (dialog == DialogAction.SETPRO22) {
					qs.setQuestVar(22);
					return sendQuestDialog(env, 2461);
				}
				else if (dialog == DialogAction.SETPRO23) {
					qs.setQuestVar(23);
					return sendQuestDialog(env, 2546);
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, qs.getQuestVarById(0), qs.getQuestVarById(0), true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203864) {
				if(qs.getQuestVarById(0) == 21)
					return sendQuestEndDialog(env);
				else if(qs.getQuestVarById(0) == 22)
					return sendQuestEndDialog(env, 1);
				else if(qs.getQuestVarById(0) == 23)
					return sendQuestEndDialog(env, 2);
			}
		}
		return false;
	}
}
