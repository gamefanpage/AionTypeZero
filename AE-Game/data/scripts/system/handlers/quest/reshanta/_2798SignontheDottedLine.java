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

import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

public class _2798SignontheDottedLine extends QuestHandler {

	private final static int questId = 2798;

	public _2798SignontheDottedLine() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 279007, 263569, 263267, 264769, 271054, 266554, 270152, 269252, 268052, 260236 };
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestNpc(279007).addOnQuestStart(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		if (sendQuestNoneDialog(env, 279007, 4762, 182205646, 1))
			return true;
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		if (env.getTargetId() == 263569) {
			if (qs.getStatus() == QuestStatus.START && var == 0)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SETPRO1:
						return defaultCloseDialog(env, 0, 1);
				}
		}
		else if (env.getTargetId() == 263267) {
			if (qs.getStatus() == QuestStatus.START && var == 1)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1352);
					case SETPRO2:
						return defaultCloseDialog(env, 1, 2);
				}
		}
		else if (env.getTargetId() == 264769) {
			if (qs.getStatus() == QuestStatus.START && var == 2)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1693);
					case SETPRO3:
						return defaultCloseDialog(env, 2, 3);
				}
		}
		else if (env.getTargetId() == 271054) {
			if (qs.getStatus() == QuestStatus.START && var == 3)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 2034);
					case SETPRO4:
						return defaultCloseDialog(env, 3, 4);
				}
		}
		else if (env.getTargetId() == 266554) {
			if (qs.getStatus() == QuestStatus.START && var == 4)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 2375);
					case SETPRO5:
						return defaultCloseDialog(env, 4, 5);
				}
		}
		else if (env.getTargetId() == 270152) {
			if (qs.getStatus() == QuestStatus.START && var == 5)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 2716);
					case SETPRO6:
						return defaultCloseDialog(env, 5, 6);
				}
		}
		else if (env.getTargetId() == 269252) {
			if (qs.getStatus() == QuestStatus.START && var == 6)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 3057);
					case SETPRO7:
						return defaultCloseDialog(env, 6, 7);
				}
		}
		else if (env.getTargetId() == 268052) {
			if (qs.getStatus() == QuestStatus.START && var == 7)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 3398);
					case SETPRO8:
						return defaultCloseDialog(env, 7, 8);
				}
		}
		else if (env.getTargetId() == 260236) {
			if (qs.getStatus() == QuestStatus.START && var == 8)
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 3739);
					case SET_SUCCEED:
						return defaultCloseDialog(env, 8, 8, true, false);
				}
		}
		return sendQuestRewardDialog(env, 279007, 10002);
	}
}
