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

package quest.altgard;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _2289RampagingMosbears extends QuestHandler {

	private static final int questId = 2289;

	public _2289RampagingMosbears() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203616).addOnQuestStart(questId);
		qe.registerQuestNpc(203616).addOnTalkEvent(questId);
		qe.registerQuestNpc(203618).addOnTalkEvent(questId);
		qe.registerQuestNpc(210564).addOnKillEvent(questId);
		qe.registerQuestNpc(210584).addOnKillEvent(questId);
		qe.registerQuestNpc(210442).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203616) { // Gefion
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203616: { // Gefion
					switch (dialog) {
						case SETPRO1: {
							return sendQuestSelectionDialog(env);
						}
						case QUEST_SELECT: {
							if (var == 5) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1354: {
							playQuestMovie(env, 62);
							return sendQuestDialog(env, 1354);
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 7, 7, true, 5, 2120); // reward
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 7, 7);
						}
					}
					break;
				}
				case 203618: { // Skanin
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 6) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 6, 7, 182203017, 1, 0, 0); // 7
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203616) { // Gefion
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		int[] mobs = { 210564, 210584 };
		return defaultOnKillEvent(env, mobs, 0, 5); // 1, 2, 3, 4, 5
	}
}
