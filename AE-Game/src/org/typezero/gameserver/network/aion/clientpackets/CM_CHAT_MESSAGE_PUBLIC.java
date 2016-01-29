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
package org.typezero.gameserver.network.aion.clientpackets;

import com.aionemu.commons.objects.filter.ObjectFilter;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.WordFilterService;
import org.typezero.gameserver.services.player.PlayerChatService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.chathandlers.ChatProcessor;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * Packet that reads normal chat messages.<br>
 *
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_PUBLIC extends AionClientPacket {

	/**
	 * Chat type
	 */
	private ChatType type;

	/**
	 * Chat message
	 */
	private String message;

	public CM_CHAT_MESSAGE_PUBLIC(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = ChatType.getChatTypeByInt(readC());
		message = readS();
	}

	@Override
	protected void runImpl() {

		final Player player = getConnection().getActivePlayer();


            message = WordFilterService.replaceBanWord(player, message);


        if (player == null) {
            return;
        }

		if (ChatProcessor.getInstance().handleChatCommand(player, message))
			return;

		//message = NameRestrictionService.filterMessage(message);

		if (LoggingConfig.LOG_CHAT)
			PlayerChatService.chatLogging(player, type, message);

		if (RestrictionsManager.canChat(player) && !PlayerChatService.isFlooding(player)) {
			switch (this.type) {
				case GROUP:
					if (!player.isInTeam())
						return;
					broadcastToGroupMembers(player);
					break;
				case ALLIANCE:
					if (!player.isInAlliance2())
						return;
					broadcastToAllianceMembers(player);
					break;
				case GROUP_LEADER:
					if (!player.isInTeam())
						return;
					// Alert must go to entire group or alliance.
					if (player.isInGroup2())
						broadcastToGroupMembers(player);
					else
						broadcastToAllianceMembers(player);
					break;
				case LEGION:
					broadcastToLegionMembers(player);
					break;
				case LEAGUE:
				case LEAGUE_ALERT:
					if (!player.isInLeague()) {
						return;
					}
					broadcastToLeagueMembers(player);
					break;
				case NORMAL:
				case SHOUT:
					if (player.isGM()) {
						broadcastFromGm(player);
					}
					else {
						if (CustomConfig.SPEAKING_BETWEEN_FACTIONS) {
							broadcastToNonBlockedPlayers(player);
						}
						else {
							broadcastToNonBlockedRacePlayers(player);
						}
					}
					break;
				case COMMAND:
					if (player.getAbyssRank().getRank() == AbyssRankEnum.COMMANDER || player.getAbyssRank().getRank() == AbyssRankEnum.SUPREME_COMMANDER) {
						broadcastFromCommander(player);
					}
					break;
				default:
					if (player.isGM()) {
						broadcastFromGm(player);
					}
					else {
						AuditLogger.info(player, String.format("Send message type %s. Message: %s", type, message));
					}
					break;
			}
		}
	}

	private void broadcastFromCommander(final Player player) {
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>() {
			@Override
			public boolean acceptObject(Player object) {
				return (senderRace == object.getRace().getRaceId() || object.isGM());
			}

		});
	}
	/**
	 * Sends message to all players from admin
	 *
	 * @param player
	 */
	private void broadcastFromGm(final Player player) {
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true);
	}

	/**
	 * Sends message to all players that are not in blocklist
	 *
	 * @param player
	 */
	private void broadcastToNonBlockedPlayers(final Player player) {
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return !object.getBlockList().contains(player.getObjectId());
			}

		});
	}

	/**
	 * Sends message to races members (other race will receive an unknown message)
	 *
	 * @param player
	 */
	private void broadcastToNonBlockedRacePlayers(final Player player) {
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return ((senderRace == object.getRace().getRaceId() && !object.getBlockList().contains(player.getObjectId())) || object.isGM());
			}

		});
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, "Unknow Message", type), false, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return senderRace != object.getRace().getRaceId() && !object.getBlockList().contains(player.getObjectId()) && !object.isGM();
			}

		});
	}

	/**
	 * Sends message to all group members (regular player group, or alliance
	 * sub-group Error 105, random value for players to report. Should never
	 * happen.
	 *
	 * @param player
	 */
	private void broadcastToGroupMembers(final Player player) {
		if (player.isInTeam()) {
			player.getCurrentGroup().sendPacket(new SM_MESSAGE(player, message, type));
		}
		else {
			PacketSendUtility.sendMessage(player, "You are not in an alliance or group. (Error 105)");
		}
	}

	/**
	 * Sends message to all alliance members
	 *
	 * @param player
	 */
	private void broadcastToAllianceMembers(final Player player) {
		player.getPlayerAlliance2().sendPacket(new SM_MESSAGE(player, message, type));
	}

	/**
	 * Sends message to all league members
	 *
	 * @param player
	 */
	private void broadcastToLeagueMembers(final Player player) {
		player.getPlayerAlliance2().getLeague().sendPacket(new SM_MESSAGE(player, message, type));
	}

	/**
	 * Sends message to all legion members
	 *
	 * @param player
	 */
	private void broadcastToLegionMembers(final Player player) {
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_MESSAGE(player, message, type));
		}
	}

}
