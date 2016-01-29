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

package quest.verteron;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Rolandas
 * @reworked vlog
 */
public class _1170HeadlessStoneStatue extends QuestHandler {

	private final static int questId = 1170;

	public _1170HeadlessStoneStatue() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(730000).addOnQuestStart(questId);
		qe.registerQuestNpc(730000).addOnTalkEvent(questId);
		qe.registerQuestNpc(700033).addOnTalkEvent(questId);
		qe.registerOnMovieEndQuest(16, questId);
		qe.registerGetingItem(182200504, questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 730000) { // Headless Stone Statue
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 700033) { // Head of Stone Statue
				if (dialog == DialogAction.USE_OBJECT) {
					return giveQuestItem(env, 182200504, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 730000) { // Headless Stone Statue
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				}
				else if (dialog == DialogAction.SETPRO1) {
					playQuestMovie(env, 16);
					return closeDialogWindow(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			changeQuestStep(env, 0, 0, true); // reward
			return true;
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (movieId == 16) {
				removeQuestItem(env, 182200504, 1);
				return QuestService.finishQuest(env);
			}
		}
		return false;
	}
}
