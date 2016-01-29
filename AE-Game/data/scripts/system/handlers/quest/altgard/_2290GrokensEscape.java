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
 * Escort Groken (203608) to the sailboat (700178). Talk with Manir (203607).
 *
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2290GrokensEscape extends QuestHandler {

	private final static int questId = 2290;

	public _2290GrokensEscape() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203608).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(203608).addOnTalkEvent(questId);
		qe.registerQuestNpc(700178).addOnTalkEvent(questId);
		qe.registerQuestNpc(203607).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				if (env.getDialogId() == DialogAction.ASK_QUEST_ACCEPT.id())
					return sendQuestDialog(env, 4);
				if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id())
					return sendQuestDialog(env, 1003);
				if (env.getDialogId() == DialogAction.QUEST_REFUSE_1.id())
					return sendQuestDialog(env, 1004);
				if (env.getDialogId() == DialogAction.FINISH_DIALOG.id())
					return sendQuestSelectionDialog(env);
				if (env.getDialogId() == DialogAction.SELECT_ACTION_1012.id()) {
					if (QuestService.startQuest(env)) {
						return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 700178, 0, 1); // 1
					}
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == DialogAction.QUEST_SELECT && qs.getQuestVarById(0) == 0) {
					return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 700178, 0, 1); // 1
				}
			}
			else if (targetId == 203607) { // Groken
				if (env.getDialog() == DialogAction.QUEST_SELECT && qs.getQuestVarById(0) == 3) {
					return sendQuestDialog(env, 1693);
				}
				else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, 3, 3, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203607) { // Manir
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 5);
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
		return defaultFollowEndEvent(env, 1, 3, false, 69);
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 0, false); // 0
	}
}
