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
import org.typezero.gameserver.services.QuestService;

/**
 * @author vlog
 */
public class _1094ProjectDrakanhammer extends QuestHandler {

	private final static int questId = 1094;

	public _1094ProjectDrakanhammer() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npc_ids = { 203834, 798155, 700411, 730153 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 1093);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1091, 1093 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (var) {
				case 0: {
					if (targetId == 203834) { // Nestor
						switch (dialog) {
							case QUEST_SELECT: {
								return sendQuestDialog(env, 1011);
							}
							case SETPRO1: {
								return defaultCloseDialog(env, 0, 1); // 1
							}
						}
					}
				}
				case 1: {
					if (targetId == 798155) { // Atropos
						switch (dialog) {
							case QUEST_SELECT: {
								return sendQuestDialog(env, 1352);
							}
							case SELECT_ACTION_1353: {
								playQuestMovie(env, 367);
								break;
							}
							case SETPRO2: {
								return defaultCloseDialog(env, 1, 2); // 2
							}
						}
					}
				}
				case 2: {
					if (targetId == 700411) { // Research Diary
						if (dialog == DialogAction.USE_OBJECT) {
							if (giveQuestItem(env, 182208017, 1)) {
								closeDialogWindow(env);
								changeQuestStep(env, 2, 3, false); // 3
								return true;
							}
						}
					}
				}
				case 3: {
					if (targetId == 730153) { // Assistant's Journal
						if (dialog == DialogAction.USE_OBJECT) {
							QuestService.collectItemCheck(env, true);
							removeQuestItem(env, 182208017, 1);
							qs.setQuestVar(4); // 4
							qs.setStatus(QuestStatus.REWARD); // reward
							updateQuestStatus(env);
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203834) { // Nestor
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
