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

package org.typezero.gameserver.services;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, sweetkr
 */
public class ClassChangeService {

	//TODO dialog enum
	/**
	 * @param player
	 */
	public static void showClassChangeDialog(Player player) {
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			PlayerClass playerClass = player.getPlayerClass();
			Race playerRace = player.getRace();
			if (player.getLevel() >= 9 && playerClass.isStartingClass()) {
				if (playerRace == Race.ELYOS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2375, 1006));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2716, 1006));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 1006));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 1006));
							break;
						case ENGINEER:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 1006));
							break;
						case ARTIST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 1006));
							break;
					}
				}
				else if (playerRace == Race.ASMODIANS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 2008));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 2008));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 2008));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 2008));
							break;
						case ENGINEER:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3569, 2008));//sniffed on NA
							break;
						case ARTIST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3910, 2008));//sniffed on NA
							break;
					}
				}
			}
		}
	}

	/**
	 * @param player
	 * @param dialogId
	 */
	public static void changeClassToSelection(final Player player, final int dialogId) {
		Race playerRace = player.getRace();
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			if (playerRace == Race.ELYOS) {
				switch (dialogId) {
					case 2376:
						setClass(player, PlayerClass.getPlayerClassById((byte)1));
						break;
					case 2461:
						setClass(player, PlayerClass.getPlayerClassById((byte)2));
						break;
					case 2717:
						setClass(player, PlayerClass.getPlayerClassById((byte)4));
						break;
					case 2802:
						setClass(player, PlayerClass.getPlayerClassById((byte)5));
						break;
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById((byte)7));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById((byte)8));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById((byte)10));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById((byte)11));
						break;
					case 3825 :
						setClass(player, PlayerClass.getPlayerClassById((byte)13));
						break;
					case 3740 :
						setClass(player, PlayerClass.getPlayerClassById((byte)14));
						break;
					case 4081 :
						setClass(player, PlayerClass.getPlayerClassById((byte)16));
						break;
						
				}
				completeQuest(player, 1006);
				completeQuest(player, 1007);

				// Stigma Quests Elyos
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 1929);
				}
			}
			else if (playerRace == Race.ASMODIANS) {
				switch (dialogId) {
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById((byte)1));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById((byte)2));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById((byte)4));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById((byte)5));
						break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById((byte)7));
						break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById((byte)8));
						break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById((byte)10));
						break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById((byte)11));
						break;
					case 3591:
						setClass(player, PlayerClass.getPlayerClassById((byte)13));
						break;
					case 3570:
						setClass(player, PlayerClass.getPlayerClassById((byte)14));
						break;
					case 3911 :
						setClass(player, PlayerClass.getPlayerClassById((byte)16));
						break;
				}
				//Optimate @Enomine
				completeQuest(player, 2008);
				completeQuest(player, 2009);

				// Stigma Quests Asmodians
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 2900);
				}
			}
		}
	}

	private static void completeQuest(Player player, int questId) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 0, null, 0, null));
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE.value(), 0));
		}
		else {
			qs.setStatus(QuestStatus.COMPLETE);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars()
				.getQuestVars()));
		}
	}

	public static void setClass(Player player, PlayerClass playerClass) {
		if (validateSwitch(player, playerClass)) {
			player.getCommonData().setPlayerClass(playerClass);
			player.getController().upgradePlayer();
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0, 0));
		}
	}

	private static boolean validateSwitch(Player player, PlayerClass playerClass) {
		int level = player.getLevel();
		PlayerClass oldClass = player.getPlayerClass();
		if (level != 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class at level 9");
			return false;
		}
		if (!oldClass.isStartingClass()) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return false;
		}
		switch (oldClass) {
			case WARRIOR:
				if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
					break;
			case SCOUT:
				if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
					break;
			case MAGE:
				if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
					break;
			case PRIEST:
				if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
					break;
			case ENGINEER:
				if (playerClass == PlayerClass.GUNNER || playerClass == PlayerClass.RIDER)
					break;
			case ARTIST:
				if (playerClass == PlayerClass.BARD)
					break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return false;
		}
		return true;
	}
}
