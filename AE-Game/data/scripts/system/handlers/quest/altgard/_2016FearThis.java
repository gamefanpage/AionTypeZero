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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Talk with Nokir (203631). Go to the MuMu Village and get rid of the Black Claw Tribe (5): Black Claw Sharpeye
 * (210455, 210456), Black Claw Warrior (214039, 210458), Black Claw Searcher (214032). Go back to Nokir. Talk with
 * Shania (203621). Gather Arachna Poison Sacs (182203018) (3) and take them to Shania. Go to the MuMu Village, and pour
 * the toxin into the Fresh Water Source (MUMU_VILLAGE_220030000). Mission completed! Report the result to Nokir.
 *
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2016FearThis extends QuestHandler {

	private final static int questId = 2016;
	private final static int[] mobs = { 210455, 210456, 214039, 210458, 214032 };

	public _2016FearThis() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203631).addOnTalkEvent(questId);
		qe.registerQuestNpc(203621).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(182203018, questId);
		qe.registerQuestItem(182203019, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203631: { // Nokir
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SELECT_ACTION_1012: {
							playQuestMovie(env, 63);
							return sendQuestDialog(env, 1012);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 6, 7); // 7
						}
					}
					break;
				}
				case 203621: { // Shania
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 7) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 8) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 7, 8); // 8
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 8, 10, false, 2035, 2120, 182203019, 1); // 10
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 8, 8);
						}
						case SETPRO4: {
							return defaultCloseDialog(env, 10, 10);
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203631) { // Nokir
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2375);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, mobs, 1, 6); // 6
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 10, 10, true)); // reward
		}
		return HandlerResult.FAILED;
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
