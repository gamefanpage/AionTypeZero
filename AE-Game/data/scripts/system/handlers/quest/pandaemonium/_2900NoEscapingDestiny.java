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

package quest.pandaemonium;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
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
 * @author Mr. Poke, edited Rolandas
 * @reworked vlog
 */
public class _2900NoEscapingDestiny extends QuestHandler {

	private final static int questId = 2900;

	public _2900NoEscapingDestiny() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204182, 203550, 790003, 790002, 203546, 204264, 204061 };
		int[] stigmas = { 140001110, 140001128, 140001171, 140001146, 140001186, 140001208, 140001240, 140001218, 140001290, 140001254, 140001274 };
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(156, questId);
		qe.registerQuestNpc(204263).addOnKillEvent(questId);
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
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVars().getQuestVars();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204182: { // Heimdall
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							TeleportService2.teleportTo(player, 220010000, 383.10248f, 1895.3093f, 327.625f, (byte) 59, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 203550: { // Munin
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 10) {
								return sendQuestDialog(env, 4080);
							}
						}
						case SETPRO2: {
							TeleportService2.teleportTo(player, 220010000, 585.5074f, 2416.0312f, 278.625f, (byte) 102, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case SETPRO10: {
							TeleportService2.teleportTo(player, 120010000, 1293.45f, 1211.5f, 215f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 10, 10, true, false); // reward
						}
					}
					break;
				}
				case 790003: { // Urd
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO3: {
							TeleportService2.teleportTo(player, 220010000, 940.74475f, 2295.5305f, 265.65674f, (byte) 46, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				}
				case 790002: { // Verdandi
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO4: {
							TeleportService2.teleportTo(player, 220010000, 1111.5637f, 1719.2745f, 270.114256f, (byte) 114, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 3, 4); // 4
						}
					}
					break;
				}
				case 203546: { // Skuld
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
							else if (var == 9) {
								return sendQuestDialog(env, 3739);
							}
						}
						case SETPRO5: {
							if (var == 4) {
								changeQuestStep(env, 4, 95, false); // 95
								WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320070000);
								InstanceService.registerPlayerWithInstance(newInstance, player);
								TeleportService2.teleportTo(player, 320070000, newInstance.getInstanceId(), 268.47397f, 251.80275f,
									125.8369f);
								return closeDialogWindow(env);
							}
						}
						case SETPRO9: {
							changeQuestStep(env, 9, 10, false); // 10
							TeleportService2.teleportTo(player, 220010000, 1, 383.0f, 1896.0f, 327.625f);
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 204264: { // Skuld 2
					switch (dialog) {
						case USE_OBJECT: {
							if (var == 99 && !isStigmaEquipped(env)) {
								return sendQuestDialog(env, 3057);
							}
						}
						case QUEST_SELECT: {
							if (var == 95) {
								return sendQuestDialog(env, 2716);
							}
							else if (var == 96) {
								return sendQuestDialog(env, 3057);
							}
							else if (var == 97) {
								return sendQuestDialog(env, 3398);
							}
						}
						case SETPRO6: {
							if (var == 95) {
								playQuestMovie(env, 156);
								return closeDialogWindow(env);
							}
						}
						case SELECT_ACTION_3058: {
							if (var == 96) {
								changeQuestStep(env, 96, 98, false); // 98
								QuestService.addNewSpawn(320070000, player.getInstanceId(), 204263, 257.5f, 245f, 125f, (byte) 0);
								return closeDialogWindow(env);
							}
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204061) { // Aud
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId == 156) {
			changeQuestStep(env, 95, 96, false); // 96
			return true;
		}
		return false;
	}

	@Override
	public boolean onEquipItemEvent(QuestEnv env, int itemId) {
		changeQuestStep(env, 99, 97, false); // 97
		return closeDialogWindow(env);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 98) {
				changeQuestStep(env, 98, 9, false); // 9
				TeleportService2.teleportTo(player, 220010000, 1, 1113.8882f, 1719.497f, 271.07687f);
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
			if (player.getWorldId() != 320070000) {
				if (var >= 95 && var <= 99) {
					removeStigma(env);
					qs.setQuestVar(4);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
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
			int var = qs.getQuestVars().getQuestVars();
			if (player.getWorldId() != 320070000) {
				if (var >= 95 && var <= 99) {
					removeStigma(env);
					qs.setQuestVar(4);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
				else if (var == 9) {
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
				return 140001110; // Improved Stamina I
			}
			case TEMPLAR: {
				return 140001128; // Divine Fury I
			}
			case RANGER: {
				return 140001171; // Arrow Deluge I
			}
			case ASSASSIN: {
				return 140001146; // Sigil Strike I
			}
			case SORCERER: {
				return 140001186; // Lumiel's Wisdom I
			}
			case SPIRIT_MASTER: {
				return 140001208; // Absorb Vitality I
			}
			case CLERIC: {
				return 140001240; // Grace of Empyrean Lord I
			}
			case CHANTER: {
				return 140001218; // Rage Spell I
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

	private void removeStigma(QuestEnv env) {
		Player player = env.getPlayer();
		for (Item item : player.getEquipment().getEquippedItemsByItemId(getStoneId(player))) {
			player.getEquipment().unEquipItem(item.getObjectId(), 0);
		}
		removeQuestItem(env, getStoneId(player), 1);
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

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
}
