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
 * @author Rhys2002
 */
public class _1347RaidingKlaw extends QuestHandler {

	private final static int questId = 1347;
	private final static int[] npc_ids = { 203965, 203966 };
	private final static int[] mob_ids = { 210908, 210874, 212137, 212056, 210917 };

	public _1347RaidingKlaw() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203965).addOnQuestStart(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		for (int mob_id : mob_ids)
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203965) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203966) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 1352);
				}
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203966) {
				if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		int[] mobs1 = { 210908, 210874 };
		int[] mobs2 = { 212137, 212056, 210917 };
		if (defaultOnKillEvent(env, mobs1, 0, 7, 0) || defaultOnKillEvent(env, mobs2, 0, 3, 1))
			return true;
		else
			return false;
	}
}
