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

package quest.raksang;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;


/**
 * @author Cheatkiller
 *
 */
public class _18703EggOnYourFace extends QuestHandler {

	private final static int questId = 18703;

	public _18703EggOnYourFace() {
		super(questId);
	}

	public void register() {
		qe.registerQuestItem(182212202, questId);
		qe.registerQuestItem(182212203, questId);
		qe.registerQuestNpc(799431).addOnTalkEvent(questId);
		qe.registerQuestNpc(701115).addOnTalkEvent(questId);
		qe.registerQuestNpc(798439).addOnTalkEvent(questId);
		qe.addHandlerSideQuestDrop(questId, 701115, 182212204, 1, 100);
		qe.registerGetingItem(182212204, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 0) {
				if (dialog == DialogAction.QUEST_ACCEPT_1) {
					removeQuestItem(env, 182212202, 1);
					giveQuestItem(env, 182212203, 1);
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799431) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
					else if(qs.getQuestVarById(0) == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (dialog == DialogAction.SETPRO1) {
						return defaultCloseDialog(env, 0, 1);
				}
				else if (dialog == DialogAction.SETPRO3) {
					return defaultCloseDialog(env, 2, 3);
			  }
			}
			else if (targetId == 701115) {
				if(qs.getQuestVarById(0) == 3)
					return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798439) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				removeQuestItem(env, 182212204, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		return defaultOnGetItemEvent(env, 3, 3, true);
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(item.getItemId() == 182212202) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		else
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				if(qs.getQuestVarById(0) == 1) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false));
				}
			}
		return HandlerResult.FAILED;
	}
}
