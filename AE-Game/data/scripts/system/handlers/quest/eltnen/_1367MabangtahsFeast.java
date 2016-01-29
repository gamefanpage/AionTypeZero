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
 * @author Atomics
 */

public class _1367MabangtahsFeast extends QuestHandler {

	private final static int questId = 1367;

	public _1367MabangtahsFeast() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204023).addOnQuestStart(questId);
		qe.registerQuestNpc(204023).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 204023) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.START) {
				long itemCount;
				long itemCount1;
				long itemCount2;
				if (env.getDialog() == DialogAction.QUEST_SELECT && qs.getQuestVarById(0) == 0) {
					itemCount = player.getInventory().getItemCountByItemId(182201333); // 2
					itemCount1 = player.getInventory().getItemCountByItemId(182201332); // 5
					itemCount2 = player.getInventory().getItemCountByItemId(182201331); // 1
					if (itemCount > 1 || itemCount1 > 5 || itemCount2 > 0) {
						return sendQuestDialog(env, 1352);
					}
					else {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (env.getDialog() == DialogAction.SETPRO1) {
					itemCount2 = player.getInventory().getItemCountByItemId(182201331); // 1
					if (itemCount2 > 0) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 1);
						return sendQuestDialog(env, 5);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == DialogAction.SETPRO2) {
					itemCount1 = player.getInventory().getItemCountByItemId(182201332); // 5
					if (itemCount1 > 4) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 2);
						return sendQuestDialog(env, 6);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == DialogAction.SETPRO3) {
					itemCount = player.getInventory().getItemCountByItemId(182201333); // 2
					if (itemCount > 1) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						qs.setQuestVarById(0, 3);
						updateQuestStatus(env);
						return sendQuestDialog(env, 7);
					}
					else
						return sendQuestDialog(env, 1352);
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

}
