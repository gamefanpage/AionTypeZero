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
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Rhys2002
 * @modified & reworked Gigi, vlog
 */
public class _2053AMissingFather extends QuestHandler {

	private final static int questId = 2053;

	public _2053AMissingFather() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204707, 204749, 204800, 730108, 700359 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204305, questId);
		qe.registerOnMovieEndQuest(236, questId);
		qe.registerOnEnterZone(ZoneName.get("MALEK_MINE_220040000"), questId);
		qe.registerOnEnterZone(ZoneName.get("MINE_PORT_220040000"), questId);
		qe.registerOnEnterWorld(questId);
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
			if (targetId == 204707) { // Mani
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
					case SETPRO6: {
						return defaultCloseDialog(env, 5, 6, 0, 0, 182204306, 1); // 6
					}
				}
			}
			else if (targetId == 204749) { // Paeru
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case SETPRO2: {
						return defaultCloseDialog(env, 1, 2, 182204305, 1, 0, 0); // 2
					}
				}
			}
			else if (targetId == 730108) { // Strahein's Liquor Bottle
				switch (dialog) {
					case USE_OBJECT: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					}
					case SETPRO5: {
						return defaultCloseDialog(env, 4, 5, 182204306, 1, 182204305, 1); // 5
					}
				}
			}
			else if (targetId == 204800) { // Hammel
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					}
					case SETPRO7: {
						return defaultCloseDialog(env, 6, 7); // 7
					}
				}
			}
			else if (targetId == 700359 && var == 7 && player.getInventory().getItemCountByItemId(182204307) >= 1) { // Secret Port Entrance
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), 1757.82f, 1392.94f, 401.75f, (byte) 94);
					return true;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204707) { // Mani
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
			if (var == 2) {
				// TODO: readable text dialog
				return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false)); // 3
			}
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onEnterZoneEvent(final QuestEnv env, ZoneName name) {
		Player player = env.getPlayer();
		if (player == null)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (name == ZoneName.get("MALEK_MINE_220040000")) {
			  if (var == 3) {
				  changeQuestStep(env, 3, 4, false); // 4
			  }
			}
			else if (name == ZoneName.get("MINE_PORT_220040000")) {
				if(var == 7) {
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
						playQuestMovie(env, 236);
						}
					}, 10000);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 236)
			return false;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			changeQuestStep(env, 7, 7, true); // reward
			removeQuestItem(env, 182204307, 1);
			return true;
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2500, true);
	}
}
