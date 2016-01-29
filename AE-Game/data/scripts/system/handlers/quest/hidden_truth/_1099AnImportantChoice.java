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

package quest.hidden_truth;

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
import org.typezero.gameserver.model.TeleportAnimation;
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
 * Talk with Pernos (790001). Find Fissure of Destiny (700551) that connects to Karamatis (310120000, 52, 174, 229, 0)
 * and talk with Hermione (205119) (spawn). Proceed to Karamatis and defeat Orissan Legionary (50): Legionary (215396,
 * 215397, 215398, 215399, 215400), Archon legionary (205021, 205022). Defeat Orissan (215400) (spawn 310120000,
 * 182, 294, 296, 90) (1). Activate the Artifact of Memory (700552). Talk with Lephar (205118) (spawn). Report the
 * result to Fasimedes (203700) (110010000, 1867, 2068, 517).
 *
 * @author vlog  Reworked Bobobear
 */
public class _1099AnImportantChoice extends QuestHandler {

	private final static int questId = 1099;
	private final static int[] npcs = { 790001, 700551, 205119, 700552, 205118, 203700 };
	private final static int[] mobs = { 215396, 215397, 215398, 215399, 215400 };

	public _1099AnImportantChoice() {
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
				case 790001: { // Pernos
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							if ((!giveQuestItem(env, 182206066, 1)) || (!giveQuestItem(env, 182206067, 1)))
								return false;
							TeleportService2.teleportTo(player, 400010000, 2243.5088f, 2180.471f, 2191.32f, (byte) 18, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 700551: { // Fissure of Destiny
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 1) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310120000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 310120000, newInstance.getInstanceId(), 52, 174, 229);
						return true;
					}
					break;
				}
				case 205119: { // Hermione
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
				case 700552: { // Artifact of Memory
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 53) {
						playQuestMovie(env, 429);
						QuestService.addNewSpawn(310120000, player.getInstanceId(), 205118, 302.19955f, 290.99936f, 207.37636f, (byte) 74);
						return useQuestObject(env, 53, 54, false, 0, 0, 0, 182206058, 1, 0, false);
					}
					break;
				}
				case 205118: { // Lephar
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 54) {
								return sendQuestDialog(env, 2719);
							}
						}
						case SETPRO6:
						TeleportService2.teleportTo(player, 110010000, 1871.9f, 1511.44f, 812.7f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
						return defaultCloseDialog(env, 54, 54, true, false); // reward
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203700) { // Fasimedes
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
				int[] npcIds = { 215396, 215397, 215398, 215399 };
				if (var == 51)
					QuestService.addNewSpawn(310120000, player.getInstanceId(), 215400, 240f, 257f, 208.53946f, (byte) 68);
				return defaultOnKillEvent(env, npcIds, 2, 52); // 2 - 52
			}
			else if (var == 52) {
				return defaultOnKillEvent(env, 215400, 52, 53); // 53
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
		if (player.getWorldId() != 310120000) {
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
		return defaultOnLvlUpEvent(env, 1098);
	}
}
