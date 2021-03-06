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

package quest.sanctum;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Mr. Poke
 * @modified Rolandas
 * @reworked vlog
 */
public class _1929ASliverofDarkness extends QuestHandler {

	private final static int questId = 1929;

	public _1929ASliverofDarkness() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203752, 203852, 203164, 205110, 700240, 205111, 203701, 203711 };
		int[] stigmas = { 140001110, 140001128, 140001171, 140001146, 140001186, 140001208, 140001240, 140001217, 140001290, 140001254, 140001274 };
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(155, questId);
		qe.registerQuestNpc(212992).addOnKillEvent(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		for (int stigma : stigmas) {
			qe.registerOnEquipItem(stigma, questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		int var = qs.getQuestVars().getQuestVars();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203752: { // Jucleas
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							TeleportService2.teleportTo(player, 110010000, 1828f, 2182f, 529f, (byte) 30, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 203852: { // Ludina
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
							TeleportService2.teleportTo(player, 210030000, 2315.4f, 1798.52f, 195.3f, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 203164: { // Morai
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 8) {
								return sendQuestDialog(env, 3057);
							}
						}
						case SETPRO3: {
							if (var == 2) {
								changeQuestStep(env, 2, 93, false); // 93
								WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310070000);
								InstanceService.registerPlayerWithInstance(newInstance, player);
								TeleportService2.teleportTo(player, 310070000, newInstance.getInstanceId(), 338, 101, 1191);
								return closeDialogWindow(env);
							}
						}
						case SETPRO7: {
							TeleportService2.teleportTo(player, 110010000, 1872.1f, 1510.84f, 812.7f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 8, 9); // 9
						}
					}
					break;
				}
				case 205110: { // Icaronix
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 93) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO4: {
							if (var == 93) {
								changeQuestStep(env, 93, 94, false); // 94
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(31001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 31001, 0));
								return true;
							}
						}
					}
					break;
				}
				case 700240: { // Icaronix's Box
					if (dialog == DialogAction.USE_OBJECT) {
						if (var == 94) {
							return playQuestMovie(env, 155);
						}
					}
					break;
				}
				case 205111: { // Ecus
					switch (dialog) {
						case USE_OBJECT: {
							if (var == 96) {
								if (isStigmaEquipped(env)) {
									return sendQuestDialog(env, 2716);
								}
								else {
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
									return closeDialogWindow(env);
								}
							}
						}
						case QUEST_SELECT: {
							if (var == 98) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SELECT_ACTION_2546: {
							/*if (var == 98) {
								if (giveQuestItem(env, getStoneId(player), 1)) {
									long existendStigmaShards = player.getInventory().getItemCountByItemId(141000001);
									if (existendStigmaShards < 300) {
										if (!player.getInventory().isFull()) {
											//ItemService.addItem(player, 141000001, 300 - existendStigmaShards);
											PacketSendUtility.sendPacket(player,
												new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
											return true;
										}
									}
									else {
										PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1));
										return true;
									}
								}
							}
						}
						case SELECT_ACTION_2720: {*/
							if (var == 98) {
								Npc npc = (Npc) env.getVisibleObject();
								npc.getController().onDelete();
								QuestService.addNewSpawn(310070000, player.getInstanceId(), 212992, (float) 191.9, (float) 267.68,
									(float) 1374, (byte) 0);
								changeQuestStep(env, 98, 97, false); // 97
								return closeDialogWindow(env);
							}
						}
					}
					break;
				}
				case 203701: { // Lavirintos
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 9) {
								return sendQuestDialog(env, 3398);
							}
						}
						case SETPRO8: {
							return defaultCloseDialog(env, 9, 9, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203711) { // Miriya
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		if (movieId == 155) {
			QuestService.addNewSpawn(310070000, player.getInstanceId(), 205111, (float) 197.6, (float) 265.9, (float) 1374.0,
				(byte) 0);
			changeQuestStep(env, 94, 98, false); // 98
			return true;
		}
		return false;
	}

	@Override
	public boolean onEquipItemEvent(QuestEnv env, int itemId) {
		changeQuestStep(env, 98, 96, false); // 96
		return closeDialogWindow(env);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 97) {
				changeQuestStep(env, 97, 8, false); // 8
				TeleportService2.teleportTo(player, 210030000, 1, 2315.9f, 1800f, 195.2f);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 93 && var <= 98) {
				removeStigma(env);
				qs.setQuestVar(2);
				updateQuestStatus(env);
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (player.getWorldId() != 310070000) {
				if (var >= 93 && var <= 98) {
					removeStigma(env);
					qs.setQuestVar(2);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
				else if (var == 8) {
					removeStigma(env);
					return true;
				}
			}
		}
		return false;
	}

	private int getStoneId(Player player) {
		switch (player.getCommonData().getPlayerClass()) {
			case GLADIATOR: {
				return 140001110;// Improved Stamina I
			}
			case TEMPLAR: {
				return 140001128;// Divine Fury I
			}
			case RANGER: {
				return 140001171;// Arrow Deluge I
			}
			case ASSASSIN: {
				return 140001146;// Sigil Strike I
			}
			case SORCERER: {
				return 140001186;// Lumiel's Wisdom I
			}
			case SPIRIT_MASTER: {
				return 140001208;// Absorb Vitality I
			}
			case CLERIC: {
				return 140001240;// Grace of Empyrean Lord I
			}
			case CHANTER: {
				return 140001217;// Rage Spell I
			}
			case BARD: {
				return 140001290;
			}
			case GUNNER: {
				return 140001254;
			}
			case RIDER: {
				return 140001274;
			}
			default: {
				return 0;
			}
		}
	}

	private boolean isStigmaEquipped(QuestEnv env) {
		Player player = env.getPlayer();
		for (Item i : player.getEquipment().getEquippedItemsAllStigma()) {
			if (i.getItemId() == getStoneId(player)) {
				return true;
			}
		}
		return false;
	}

	private void removeStigma(QuestEnv env) {
		Player player = env.getPlayer();
		for (Item item : player.getEquipment().getEquippedItemsByItemId(getStoneId(player))) {
			player.getEquipment().unEquipItem(item.getObjectId(), 0);
		}
		removeQuestItem(env, getStoneId(player), 1);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
}
