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

package quest.theobomos;

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
public class _3088InCiderTrading extends QuestHandler {

	private final static int questId = 3088;

	public _3088InCiderTrading() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(798202).addOnQuestStart(questId);
		qe.registerQuestNpc(798202).addOnTalkEvent(questId);
		qe.registerQuestNpc(798201).addOnTalkEvent(questId);
		qe.registerQuestNpc(798204).addOnTalkEvent(questId);
		qe.registerQuestNpc(798132).addOnTalkEvent(questId);
		qe.registerQuestNpc(798166).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798202) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798202) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				else if (dialog == DialogAction.SELECT_ACTION_1012) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 1)
						return sendQuestDialog(env, 1012);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == DialogAction.SELECT_ACTION_1097) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 10)
						return sendQuestDialog(env, 1097);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == DialogAction.SELECT_ACTION_1182) {
					if(player.getInventory().getItemCountByItemId(160003020) >= 100)
						return sendQuestDialog(env, 1182);
					else
						return sendQuestDialog(env, 1267);
				}
				else if (dialog == DialogAction.SELECT_ACTION_1011) {
					return sendQuestDialog(env, 1011);
				}
				else if (dialog == DialogAction.SETPRO1) {
					player.getInventory().decreaseByItemId(160003020, 1);
					return sendQuestDialog(env, 1352);
				}
				else if (dialog == DialogAction.SETPRO2) {
					return defaultCloseDialog(env, 0, 2);
				}
				else if (dialog == DialogAction.SETPRO3) {
					player.getInventory().decreaseByItemId(160003020, 10);
					return sendQuestDialog(env, 2034);
				}
				else if (dialog == DialogAction.SETPRO4) {
					return defaultCloseDialog(env, 0, 4);
				}
				else if (dialog == DialogAction.SETPRO7) {
					player.getInventory().decreaseByItemId(160003020, 100);
					return sendQuestDialog(env, 3398);
				}
				else if (dialog == DialogAction.SETPRO8) {
					return defaultCloseDialog(env, 0, 8);
				}
			}
			if (targetId == 798201) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 1693);
					}
					if(qs.getQuestVarById(0) == 5) {
						return sendQuestDialog(env, 2716);
					}
				}
				else if (dialog == DialogAction.SETPRO6) {
					return defaultCloseDialog(env, 5, 6);
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, 2, 2, true, true);
				}
			}
			if (targetId == 798204) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 4) {
						return sendQuestDialog(env, 2375);
					}
				}
				else if (dialog == DialogAction.SETPRO5) {
					return defaultCloseDialog(env, 4, 5);
				}
			}
			if (targetId == 798132) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 6) {
						return sendQuestDialog(env, 3057);
					}
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, 6, 6, true, true);
				}
			}
			if (targetId == 798166) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 8) {
						if(player.getInventory().getItemCountByItemId(182208064) >= 1)
							return sendQuestDialog(env, 3739);
					}
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					player.getInventory().decreaseByItemId(182208064, 1);
					return defaultCloseDialog(env, 8, 8, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798201 && qs.getQuestVarById(0) == 2) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			}
			else if (targetId == 798132 && qs.getQuestVarById(0) == 6) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 1);
			}
			else if (targetId == 798166 && qs.getQuestVarById(0) == 8) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 2);
			}
		}
		return false;
	}
}
