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

package quest.empyrean_crucible;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Cheatkiller
 *
 */
public class _28213TheColiseumSecret extends QuestHandler {

	private final static int questId = 28213;

	public _28213TheColiseumSecret() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestItem(182212223, questId);
		qe.registerQuestNpc(205986).addOnQuestStart(questId);
		qe.registerQuestNpc(205986).addOnTalkEvent(questId);
		qe.registerQuestNpc(205320).addOnTalkEvent(questId);
		qe.registerQuestNpc(798804).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205986) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					giveQuestItem(env, 182212222, 1);
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 205320) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1352);
					}
					case SELECT_ACTION_1353: {
						removeQuestItem(env, 182212222, 1);
						giveQuestItem(env, 182212223, 1);
						return sendQuestDialog(env, 1353);
					}
					case SETPRO1: {
						return defaultCloseDialog(env, 0, 1);
					}
				}
			}
			else if (targetId == 798804) {
				switch (dialog) {
					case QUEST_SELECT: {
						if(var == 1){
						return sendQuestDialog(env, 1693);
						}
						else if(var == 3){
							return sendQuestDialog(env, 2375);
						}
					}
					case SETPRO2: {
						return defaultCloseDialog(env, 1, 2);
					}
					case SELECT_QUEST_REWARD: {
						changeQuestStep(env, 3, 3, true);
						return sendQuestDialog(env, 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798804) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
			removeQuestItem(env, 182212223, 1);
				changeQuestStep(env, 2, 3, false);
				return HandlerResult.SUCCESS;
		}
		return HandlerResult.FAILED;
	}
}
