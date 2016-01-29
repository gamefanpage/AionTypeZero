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
package org.typezero.gameserver.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import java.util.Set;

/**
 * Class that is responsible for storing/loading player data
 *
 * @author SoulKeeper, Saelya
 * @author cura
 */
public abstract class PlayerDAO implements IDFactoryAwareDAO {

	/**
	 * Returns true if name is used, false in other case
	 *
	 * @param name name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isNameUsed(String name);

	/**
	 * Returns player name by player object id
	 *
	 * @param playerObjectIds player object ids to get name
	 * @return map ObjectID-To-Name
	 */
	public abstract Map<Integer, String> getPlayerNames(Collection<Integer> playerObjectIds);

	public abstract void changePlayerId(Player player, int accountId);

	/**
	 * Stores player to db
	 *
	 * @param player
	 */
	public abstract void storePlayer(Player player);

	/**
	 * This method is used to store only newly created characters
	 *
	 * @param pcd player to save in database
	 * @return true if every things went ok.
	 */
	public abstract boolean saveNewPlayer(PlayerCommonData pcd, int accountId, String accountName);

	public abstract PlayerCommonData loadPlayerCommonData(int playerObjId);

	/**
	 * Removes player and all related data (Done by CASCADE DELETION)
	 *
	 * @param playerId player to delete
	 */
	public abstract void deletePlayer(int playerId);

	public abstract void updateDeletionTime(int objectId, Timestamp deletionDate);

	public abstract void storeCreationTime(int objectId, Timestamp creationDate);

	/**
	 * Loads creation and deletion time from database, for particular player and
	 * sets these values in given <tt>PlayerAccountData</tt> object.
	 *
	 * @param acData
	 */
	public abstract void setCreationDeletionTime(PlayerAccountData acData);

	/**
	 * Returns a list of objectId of players that are on the account with given
	 * accountId
	 *
	 * @param accountId
	 * @return List<Integer>
	 */
	public abstract List<Integer> getPlayerOidsOnAccount(int accountId);

	/**
	 * Stores the last online time
	 *
	 * @param objectId Object ID of player to store
	 * @param lastOnline Last online time of player to store
	 */
	public abstract void storeLastOnlineTime(final int objectId, final Timestamp lastOnline);

	/**
	 * Store online or offline player status
	 *
	 * @param player
	 * @param online
	 */
	public abstract void onlinePlayer(final Player player, final boolean online);

	/**
	 * Set all players offline status
	 *
	 * @param online
	 */
	public abstract void setPlayersOffline(final boolean online);

	/**
	 * get commondata by name for MailService
	 *
	 * @param name
	 * @return
	 */
	public abstract PlayerCommonData loadPlayerCommonDataByName(String name);

	/**
	 * Returns Player's Account ID
	 *
	 * @param name
	 * @return
	 */
	public abstract int getAccountIdByName(final String name);

	/**
	 * Identifier name for all PlayerDAO classes
	 *
	 * @return PlayerDAO.class.getName()
	 */
	public abstract String getPlayerNameByObjId(final int playerObjId);

	/**
	 * get playerId by name
	 *
	 * @param playerName
	 * @return
	 */
	public abstract int getPlayerIdByName(final String playerName);

	public abstract void storePlayerName(PlayerCommonData recipientCommonData);

	/**
	 * Return account characters count
	 *
	 * @param accountId
	 * @return
	 */
	public abstract int getCharacterCountOnAccount(final int accountId);

	/**
	 * Get characters count for a given Race
	 *
	 * @param race
	 * @return the number of characters for race
	 */
	public abstract int getCharacterCountForRace(Race race);

	/**
	 * Return online characters count
	 *
	 * @param none
	 * @return
	 */
	public abstract int getOnlinePlayerCount();

	/**
	 * Returns a Set of objectId of accounts that are inactive for more than
	 * dayOfInactivity days
	 *
	 * @param daysOfInactivity Number of days a char needs to be inactive
	 * @param limitation Maximum number of chars deleted in one run
	 * @return List of IDs
	 */
	public abstract Set<Integer> getInactiveAccounts(final int daysOfInactivity, int limitation);

	public abstract void setPlayerLastTransferTime(final int playerId, final long time);

	@Override
	public final String getClassName() {
		return PlayerDAO.class.getName();
	}
}
