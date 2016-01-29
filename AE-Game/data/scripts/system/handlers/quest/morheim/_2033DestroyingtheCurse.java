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

package quest.morheim;

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
 * Talk with Urakon (204391).<br>
 * Talk with Kellan (790020).<br>
 * Gather Spiner Tails and take them to Kellan.<br>
 * Listen to the story of Kellan.<br>
 * Meet Kimssi (204393).<br>
 * Choose a Skurv to guide you.<br>
 * Throw the Cursed Necklace (182204007) into the Boiling Lava (TARANS_CAVERN_220020000).<br>
 * Report back to Kellan.
 *
 * @author Erin
 * @reworked vlog
 */
public class _2033DestroyingtheCurse extends QuestHandler {

	private final static int questId = 2033;

	public _2033DestroyingtheCurse() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204391, 790020, 204393, 204394, 204395, 204396, 204397, 204398 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204007, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
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
				case 204391: { // Urakon
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 790020: { // Kellan
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 3) {
								return sendQuestDialog(env, 10000);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 2, 3, false, 10000, 10001); // 3
						}
						case SETPRO4: {
							if (!player.getInventory().isFullSpecialCube()) {
								return defaultCloseDialog(env, 3, 4, 182204007, 1, 0, 0); // 4
							}
							else {
								return defaultCloseDialog(env, 3, 3);
							}
						}
						case FINISH_DIALOG: {
							return defaultCloseDialog(env, 2, 2);
						}
					}
					break;
				}
				case 204393: { // Kimssi
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SELECT_ACTION_2376: {
							if (var == 4) {
								playQuestMovie(env, 74);
								return sendQuestDialog(env, 2376);
							}
						}
						case SETPRO5: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
					}
					break;
				}
				case 204394:
				case 204395:
				case 204396:
				case 204397:
				case 204398: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 5) {
								return sendQuestDialog(env, 2716);
							}
						}
						case SETPRO6: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790020) { // Kellan
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
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 6) {
				if (player.isInsideZone(ZoneName.get("DF2_ITEMUSEAREA_Q2033"))) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 6, 6, true, 75)); // reward
				}
			}
		}
		return HandlerResult.SUCCESS; // ??
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2300, true);
	}
}
