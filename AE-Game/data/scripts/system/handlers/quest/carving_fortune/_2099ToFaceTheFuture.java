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

package quest.carving_fortune;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * Talk with Munin (790001). Find Fissure of Destiny (700551) that connects to Ataxiar (320140000)
 * and talk with Hagen (205020) (spawn). Proceed to Ataxiar aand annihilate the Guardian Legionarys (50): Legionary
 * (798342, 798343, 798344, 798345), Vanquish Brigade General Hellion (1), Talk with Lephar (205118) (spawn). Report the
 * result to Vidar (204052).
 *
 * @author Bobobear
 */
public class _2099ToFaceTheFuture extends QuestHandler {

	private final static int questId = 2099;
	private final static int[] npcs = { 203550, 205020, 205118, 700551, 204052 };
	private final static int[] mobs = { 798342, 798343, 798344, 798345, 798346 };

	public _2099ToFaceTheFuture() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc_id : npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203550: { // Munin
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							if ((!giveQuestItem(env, 182207093, 1)) || (!giveQuestItem(env, 182207094, 1)))
								return false;
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 700551: { // Fissure of Destiny
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 1) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320140000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 320140000, newInstance.getInstanceId(), 52, 174, 229);
						return true;
					}
					break;
				}
				case 205020: { // Hagen
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
							if (var == 1) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(1001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
								final QuestEnv qe = env;
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										changeQuestStep(qe, 1, 2, false);
									}
								}, 43000);
								return true;
							}
						}
					}
					break;
				}
				case 205118: { // Lephar
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 53) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2:
						case SETPRO3:
							return defaultCloseDialog(env, 53, 53, true, false); // reward
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204052) { // Vidar
				removeQuestItem(env, 182207093, 1);
				removeQuestItem(env, 182207094, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 52) {
				int[] npcIds = { 798342, 798343, 798344, 798345 };
				if (var == 51)
					QuestService.addNewSpawn(320140000, player.getInstanceId(), 798346, 240f, 257f, 208.53946f, (byte) 68);
				return defaultOnKillEvent(env, npcIds, 2, 52); // 2 - 52
			}
			else if (var == 52) {
				return defaultOnKillEvent(env, 798346, 52, 53); // 53
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
			if (var > 1) {
				changeQuestStep(env, var, 1, false); // 1
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() != 320140000) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 1) {
					changeQuestStep(env, var, 1, false); // 1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2098);
	}
}
