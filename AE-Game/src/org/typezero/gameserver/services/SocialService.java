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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.BlockListDAO;
import org.typezero.gameserver.dao.FriendListDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.BlockedPlayer;
import org.typezero.gameserver.model.gameobjects.player.Friend;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_BLOCK_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import org.typezero.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import org.typezero.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.world.World;

/**
 * Handles activities related to social groups ingame such as the buddy list, legions, etc
 *
 * @author Ben
 */
public class SocialService {

	/**
	 * Blocks the given object ID for the given player.<br />
	 * <ul>
	 * <li>Does not send packets</li>
	 * </ul>
	 *
	 * @param player
	 * @param blockedPlayer
	 * @param reason
	 * @return Success
	 */
	public static boolean addBlockedUser(Player player, Player blockedPlayer, String reason) {
		if (DAOManager.getDAO(BlockListDAO.class).addBlockedUser(player.getObjectId(), blockedPlayer.getObjectId(), reason)) {
			player.getBlockList().add(new BlockedPlayer(blockedPlayer.getCommonData(), reason));

			player.getClientConnection().sendPacket(
				new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.BLOCK_SUCCESSFUL, blockedPlayer.getName()));
			player.getClientConnection().sendPacket(new SM_BLOCK_LIST());

			return true;
		}
		return false;
	}

	/**
	 * Unblocks the given object ID for the given player.<br />
	 * <ul>
	 * <li>Does not send packets</li>
	 * </ul>
	 *
	 * @param player
	 * @param blockedUserId
	 *          ID of player to unblock
	 * @return Success
	 */
	public static boolean deleteBlockedUser(Player player, int blockedUserId) {
		if (DAOManager.getDAO(BlockListDAO.class).delBlockedUser(player.getObjectId(), blockedUserId)) {
			player.getBlockList().remove(blockedUserId);
			player.getClientConnection().sendPacket(
				new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.UNBLOCK_SUCCESSFUL, DAOManager.getDAO(PlayerDAO.class)
					.loadPlayerCommonData(blockedUserId).getName()));

			player.getClientConnection().sendPacket(new SM_BLOCK_LIST());
			return true;
		}
		return false;
	}

	/**
	 * Sets the reason for blocking a user
	 *
	 * @param player
	 *          Player whos block list is to be edited
	 * @param target
	 *          Whom to block
	 * @param reason
	 *          Reason to set
	 * @return Success - May be false if the reason was the same and therefore not edited
	 */
	public static boolean setBlockedReason(Player player, BlockedPlayer target, String reason) {

		if (!target.getReason().equals(reason)) {
			if (DAOManager.getDAO(BlockListDAO.class).setReason(player.getObjectId(), target.getObjId(), reason)) {
				target.setReason(reason);
				player.getClientConnection().sendPacket(new SM_BLOCK_LIST());
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds two players to each others friend lists, and updates the database<br />
	 *
	 * @param friend1
	 * @param friend2
	 */
	public static void makeFriends(Player friend1, Player friend2) {
		DAOManager.getDAO(FriendListDAO.class).addFriends(friend1, friend2);

		friend1.getFriendList().addFriend(new Friend(friend2.getCommonData()));
		friend2.getFriendList().addFriend(new Friend(friend1.getCommonData()));

		friend1.getClientConnection().sendPacket(new SM_FRIEND_LIST());
		friend2.getClientConnection().sendPacket(new SM_FRIEND_LIST());

		friend1.getClientConnection()
			.sendPacket(new SM_FRIEND_RESPONSE(friend2.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
		friend2.getClientConnection()
			.sendPacket(new SM_FRIEND_RESPONSE(friend1.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
	}

	/**
	 * Deletes two players from eachother's friend lists, and updates the database
	 * <ul>
	 * <li>Note: Does not send notification packets, and does not send new list packet
	 * </ul>
	 * </li>
	 *
	 * @param deleter
	 *          Player deleting a friend
	 * @param exFriend2Id
	 *          Object ID of the friend he is deleting
	 */
	public static void deleteFriend(Player deleter, int exFriend2Id) {

		// If the DAO is successful
		if (DAOManager.getDAO(FriendListDAO.class).delFriends(deleter.getObjectId(), exFriend2Id)) {
			// Try to get the target player from the cache
			Player friend2Player = PlayerService.getCachedPlayer(exFriend2Id);
			// If the cache doesn't have this player, try to get him from the world
			if (friend2Player == null)
				friend2Player = World.getInstance().findPlayer(exFriend2Id);

			String friend2Name = friend2Player != null ? friend2Player.getName() : DAOManager.getDAO(PlayerDAO.class)
				.loadPlayerCommonData(exFriend2Id).getName();

			// Delete from deleter's friend list and send packets
			deleter.getFriendList().delFriend(exFriend2Id);

			deleter.getClientConnection().sendPacket(new SM_FRIEND_LIST());
			deleter.getClientConnection().sendPacket(new SM_FRIEND_RESPONSE(friend2Name, SM_FRIEND_RESPONSE.TARGET_REMOVED));

			if (friend2Player != null) {
				friend2Player.getFriendList().delFriend(deleter.getObjectId());

				if (friend2Player.isOnline()) {
					friend2Player.getClientConnection().sendPacket(
						new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.DELETED, deleter.getName()));
					friend2Player.getClientConnection().sendPacket(new SM_FRIEND_LIST());
				}
			}
		}

	}
}
