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

package quest.brusthonin;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Surt (205150).<br>
 * Talk with Neligor (205159).<br>
 * Talk with BuBu Khaaan (205164).<br>
 * Talk with BuBu Chan (205197).<br>
 * Obtain BuBu Chan's Requested Items and bring them to BuBu Chan.<br>
 * Talk with Cayron (205198).<br>
 * Find Book of Brohum (730174).<br>
 * Talk with Cayron.<br>
 * Go to Old Nahor Castle and find the key to the secret passage (182209012 need to add) in Old Wooden Box (700395).<br>
 * Report back to Surt.
 *
 * @author Hellboy Aion4Free
 * @reworked vlog
 */
public class _2093TheSecretPassage extends QuestHandler {

	private final static int questId = 2093;

	public _2093TheSecretPassage() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 205150, 205159, 205164, 205197, 205198, 730174, 700395 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.addHandlerSideQuestDrop(questId, 730174, 182209011, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 700395, 182209012, 1, 100);
		qe.registerGetingItem(182209011, questId);
		qe.registerGetingItem(182209012, questId);
		for (int npc_id : npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 205150: { // Surt
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SELECT_ACTION_1012: {
							playQuestMovie(env, 397);
							return sendQuestDialog(env, 1012);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 205159: { // Neligor
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 205164: { // BuBu Khaaan
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				}
				case 205197: { // BuBu Chan
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SETPRO4: {
							return defaultCloseDialog(env, 3, 4); // 4
						}
						case SETPRO5: {
							return defaultCloseDialog(env, 4, 4);
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 4, 5, false, 10000, 10001);
						}
					}
					break;
				}
				case 205198: { // Cayron
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 5) {
								return sendQuestDialog(env, 2716);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 3398);
							}
						}
						case SELECT_ACTION_3399: {
							playQuestMovie(env, 398);
							removeQuestItem(env, 182209011, 1);
							return sendQuestDialog(env, 3399);
						}
						case SETPRO6: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
						case SETPRO8: {
							return defaultCloseDialog(env, 7, 8); // 8
						}
					}
					break;
				}
				case 730174: { // Brohum
					if (dialog == DialogAction.USE_OBJECT && var == 6) {
						return true; // loot
					}
					break;
				}
				case 700395: { // Old Wooden Box
					if (dialog == DialogAction.USE_OBJECT && var == 8) {
						return true; // loot
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205150) { // Surt
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					removeQuestItem(env, 182209012, 1);
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 6) {
				return defaultOnGetItemEvent(env, 6, 7, false); // 7
			}
			else if (var == 8) {
				return defaultOnGetItemEvent(env, 8, 8, true); // reward
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
		return defaultOnLvlUpEvent(env, 2091, true);
	}
}
