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
 * @author Mr. Poke, Dune11
 */
public class _1900RingImbuedAether extends QuestHandler {

	private final static int questId = 1900;

	public _1900RingImbuedAether() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203757).addOnQuestStart(questId);
		qe.registerQuestNpc(203757).addOnTalkEvent(questId);
		qe.registerQuestNpc(203739).addOnTalkEvent(questId);
		qe.registerQuestNpc(203766).addOnTalkEvent(questId);
		qe.registerQuestNpc(203797).addOnTalkEvent(questId);
		qe.registerQuestNpc(203795).addOnTalkEvent(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();

		if (sendQuestNoneDialog(env, 203757, 182206003, 1))
			return true;

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		if (env.getTargetId() == 203739) {
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (env.getTargetId() == 203766) {
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == DialogAction.SETPRO2) {
					return defaultCloseDialog(env, 1, 2);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (env.getTargetId() == 203797) {
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 2034);
				else if (env.getDialog() == DialogAction.SETPRO3) {
					return defaultCloseDialog(env, 2, 3);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (env.getTargetId() == 203795) {
			if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 2375);
				else if (env.getDialog() == DialogAction.SETPRO4) {
					return defaultCloseDialog(env, 3, 0, true, false);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (env.getTargetId() == 203830) {
			if (env.getDialog() == DialogAction.USE_OBJECT && qs.getStatus() == QuestStatus.REWARD)
				return sendQuestDialog(env, 2716);
			else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id() && qs.getStatus() != QuestStatus.COMPLETE
				&& qs.getStatus() != QuestStatus.NONE) {
				removeQuestItem(env, 182206003, 1);
				return sendQuestDialog(env, 5);
			}
			else
				return sendQuestEndDialog(env);
		}
		return sendQuestRewardDialog(env, 203830, 0);
	}
}
