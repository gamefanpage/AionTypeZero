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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Atomics
 * @modified Gigi
 */
public class _2288PutYourMoneyWhereYourMouthIs extends QuestHandler {

	private final static int questId = 2288;

	public _2288PutYourMoneyWhereYourMouthIs() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203621).addOnQuestStart(questId);
		qe.registerQuestNpc(203621).addOnTalkEvent(questId);
		qe.registerQuestNpc(210564).addOnKillEvent(questId);
		qe.registerQuestNpc(210584).addOnKillEvent(questId);
		qe.registerQuestNpc(210581).addOnKillEvent(questId);
		qe.registerQuestNpc(201047).addOnKillEvent(questId);
		qe.registerQuestNpc(210436).addOnKillEvent(questId);
		qe.registerQuestNpc(210437).addOnKillEvent(questId);
		qe.registerQuestNpc(210440).addOnKillEvent(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() != QuestStatus.START)
			return false;
		if (targetId == 210564 || targetId == 210584 || targetId == 210581 || targetId == 210436 || targetId == 201047
			|| targetId == 210437 || targetId == 210440) {
			if (var > 0 && var < 3) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				return true;
			}
			else if (var == 3) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
				QuestService.questTimerEnd(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203621) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.START) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					if (qs.getQuestVarById(0) == 4) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
					else if (qs.getQuestVarById(0) == 0)
						return sendQuestDialog(env, 1003);
					else
						return sendQuestSelectionDialog(env);
				}
				else if (env.getDialog() == DialogAction.SETPRO1) {
					QuestService.questTimerStart(env, 600);
					qs.setQuestVarById(0, 1);
					return sendQuestSelectionDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD)
				return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (var > 0 && var < 3) {
				qs.setQuestVarById(0, 0);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}
