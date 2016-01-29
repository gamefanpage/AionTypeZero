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

package quest.heiron;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * Find Litonos (204616) (bring him the Berone's Necklace (182201780)). Talk with Litonos. Take Litonos to Berone
 * (204589). Talk with Berone.
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _1562CrossedDestiny extends QuestHandler {

	private final static int questId = 1562;

	public _1562CrossedDestiny() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204589).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(204589).addOnTalkEvent(questId);
		qe.registerQuestNpc(204616).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204589) { // Berone
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				if (env.getDialogId() == DialogAction.ASK_QUEST_ACCEPT.id())
					return sendQuestDialog(env, 4);
				if (env.getDialogId() == DialogAction.QUEST_REFUSE_1.id())
					return sendQuestDialog(env, 1004);
				if (env.getDialog() == DialogAction.QUEST_ACCEPT_1) {
					QuestService.startQuest(env);
					return defaultCloseDialog(env, 0, 1, false, false, 182201780, 1, 0, 0);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204616: { // Litonos
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (qs.getQuestVarById(0) == 1 && player.getInventory().getItemCountByItemId(182201780) == 1)
								return sendQuestDialog(env, 1352);
							else
								return sendQuestDialog(env, 1438);
						case FINISH_DIALOG:
							return defaultCloseDialog(env, 0, 0);
						case SETPRO2:
							if (qs.getQuestVarById(0) == 1) {
								defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 204589, 0, 0);
								return defaultCloseDialog(env, 1, 2, false, false, 0, 0, 182201780, 1); // 2
							}
						case USE_OBJECT:
							if (qs.getQuestVarById(0) == 1) {
								return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 204589, 1, 2);
							}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204589) { // Berone
				if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
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
			if (var == 2) {
				changeQuestStep(env, 2, 1, false);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 2, true); // reward
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 1, false); // 1
	}
}
