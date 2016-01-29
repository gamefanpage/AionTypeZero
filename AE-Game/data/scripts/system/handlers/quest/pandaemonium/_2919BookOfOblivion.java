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

package quest.pandaemonium;

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
public class _2919BookOfOblivion extends QuestHandler {

	private final static int questId = 2919;

	public _2919BookOfOblivion() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(204206).addOnQuestStart(questId);
		qe.registerQuestNpc(204206).addOnTalkEvent(questId);
		qe.registerQuestNpc(204215).addOnTalkEvent(questId);
		qe.registerQuestNpc(204192).addOnTalkEvent(questId);
		qe.registerQuestNpc(700212).addOnTalkEvent(questId);
		qe.registerQuestNpc(204224).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204206) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204215) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0)
						return sendQuestDialog(env, 1352);
				}
				else if (dialog == DialogAction.SETPRO2) {
					return defaultCloseDialog(env, 0, 1);
				}
			}
			else if (targetId == 204192) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 1)
						return sendQuestDialog(env, 1693);
				}
				else if (dialog == DialogAction.SETPRO3) {
					return defaultCloseDialog(env, 1, 2);
				}
			}
			else if (targetId == 700212) {
				if (dialog == DialogAction.USE_OBJECT) {
					if(qs.getQuestVarById(0) == 2)
						return sendQuestDialog(env, 2034);
					else if(qs.getQuestVarById(0) == 6)
						return sendQuestDialog(env, 3057);
				}
				else if (dialog == DialogAction.SETPRO4) {
				  return defaultCloseDialog(env, 2, 3);
				}
				else if (dialog == DialogAction.SETPRO7) {
					giveQuestItem(env, 182207013, 1);
				  return defaultCloseDialog(env, 6, 7);
				}
			}
			else if (targetId == 204206) {
				if(qs.getQuestVarById(0) == 7) {
					if (dialog == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 3398);
					}
				}
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 3)
						return sendQuestDialog(env, 2375);
				}
				else if (dialog == DialogAction.SETPRO5) {
					return defaultCloseDialog(env, 3, 4);
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					removeQuestItem(env, 182207013, 1);
					return defaultCloseDialog(env, 7, 7, true, true);
				}
			}
			else if (targetId == 204224) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 4)
						return sendQuestDialog(env, 2716);
				}
				else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
					return checkQuestItems(env, 4, 6, false, 2802, 2717);
				}
				else if (dialog == DialogAction.SETPRO6) {
					return closeDialogWindow(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204206) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
