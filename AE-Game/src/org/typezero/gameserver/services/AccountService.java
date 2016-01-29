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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.GameServer;
import org.typezero.gameserver.configs.main.CacheConfig;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.dao.LegionMemberDAO;
import org.typezero.gameserver.dao.PlayerAppearanceDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dao.PlayerPunishmentsDAO;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.account.AccountTime;
import org.typezero.gameserver.model.account.CharacterBanInfo;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.items.storage.PlayerStorage;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.collections.cachemap.CacheMap;
import org.typezero.gameserver.utils.collections.cachemap.CacheMapFactory;
import org.typezero.gameserver.world.World;

/**
 * This class is a front-end for daos and it's responsibility is to retrieve the Account objects
 *
 * @author Luno
 * @modified cura
 */
public class AccountService {

	private static final Logger log = LoggerFactory.getLogger(AccountService.class);

	private static CacheMap<Integer, Account> accountsMap = CacheMapFactory.createSoftCacheMap("Account", "account");

	/**
	 * Returns {@link Account} object that has given id.
	 *
	 * @param accountId
	 * @param accountTime
	 * @param accountName
	 * @param accessLevel
	 * @param membership
	 * @return Account
	 */
	public static Account getAccount(int accountId, String accountName, AccountTime accountTime, byte accessLevel,
		byte membership, long toll) {
		log.debug("[AS] request for account: " + accountId);

		Account account = accountsMap.get(accountId);
		if (account == null) {
			account = loadAccount(accountId);
			if (CacheConfig.CACHE_ACCOUNTS)
				accountsMap.put(accountId, account);
		}
		account.setName(accountName);
		account.setAccountTime(accountTime);
		account.setAccessLevel(accessLevel);
		account.setMembership(membership);
		account.setToll(toll);
		removeDeletedCharacters(account);
		return account;
	}

	/**
	 * Removes from db characters that should be deleted (their deletion time has passed).
	 *
	 * @param account
	 */
	public static void removeDeletedCharacters(Account account) {
		/* Removes chars that should be removed */
		Iterator<PlayerAccountData> it = account.iterator();
		while (it.hasNext()) {
			PlayerAccountData pad = it.next();
			Race race = pad.getPlayerCommonData().getRace();
			long deletionTime = 0;
            if (pad.getDeletionDate() != null)
                deletionTime = pad.getDeletionDate().getTime();

			if (deletionTime != 0 && deletionTime <= System.currentTimeMillis()) {
				it.remove();
				account.decrementCountOf(race);
				PlayerService.deletePlayerFromDB(pad.getPlayerCommonData().getPlayerObjId());
				if (GSConfig.ENABLE_RATIO_LIMITATION && pad.getPlayerCommonData().getLevel() >= GSConfig.RATIO_MIN_REQUIRED_LEVEL) {
					if (account.getNumberOf(race) == 0) {
						GameServer.updateRatio(pad.getPlayerCommonData().getRace(), -1);
					}
				}
			}
		}
		if (account.isEmpty()) {
			removeAccountWH(account.getId());
			account.getAccountWarehouse().clear();
		}
	}

	private static void removeAccountWH(int accountId) {
		DAOManager.getDAO(InventoryDAO.class).deleteAccountWH(accountId);
	}
	/**
	 * Loads account data and returns.
	 *
	 * @param accountId
	 * @param accountName
	 * @return
	 */
	public static Account loadAccount(int accountId) {
		Account account = new Account(accountId);

		PlayerDAO playerDAO = DAOManager.getDAO(PlayerDAO.class);
		PlayerAppearanceDAO appereanceDAO = DAOManager.getDAO(PlayerAppearanceDAO.class);

		List<Integer> playerIdList = playerDAO.getPlayerOidsOnAccount(accountId);

		for (int playerId : playerIdList) {
			PlayerCommonData playerCommonData = playerDAO.loadPlayerCommonData(playerId);
			CharacterBanInfo cbi = DAOManager.getDAO(PlayerPunishmentsDAO.class).getCharBanInfo(playerId);
			if(playerCommonData.isOnline())  {
				if(World.getInstance().findPlayer(playerId) == null) {
					playerCommonData.setOnline(false);
					log.warn(playerCommonData.getName()+" has online status, but I cant find it in World. Skip online status");
				}
			}
			PlayerAppearance appereance = appereanceDAO.load(playerId);

			LegionMember legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(playerId);

			/**
			 * Load only equipment and its stones to display on character selection screen
			 */
			List<Item> equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(playerId);

			PlayerAccountData acData = new PlayerAccountData(playerCommonData, cbi, appereance, equipment, legionMember);
			playerDAO.setCreationDeletionTime(acData);

			account.addPlayerAccountData(acData);

			if (account.getAccountWarehouse() == null) {
				Storage accWarehouse = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerId, StorageType.ACCOUNT_WAREHOUSE);
				ItemService.loadItemStones(accWarehouse.getItems());
				account.setAccountWarehouse(accWarehouse);
			}
		}

		// For new accounts - create empty account warehouse
		if (account.getAccountWarehouse() == null) {
			account.setAccountWarehouse(new PlayerStorage(StorageType.ACCOUNT_WAREHOUSE));
		}
		return account;
	}
}
