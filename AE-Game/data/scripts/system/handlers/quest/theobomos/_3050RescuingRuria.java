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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Get the antidote (182208035) from Calydon Sorcerer (214304) and bring it to Ruria (798211). Talk with Ruria. Escort
 * Ruria to the place where Melleas (798208) is. Talk with Melleas. Tell Rosina (798190) about Ruria.
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _3050RescuingRuria extends QuestHandler {

	private final static int questId = 3050;

	public _3050RescuingRuria() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798211).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(798211).addOnTalkEvent(questId);
		qe.registerQuestNpc(798208).addOnTalkEvent(questId);
		qe.registerQuestNpc(798190).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798211) { // Ruria
				switch (env.getDialog()) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798211: { // Ruria
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 0) {
								long itemCount = player.getInventory().getItemCountByItemId(182208035);
								if (itemCount >= 1) {
									return sendQuestDialog(env, 1011);
								}
								return sendQuestDialog(env, 1097);
							}
							else if (qs.getQuestVarById(0) == 1) {
								return sendQuestDialog(env, 1013);
						}
							}
						case SELECT_ACTION_1012: {
							removeQuestItem(env, 182208035, 1);
							changeQuestStep(env, 0, 1, false);
							return sendQuestDialog(env, 1012);
						}
						case SETPRO1: {
							playQuestMovie(env, 370);
							return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 798208, 1, 2); // 1
						}
					}
				}
					break;
				case 798208: { // Melleas
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SET_SUCCEED: {
							return defaultCloseDialog(env, 3, 3, true, false);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798190) { // Rosina
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				changeQuestStep(env, 1, 0, false);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 3, false); // 2
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 1, false); // 0
	}
}
