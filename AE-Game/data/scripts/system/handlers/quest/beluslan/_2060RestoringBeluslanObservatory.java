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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Talk with Hod (204701).<br>
 * Talk with Gwendolin (204785).<br>
 * Talk with Hisui (278003).<br>
 * Talk with Glati (278088) (182204318).<br>
 * Go to Leibo Island (400010001), gather Aether, and bring it (182204319) to Gwendolin.<br>
 * Destroy Field Suppressors around the Observatory (700290) (3).<br>
 * Repair the Aetheric Field Maintaining Device (700293).<br>
 * Report to Hod.<br>
 *
 * @author kecimis
 * @reworked vlog
 */
public class _2060RestoringBeluslanObservatory extends QuestHandler {

	private final static int questId = 2060;

	public _2060RestoringBeluslanObservatory() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204701, 204785, 278003, 278088, 700293 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204318, questId);
		qe.registerQuestNpc(700290).addOnKillEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
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
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204701: { // Hod
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
				case 204785: { // Gwendolin
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 4) {
								if (QuestService.collectItemCheck(env, false)) {
									return sendQuestDialog(env, 2375);
								}
								else {
									giveQuestItem(env, 182204318, 1); // give another bottle
									return sendQuestDialog(env, 2461);
								}
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case SETPRO5: {
							return defaultCloseDialog(env, 4, 5, 0, 0, 182204319, 1); // 5
						}
						case FINISH_DIALOG: {
							if (var == 4) {
								return sendQuestSelectionDialog(env);
							}
						}
					}
					break;
				}
				case 278003: { // Hisui
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
				case 278088: { // Glati
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO4: {
							return defaultCloseDialog(env, 3, 4, 182204318, 1, 0, 0); // 4
						}
					}
					break;
				}
				case 700293: { // Aetheric Field Maintaining Device
					if (dialog == DialogAction.USE_OBJECT) {
						if (var == 8) {
							return useQuestObject(env, 8, 8, true, 0); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204701) { // Hod
				if (env.getDialog() == DialogAction.USE_OBJECT) {
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
			if (var == 4) {
				if (player.isInsideZone(ZoneName.get("AB1_ITEMUSEAREA_Q2060"))) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 4, 4, false, 182204319, 1, 0)); // 4 + aether bottle
				}
			}
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 700290, 5, 8); // 6, 7, 8
	}
}
