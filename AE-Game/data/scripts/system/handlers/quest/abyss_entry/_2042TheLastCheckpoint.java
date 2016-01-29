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

package quest.abyss_entry;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * Talk with Aegir (204301). Talk with Yornduf (204319). Go through all the rings within the time limit. Talk with
 * Yornduf. Report back to Aegir.
 *
 * @author Hellboy aion4Free, Hilgert
 * @reworked vlog
 */
public class _2042TheLastCheckpoint extends QuestHandler {

	private final static int questId = 2042;
	private String[] rings = { "MORHEIM_ICE_FORTRESS_220020000_1", "MORHEIM_ICE_FORTRESS_220020000_2",
		"MORHEIM_ICE_FORTRESS_220020000_3", "MORHEIM_ICE_FORTRESS_220020000_4", "MORHEIM_ICE_FORTRESS_220020000_5",
		"MORHEIM_ICE_FORTRESS_220020000_6" };

	public _2042TheLastCheckpoint() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204301).addOnTalkEvent(questId);
		qe.registerQuestNpc(204319).addOnTalkEvent(questId);
		for (String ring : rings) {
			qe.registerOnPassFlyingRings(ring, questId);
		}
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204301: { // Aegir
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 0, 0);
						}
					}
					break;
				}
				case 204319: { // Yornduf
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							if (var == 8) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 9) {
								return sendQuestDialog(env, 3057);
							}
						}
						case SELECT_ACTION_1354: {
							if (var == 1 || var == 9) {
								playQuestMovie(env, 89);
								return sendQuestDialog(env, 1354);
							}
						}
						case SETPRO2: {
							if (var == 1) {
								QuestService.questTimerStart(env, 150);
								return defaultCloseDialog(env, 1, 2); // 2
							}
							else if (var == 9) {
								QuestService.questTimerStart(env, 150);
								return defaultCloseDialog(env, 9, 2); // 2
							}
						}
						case SET_SUCCEED: {
							if (var == 8) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
							}
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 9, 9);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204301) { // Aegir
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}

	@Override
	public boolean onPassFlyingRingEvent(QuestEnv env, String flyingRing) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (rings[0].equals(flyingRing)) {
				changeQuestStep(env, 2, 3, false); // 3
				return true;
			}
			else if (rings[1].equals(flyingRing)) {
				changeQuestStep(env, 3, 4, false); // 4
				return true;
			}
			else if (rings[2].equals(flyingRing)) {
				changeQuestStep(env, 4, 5, false); // 5
				return true;
			}
			else if (rings[3].equals(flyingRing)) {
				changeQuestStep(env, 5, 6, false); // 6
				return true;
			}
			else if (rings[4].equals(flyingRing)) {
				changeQuestStep(env, 6, 7, false); // 7
				return true;
			}
			else if (rings[5].equals(flyingRing)) {
				changeQuestStep(env, 7, 8, false); // 8
				QuestService.questTimerEnd(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 1 && var < 8) {
				changeQuestStep(env, var, 9, false); // 9
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 1 && var < 9) {
				QuestService.questTimerEnd(env);
				return this.onQuestTimerEndEvent(env);
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 1 && var < 9) {
				QuestService.questTimerEnd(env);
				return this.onQuestTimerEndEvent(env);
			}
		}
		return false;
	}
}
