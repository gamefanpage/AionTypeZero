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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * Talk with Gulkalla (203649). Destroy Hero Spirits (210588, 210722) (3). Go back to Gulkalla. Bring Umkata's Three
 * Tokens (700097) to Umkata's Grave (700098), summon the spirit of Umkata (210752, spawn), and destroy it. Report back
 * to Gulkalla.
 *
 * @author Mr. Poke
 * @modified Gigi
 * @reworked vlog
 */
public class _2018ReconstructingImpetusium extends QuestHandler {

	private final static int questId = 2018;

	public _2018ReconstructingImpetusium() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203649).addOnTalkEvent(questId);
		qe.registerQuestNpc(700097).addOnTalkEvent(questId);
		qe.registerQuestNpc(700098).addOnTalkEvent(questId);
		qe.registerQuestNpc(210588).addOnKillEvent(questId);
		qe.registerQuestNpc(210722).addOnKillEvent(questId);
		qe.registerQuestNpc(210752).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203649: { // Gulkalla
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 4) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case SELECT_QUEST_REWARD: {
							changeQuestStep(env, 7, 7, true); // reward
							return sendQuestDialog(env, 5);
						}
					}
					break;
				}
				case 700097: { // Umkata's Jewel Box
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 5) {
						return true; // loot
					}
					break;
				}
				case 700098: { // Umkata's Grave
					switch (env.getDialog()) {
						case USE_OBJECT: {
							if (var == 5) {
								return sendQuestDialog(env, 2034);
							}
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							if (QuestService.collectItemCheck(env, false)) { // don't remove yet
								QuestService.addNewSpawn(220030000, player.getInstanceId(), 210752, 2889.9834f, 1741.3108f, 254.75f,
									(byte) 0);
								return closeDialogWindow(env);
							}
							else {
								return sendQuestDialog(env, 2120);
							}
						}
						case FINISH_DIALOG: {
							return closeDialogWindow(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203649) { // Gulkalla
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var >= 1 && var < 4) {
			int[] npcs = { 210588, 210722 };
			return defaultOnKillEvent(env, npcs, var, var + 1); // 2 - 4
		}
		else if (var == 5) {
			if (env.getTargetId() == 210752) {
				qs.setQuestVar(7); // 7
				updateQuestStatus(env);
				QuestService.collectItemCheck(env, true); // now remove collected items
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2200, true);
	}
}
