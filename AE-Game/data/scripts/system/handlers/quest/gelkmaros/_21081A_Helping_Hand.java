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

package quest.gelkmaros;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _21081A_Helping_Hand extends QuestHandler {

	private final static int questId = 21081;

	public _21081A_Helping_Hand() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799225).addOnQuestStart(questId); // Richelle
		qe.registerQuestNpc(799225).addOnTalkEvent(questId); // Richelle
		qe.registerQuestNpc(799332).addOnTalkEvent(questId); // Agovard
		qe.registerQuestNpc(799217).addOnTalkEvent(questId); // Renato
		qe.registerQuestNpc(799202).addOnTalkEvent(questId); // Ipses
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799225) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					default: {
						return sendQuestStartDialog(env, 182214017, 1);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799332: // Brontes
				{
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1353);
						}
						case SELECT_ACTION_1353: {
							return sendQuestDialog(env, 1353);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
				}
				case 799217: // Pilipides
				{
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1693);
						}
						case SELECT_ACTION_1694: {
							return sendQuestDialog(env, 1694);
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2);
						}
					}
				}
				case 799202: // Drenia
				{
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 2375);
						}
						case SELECT_QUEST_REWARD: {
							return defaultCloseDialog(env, 2, 3, true, true);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799202) // Drenia
			{
				switch (env.getDialogId()) {
					case 1009: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
