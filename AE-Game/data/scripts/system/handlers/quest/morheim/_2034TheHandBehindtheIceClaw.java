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
import org.typezero.gameserver.model.gameobjects.Npc;
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
 * @author Rhys2002
 * @reworked vlog
 */
public class _2034TheHandBehindtheIceClaw extends QuestHandler {

	private final static int questId = 2034;

	public _2034TheHandBehindtheIceClaw() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204303, 204332, 700246, 204301 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204417).addOnKillEvent(questId);
		qe.registerQuestNpc(212877).addOnKillEvent(questId);
		qe.registerQuestItem(182204008, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204303: { // Nina
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 5) {
								return sendQuestDialog(env, 2716);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SET_SUCCEED: {
							return defaultCloseDialog(env, 5, 5, true, false); // reward
						}
					}
					break;
				}
				case 204332: { // Jorund
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 2) {
								if (player.getInventory().getItemCountByItemId(182204008) == 0) {
									return sendQuestDialog(env, 1694);
								}
								else {
									return sendQuestDialog(env, 1693);
								}
							}
							else if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO2: {
							if (var == 1) {
								return defaultCloseDialog(env, 1, 2, 182204008, 1, 0, 0); // 2
							}
							else if (var == 2) {
								return defaultCloseDialog(env, 2, 2, 182204008, 1, 0, 0); // 2
							}
						}
						case SETPRO4: {
							player.getTitleList().addTitle(58, true, 0);
							return defaultCloseDialog(env, 3, 4); // 4
						}
					}
					break;
				}
				case 700246: { // Dead Fire
					if (dialog == DialogAction.USE_OBJECT) {
						if (var == 2) {
							if (player.getInventory().getItemCountByItemId(182204019) > 0) {
								final Npc npc = (Npc) env.getVisibleObject();
								QuestService.addNewSpawn(220020000, 1, 204417, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
								removeQuestItem(env, 182204008, 1);
								removeQuestItem(env, 182204019, 1);
							}
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
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("ALTAR_OF_TRIAL_220020000"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 2, false));
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int targetId = env.getTargetId();
			switch (targetId) {
				case 204417: {
					return defaultOnKillEvent(env, 204417, 2, 3); // 3
				}
				case 212877: {
					return defaultOnKillEvent(env, 212877, 4, 5); // 5
				}
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
		return defaultOnLvlUpEvent(env, 2300, true);
	}
}
