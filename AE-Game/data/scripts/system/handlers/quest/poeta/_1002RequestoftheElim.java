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

package quest.poeta;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_ASCENSION_MORPH;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestActionType;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author MrPoke
 * @reworked vlog
 */
public class _1002RequestoftheElim extends QuestHandler {

	private final static int questId = 1002;

	public _1002RequestoftheElim() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203076, 730007, 730010, 730008, 205000, 203067 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203076: { // Ampeis
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
				case 730007: { // Forest Protector Noah
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 5) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 2034);
							}
							else if (var == 12) {
								return sendQuestDialog(env, 2120);
							}
						}
						case SELECT_ACTION_1353: {
							if (var == 1) {
								playQuestMovie(env, 20);
								return sendQuestDialog(env, 1353);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2, 182200002, 1, 0, 0); // 2
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 5, 6, 0, 0, 182200002, 1); // 6
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							if (var == 6) {
								return checkQuestItems(env, 6, 12, false, 2120, 2205); // 12
							}
							else if (var == 12) {
								return sendQuestDialog(env, 2120);
							}
						}
						case SETPRO4: {
							return defaultCloseDialog(env, 12, 13); // 13
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 730010: { // Sleeping Elder
					if (dialog == DialogAction.USE_OBJECT) {
						if (player.getInventory().getItemCountByItemId(182200002) == 1) {
							if (var == 2) {
								((Npc) env.getVisibleObject()).getController().scheduleRespawn();
								((Npc) env.getVisibleObject()).getController().onDelete();
								useQuestObject(env, 2, 4, false, false); // 4
							}
							else if (var == 4) {
								((Npc) env.getVisibleObject()).getController().scheduleRespawn();
								((Npc) env.getVisibleObject()).getController().onDelete();
								return useQuestObject(env, 4, 5, false, false); // 5
							}
						}
					}
					break;
				}
				case 730008: { // Daminu
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 13) {
								return sendQuestDialog(env, 2375);
							}
							else if (var == 14) {
								return sendQuestDialog(env, 2461);
							}
						}
						case SETPRO5: {
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310010000);
							TeleportService2.teleportTo(player, 310010000, newInstance.getInstanceId(), 52, 174, 229);
							changeQuestStep(env, 13, 20, false); // 20
							return closeDialogWindow(env);
						}
						case SETPRO6: {
							return defaultCloseDialog(env, 14, 14, true, false); // reward
						}
					}
					break;
				}
				case 205000: { // Belpartan
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 20) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(1001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
								ThreadPoolManager.getInstance().schedule(new Runnable() {

									@Override
									public void run() {
										changeQuestStep(env, 20, 14, false); // 14
										TeleportService2.teleportTo(player, 210010000, 1, 603, 1537, 116, (byte) 20);
									}
								}, 43000);
								return true;
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) { // Kalio
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2716);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 310010000) {
				PacketSendUtility.sendPacket(player, new SM_ASCENSION_MORPH(1));
				return true;
			}
			else {
				int var = qs.getQuestVarById(0);
				if (var == 20) {
					changeQuestStep(env, 20, 13, false); // 13
				}
			}
		}
		return false;
	}

	@Override
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		int targetId = env.getTargetId();
		if (targetId == 730010) {
			if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVarById(0) != 2 && qs.getQuestVarById(0) != 4) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
}
