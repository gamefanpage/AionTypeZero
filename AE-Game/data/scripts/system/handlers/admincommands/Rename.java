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

package admincommands;

import java.util.Iterator;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dao.OldNamesDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Friend;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author xTz
 */
public class Rename extends AdminCommand {

	public Rename() {
		super("rename");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
				+ "or use //rename [target] <rename>");
			return;
		}

		Player player = null;
		String recipient = null;
		String rename = null;

		if (params.length == 2) {
			recipient = Util.convertName(params[0]);
			rename = Util.convertName(params[1]);

			if (!DAOManager.getDAO(PlayerDAO.class).isNameUsed(recipient)) {
				PacketSendUtility.sendMessage(admin, "Could not find a Player by that name.");
				return;
			}
			PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipient);
			player = recipientCommonData.getPlayer();

			if (!check(admin, rename))
				return;

			if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
				DAOManager.getDAO(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
			recipientCommonData.setName(rename);
			DAOManager.getDAO(PlayerDAO.class).storePlayerName(recipientCommonData);
			if (recipientCommonData.isOnline()) {
				PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
				PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
				sendPacket(admin, player, rename, recipient);
			}
			else
				PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
		}
		if (params.length == 1) {
			rename = Util.convertName(params[0]);

			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}

			if (target instanceof Player) {
				player = (Player) target;
				if (!check(admin, rename))
					return;

				if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
					DAOManager.getDAO(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
				player.getCommonData().setName(rename);
				PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
				DAOManager.getDAO(PlayerDAO.class).storePlayerName(player.getCommonData());
			}
			else
				PacketSendUtility.sendMessage(admin, "The command can be applied only on the player.");

			recipient = target.getName();
			sendPacket(admin, player, rename, recipient);
		}
	}

	private static boolean check(Player admin, String rename) {
		if (!NameRestrictionService.isValidName(rename)) {
			PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400151));
			return false;
		}
		if (!PlayerService.isFreeName(rename)) {
			PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400155));
			return false;
		}
		if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED && PlayerService.isOldName(rename)) {
			PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400155));
			return false;
		}
		return true;
	}

	public void sendPacket(Player admin, Player player, String rename, String recipient) {
		Iterator<Friend> knownFriends = player.getFriendList().iterator();

		while (knownFriends.hasNext()) {
			Friend nextObject = knownFriends.next();
			if (nextObject.getPlayer() != null && nextObject.getPlayer().isOnline()) {
				PacketSendUtility.sendPacket(nextObject.getPlayer(), new SM_PLAYER_INFO(player, false));
			}
		}

		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
		PacketSendUtility.sendMessage(player, "You have been renamed to " + rename);
		PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
			+ "or use //rename [target] <rename>");
	}
}
