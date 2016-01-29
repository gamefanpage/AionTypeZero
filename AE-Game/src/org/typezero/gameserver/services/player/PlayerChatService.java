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

package org.typezero.gameserver.services.player;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Source
 */
public class PlayerChatService {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");

	/**
	 * This method will control players msg
	 *
	 * @param player
	 */
	public static boolean isFlooding(final Player player) {
		player.setLastMessageTime();

		if (player.floodMsgCount() > SecurityConfig.FLOOD_MSG) {
			player.setGagged(true);
			if (player.hasVar("chatgag"))
			{
				player.delVar("chatgag", true);
			}
			player.setVarLong("chatgag", System.currentTimeMillis() + 2 * 60 * 1000, true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLOODING);
			player.getController().cancelTask(TaskId.GAG);
			player.getController().addTask(TaskId.GAG, ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					player.setGagged(false);
					player.delVar("chatgag", true);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CAN_CHAT_NOW);
				}

			}, 2 * 60000L));

			return true;
		}

		return false;
	}

	public static void chatLogging(Player player, ChatType type, String message) {
		switch (type) {
			case GROUP:
				log.info(String.format("[MESSAGE] - GROUP <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case ALLIANCE:
				log.info(String.format("[MESSAGE] - ALLIANCE <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case GROUP_LEADER:
				log.info(String.format("[MESSAGE] - LEADER_ALERT: [%s]> %s", player.getName(), message));
				break;
			case LEGION:
				log.info(String.format("[MESSAGE] - LEGION <%s>: [%s]> %s", player.getLegion().getLegionName(),
						player.getName(), message));
				break;
			case LEAGUE:
			case LEAGUE_ALERT:
				log.info(String.format("[MESSAGE] - LEAGUE <%s>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case NORMAL:
			case SHOUT:
				if (player.getRace() == Race.ASMODIANS)
					log.info(String.format("[MESSAGE] - ALL (ASMO): [%s]> %s", player.getName(), message));
				else
					log.info(String.format("[MESSAGE] - ALL (ELYOS): [%s]> %s", player.getName(), message));
				break;
			default:
				if (player.isGM())
					log.info(String.format("[MESSAGE] - ALL (GM): [%s]> %s", player.getName(), message));
				break;
		}
	}

}
