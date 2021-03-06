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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Go to Brusthonin and meet Phyper (798300). See if the prophecy of Phyper is realized (go out from Brusthonin). A
 * summons has arrived! Go to Khrudgelmir (204253). Talk with the Arena Master, Garm (204089). Enter Underground Arena
 * Entrance (700368) and find a Shadow Judge (700963). Find Underground Arena Exit (730067) and escape from the Shadow
 * Court Dungeon. Talk with Khrudgelmir. Go to Ishalgen and talk with Munin (203550).
 *
 * @author vlog
 */
public class _2076TheShadowSummons extends QuestHandler {

	private final static int questId = 2076;
	private final static int[] npcs = { 798300, 204253, 700369, 204089, 203550 };

	public _2076TheShadowSummons() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLeaveZone(ZoneName.get("BALTASAR_HILL_VILLAGE_220050000"), questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2701, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
			return false;
		Npc target = (Npc) env.getVisibleObject();
		int targetId = target.getNpcId();
		int var = qs.getQuestVarById(0);
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798300: { // Phyper
					if (dialog == DialogAction.QUEST_SELECT && var == 0) {
						return sendQuestDialog(env, 1011);
					}
					if (dialog == DialogAction.SETPRO1) {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				}
				case 204253: { // Khrudgelmir
					if (dialog == DialogAction.QUEST_SELECT && var == 2) {
						return sendQuestDialog(env, 1693);
					}
					if (dialog == DialogAction.QUEST_SELECT && var == 6) {
						return sendQuestDialog(env, 3057);
					}
					if (dialog == DialogAction.SETPRO3) {
						removeQuestItem(env, 182205502, 1);
						return defaultCloseDialog(env, 2, 3); // 3
					}
					if (dialog == DialogAction.SET_SUCCEED) {
						return defaultCloseDialog(env, 6, 6, true, false); // reward
					}
					break;
				}
				case 700369: { // Underground Arena Exit
					if (dialog == DialogAction.USE_OBJECT && var == 5) {
						TeleportService2.teleportTo(player, 120010000, 981.6009f, 1552.97f, 210.46f);
						changeQuestStep(env, 5, 6, false); // 6
						return true;
					}
					break;
				}
				case 204089: { // Garm
					if (dialog == DialogAction.QUEST_SELECT && var == 3) {
						return sendQuestDialog(env, 2034);
					}
					if (dialog == DialogAction.SETPRO4) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320120000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 320120000, newInstance.getInstanceId(), 591.47894f, 420.20865f,
							202.97754f);
						playQuestMovie(env, 423);
						changeQuestStep(env, 3, 5, false); // 5
						return closeDialogWindow(env);
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) { // Munin
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					int[] questItems = { 182205502 };
					return sendQuestEndDialog(env, questItems);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLeaveZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("BALTASAR_HILL_VILLAGE_220050000") && var == 1) {
				giveQuestItem(env, 182205502, 1);
				changeQuestStep(env, 1, 2, false); // 2
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() == 320120000 || qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var == 5) {
			qs.setQuestVarById(0, 3); // 3
			updateQuestStatus(env);
			return true;
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var == 5) {
			qs.setQuestVarById(0, 3); // 3
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}
