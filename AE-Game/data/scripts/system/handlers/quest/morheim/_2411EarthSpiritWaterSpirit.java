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
public class _2411EarthSpiritWaterSpirit extends QuestHandler {

	private final static int questId = 2411;

	int rewardIndex;

	public _2411EarthSpiritWaterSpirit() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(204369).addOnQuestStart(questId);
		qe.registerQuestNpc(204366).addOnTalkEvent(questId);
		qe.registerQuestNpc(204364).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204369) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204369) {
				if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 1003);
				}
			else if (dialog == DialogAction.SELECT_ACTION_1012) {
				return sendQuestDialog(env, 1012);
			}
			else if (dialog == DialogAction.SELECT_ACTION_1097) {
				return sendQuestDialog(env, 1097);
			}
			else if(dialog == DialogAction.SETPRO10) {
				changeQuestStep(env, 0, 1, false);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return closeDialogWindow(env);
			}
			else if(dialog == DialogAction.SETPRO20) {
				rewardIndex = 1;
				changeQuestStep(env, 0, 2, false);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return closeDialogWindow(env);
			}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204366 && qs.getQuestVarById(0) == 1) {
				if (dialog == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 1352);
					}
				}
			else if(targetId == 204364 && qs.getQuestVarById(0) == 2) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 1693);
				}
			}
			return sendQuestEndDialog(env, rewardIndex);
		}
	 return false;
	}
}
