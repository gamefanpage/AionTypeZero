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

package quest.reshanta;

import java.util.ArrayList;
import java.util.List;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _2759TenaciousGuardian extends QuestHandler {

	private static final int questId = 2759;
	private final List<Integer> killedMobs = new ArrayList<Integer>();

	public _2759TenaciousGuardian() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(264769).addOnQuestStart(questId);
		qe.registerQuestNpc(264769).addOnTalkEvent(questId);
		qe.registerQuestNpc(278588).addOnKillEvent(questId);
		qe.registerQuestNpc(278589).addOnKillEvent(questId);
		qe.registerQuestNpc(278590).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 264769) { // Gudharten
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
			if (targetId == 264769) { // Gudharten
				if (dialog == DialogAction.QUEST_SELECT) {
					if (var == 3) {
						return sendQuestDialog(env, 1352);
					}
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					changeQuestStep(env, 3, 3, true); // reward
					killedMobs.clear();
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 264769) { // Gudharten
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var < 3) {
				switch (targetId) {
					case 278588: {
						if (!killedMobs.contains(278588)) {
							killedMobs.add(278588);
							return defaultOnKillEvent(env, 278588, var, var + 1);
						}
						break;
					}
					case 278589: {
						if (!killedMobs.contains(278589)) {
							killedMobs.add(278589);
							return defaultOnKillEvent(env, 278589, var, var + 1);
						}
						break;
					}
					case 278590: {
						if (!killedMobs.contains(278590)) {
							killedMobs.add(278590);
							return defaultOnKillEvent(env, 278590, var, var + 1);
						}
						break;
					}
				}
			}
		}
		return false;
	}
}
