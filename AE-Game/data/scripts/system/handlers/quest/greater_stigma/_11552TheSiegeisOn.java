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

package quest.greater_stigma;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author zhkchi
 *
 */
public class _11552TheSiegeisOn extends QuestHandler {

	private final static int questId = 11552;

	public _11552TheSiegeisOn() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205531).addOnQuestStart(questId);
		qe.registerQuestNpc(205531).addOnTalkEvent(questId);
		qe.registerQuestNpc(259014).addOnAttackEvent(questId);
		qe.registerQuestNpc(259214).addOnAttackEvent(questId);
		qe.registerQuestNpc(259414).addOnAttackEvent(questId);
		qe.registerQuestNpc(259614).addOnAttackEvent(questId);
	}

	@Override
	public boolean onKillInWorldEvent(QuestEnv env) {
		return defaultOnKillRankedEvent(env, 0, 10, false); // reward
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if (env.getTargetId() == 205531) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					case QUEST_ACCEPT_SIMPLE:
						return sendQuestStartDialog(env);
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 0 && env.getTargetId() == 259014) {
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 1 && env.getTargetId() == 259214){
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 2 && env.getTargetId() == 259414){
				changeQuestStep(env, var, var + 1, false);
				return true;
			}else if(var == 3 && env.getTargetId() == 259614){
				changeQuestStep(env, var, var + 1, true);
				return true;
			}
		}
		return false;
	}
}
