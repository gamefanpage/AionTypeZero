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

package quest.eltnen;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nephis and quest helper team
 */
public class _1371FlowersForIsson extends QuestHandler {

	private final static int questId = 1371;

	public _1371FlowersForIsson() {
		super(questId);
	}

	@Override
	public void register() {

		qe.registerQuestNpc(203949).addOnQuestStart(questId);
		qe.registerQuestNpc(203949).addOnTalkEvent(questId);
		qe.registerQuestNpc(730039).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		long itemCount = 0;
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203949) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203949) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
						break;
					case CHECK_USER_HAS_QUEST_ITEM:
						if (var == 0)
							itemCount = player.getInventory().getItemCountByItemId(152000601);
						if (itemCount > 4) {
							return sendQuestDialog(env, 1353);
						}
						else {
							return sendQuestDialog(env, 1438);
						}
					case SETPRO1: {
						removeQuestItem(env, 152000601, 5);
						qs.setQuestVar(2);
						updateQuestStatus(env);
						return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 730039) {
				if (qs != null && qs.getStatus() == QuestStatus.START) {
					return useQuestObject(env, 2, 2, true, false); // reward
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203949) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
