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

package quest.chantra_dredgion;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _3725MyLuckyNumber extends QuestHandler {

	private static final int questId = 3725;

	public _3725MyLuckyNumber() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnDredgionReward(questId);
		qe.registerQuestNpc(799069).addOnQuestStart(questId);
		qe.registerQuestNpc(799069).addOnTalkEvent(questId);
		qe.registerQuestNpc(798928).addOnTalkEvent(questId);
		qe.registerQuestNpc(281866).addOnKillEvent(questId);
		qe.registerQuestNpc(216866).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 799069) { // Yannis
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			if (targetId == 798928) { // Yulia
				if (dialog == DialogAction.QUEST_SELECT) {
					if (var1 == 6 && var2 == 15) {
						return sendQuestDialog(env, 10002);
					}
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798928) { // Yulia
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		int[] mobs = { 281866, 216866 };
		return defaultOnKillEvent(env, mobs, 0, 15, 2); // 2: 0 - 15
	}

	@Override
	public boolean onDredgionRewardEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			if (var1 < 6) {
				changeQuestStep(env, var1, var1 + 1, false, 1); // 1: 0 - 6
				return true;
			}
		}
		return false;
	}
}
