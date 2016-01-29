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

package quest.inggison;

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
public class _11006TestingTheWaters extends QuestHandler {

	private final static int questId = 11006;

	public _11006TestingTheWaters() {
		super(questId);
	}

	public void register() {
		qe.registerQuestItem(182206704, questId);
		qe.registerQuestItem(182206705, questId);
		qe.registerQuestNpc(798940).addOnQuestStart(questId);
		qe.registerQuestNpc(798940).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798940) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env, 182206704, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798940) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 1)
						return sendQuestDialog(env, 1352);
				}
				else if (dialog == DialogAction.SETPRO2) {
					giveQuestItem(env, 182206705, 1);
					removeQuestItem(env, 182206706, 1);
					return defaultCloseDialog(env, 1, 2);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798940) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				removeQuestItem(env, 182206707, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null && qs.getStatus() == QuestStatus.START) {
			if (item.getItemId() == 182206704) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false, 182206706, 1));
			}
			else if (item.getItemId() == 182206705) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 2, true, 182206707, 1));
			}
		}
		return HandlerResult.FAILED;
	}
}
