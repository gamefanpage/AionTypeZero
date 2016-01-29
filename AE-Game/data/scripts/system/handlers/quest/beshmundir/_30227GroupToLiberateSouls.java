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

package quest.beshmundir;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Gigi
 */

public class _30227GroupToLiberateSouls extends QuestHandler {

	private final static int questId = 30227;

	public _30227GroupToLiberateSouls() {
		super(questId);
	}

	@Override
	public void register() {
		int[] mobs = { 216586, 216590, 216733, 216734, 216735, 216736, 216737, 216738, 216245 };
		qe.registerQuestNpc(798946).addOnQuestStart(questId);
		qe.registerQuestNpc(798946).addOnTalkEvent(questId);
		qe.registerQuestNpc(799521).addOnTalkEvent(questId);
		qe.registerQuestNpc(799517).addOnTalkEvent(questId);
		for (int mob : mobs)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798946) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}

		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799521: {
					switch (dialog) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1);
					}
				}
				case 799517: {
					switch (dialog) {
						case SETPRO1: {
							QuestService.questTimerStart(env, 300);
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798946) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					case SELECT_QUEST_REWARD: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
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
			if (var == 1) {
				qs.setQuestVarById(0, 0);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 216586:
			case 216590:
				if (qs.getQuestVarById(0) == 1) {
					QuestService.questTimerEnd(env);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					playQuestMovie(env, 445);
					return true;
				}
				break;
			case 216733:
			case 216734:
			case 216735:
			case 216736:
			case 216737:
			case 216738:
			case 216245:
				if (qs.getQuestVarById(0) == 2) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
				break;
		}
		return false;
	}
}
