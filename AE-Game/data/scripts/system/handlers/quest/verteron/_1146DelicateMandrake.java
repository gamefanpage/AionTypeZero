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
 * @author Mr.Poke, Dune11
 * @modified xTz, Undertrey
 * @reworked vlog
 */
public class _1146DelicateMandrake extends QuestHandler {

	private final static int questId = 1146;

	public _1146DelicateMandrake() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203123).addOnQuestStart(questId);
		qe.registerQuestNpc(203123).addOnTalkEvent(questId);
		qe.registerQuestNpc(203139).addOnTalkEvent(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203123) { // Gano
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1011);
					}
					case ASK_QUEST_ACCEPT: {
						return sendQuestDialog(env, 4);
					}
					case QUEST_ACCEPT_1: {
						if (giveQuestItem(env, 182200519, 1)) {
							if (QuestService.startQuest(env)) {
								QuestService.questTimerStart(env, 120);
								return sendQuestDialog(env, 1003);
							}
						}
					}
					case QUEST_REFUSE_1: {
						return sendQuestDialog(env, 1004);
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203139) { // Krodis
				switch (dialog) {
					case USE_OBJECT: {
						if (player.getInventory().getItemCountByItemId(182200519) > 0) {
							return sendQuestDialog(env, 2375);
						}
					}
					case SELECT_QUEST_REWARD: {
						removeQuestItem(env, 182200519, 1);
						changeQuestStep(env, 0, 0, true);
						QuestService.questTimerEnd(env);
						return sendQuestDialog(env, 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203139) { // Krodis
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182200519, 1);
			QuestService.abandonQuest(player, questId);
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
}
