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

package quest.beluslan;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Rhys2002
 * @modified & reworked Gigi
 */
public class _2051SavingBeluslanFortress extends QuestHandler {

	private final static int questId = 2051;
	private final static int[] npc_ids = { 204702, 204733, 204206, 278040, 700285, 700284 };

	public _2051SavingBeluslanFortress() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204302, questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2500, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204702) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204702) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SETPRO1:
					return defaultCloseDialog(env, 0, 1);
			}
		}
		else if (targetId == 204733) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
					else if (var == 2)
						return sendQuestDialog(env, 1693);
					else if (var == 6)
						return sendQuestDialog(env, 3057);
				case SETPRO2:
					return defaultCloseDialog(env, 1, 11);
				case SETPRO3:
					return defaultCloseDialog(env, 2, 3);
				case SETPRO7:
					return defaultCloseDialog(env, 6, 7);
			}
		}
		else if (targetId == 204206) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3)
						return sendQuestDialog(env, 2034);
				case SETPRO4:
					return defaultCloseDialog(env, 3, 4);
			}
		}
		else if (targetId == 278040) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 4)
						return sendQuestDialog(env, 2375);
					else if (var == 5)
						return sendQuestDialog(env, 2716);
				case CHECK_USER_HAS_QUEST_ITEM:
					if (QuestService.collectItemCheck(env, true)) {
						if (!giveQuestItem(env, 182204302, 1))
							return true;
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return sendQuestDialog(env, 10000);
					}
					else
						return sendQuestDialog(env, 10001);
				case SETPRO5:
					return defaultCloseDialog(env, 4, 5);
			}
		}
		else if (targetId == 700285) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 7)
						return useQuestObject(env, 7, 7, true, 0, 0, 0, 182204302, 1, 0, false); // reward
			}
		}
		else if (targetId == 700284) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 11)
						return useQuestObject(env, 11, 2, false, 0);
			}
		}
		return false;
	}
}
