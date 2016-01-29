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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.GenericValidator;
import org.typezero.gameserver.configs.main.CacheConfig;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.controllers.FlyController;
import org.typezero.gameserver.controllers.PlayerController;
import org.typezero.gameserver.controllers.effect.PlayerEffectController;
import org.typezero.gameserver.dao.*;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.PlayerInitialData;
import org.typezero.gameserver.dataholders.PlayerInitialData.LocationData;
import org.typezero.gameserver.dataholders.PlayerInitialData.PlayerCreationData;
import org.typezero.gameserver.dataholders.PlayerInitialData.PlayerCreationData.ItemType;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.*;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseRegistry;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.instance.InstanceCoolTimeType;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.items.storage.PlayerStorage;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.skill.PlayerSkillList;
import org.typezero.gameserver.model.stats.calc.functions.PlayerStatFunctions;
import org.typezero.gameserver.model.stats.listeners.TitleChangeListener;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.PunishmentService.PunishmentType;
import org.typezero.gameserver.services.SkillLearnService;
import org.typezero.gameserver.services.item.ItemFactory;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.collections.cachemap.CacheMap;
import org.typezero.gameserver.utils.collections.cachemap.CacheMapFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.knownlist.KnownList;
import org.typezero.gameserver.world.knownlist.Visitor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class is designed to do all the work related with loading/storing
 * players.<br> Same with storing,
 * {@link #storePlayer(org.typezero.gameserver.model.gameobjects.player.Player)}
 * stores all player data like appearance, items, etc...
 *
 * @author SoulKeeper, Saelya, Cura
 */
public class PlayerService {

	private static final CacheMap<Integer, Player> playerCache = CacheMapFactory.createSoftCacheMap("Player", "player");
    private static final String COOLDOWN_COUNT_UPDATE = "0 0 9 ? * *";

	/**
	 * Checks if name is already taken or not
	 *
	 * @param name character name
	 * @return true if is free, false in other case
	 */
	public static boolean isFreeName(String name) {
		return !DAOManager.getDAO(PlayerDAO.class).isNameUsed(name);
	}

	public static boolean isOldName(String name) {
		return DAOManager.getDAO(OldNamesDAO.class).isOldName(name);
	}

	/**
	 * Stores newly created player
	 *
	 * @param player player to store
	 * @return true if character was successful saved.
	 */
	public static boolean storeNewPlayer(Player player, String accountName, int accountId) {
		return DAOManager.getDAO(PlayerDAO.class).saveNewPlayer(player.getCommonData(), accountId, accountName)
			&& DAOManager.getDAO(PlayerAppearanceDAO.class).store(player)
			&& DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player)
			&& DAOManager.getDAO(InventoryDAO.class).store(player);
	}

	/**
	 * Stores player data into db
	 *
	 * @param player
	 */
	public static void storePlayer(Player player) {
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
		DAOManager.getDAO(PlayerSettingsDAO.class).saveSettings(player);
		DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
        DAOManager.getDAO(PlayerPassportsDAO.class).store(player);
		DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).storePlayerPunishments(player, PunishmentType.PRISON);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).storePlayerPunishments(player, PunishmentType.GATHER);
		DAOManager.getDAO(InventoryDAO.class).store(player);
		for (House house : player.getHouses()) {
			DAOManager.getDAO(HousesDAO.class).storeHouse(house);
			if (house.getRegistry() != null && house.getRegistry().getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				DAOManager.getDAO(PlayerRegisteredItemsDAO.class).store(house.getRegistry(),
					player.getCommonData().getPlayerObjId());
			}
		}
		DAOManager.getDAO(ItemStoneListDAO.class).save(player);
		DAOManager.getDAO(MailDAO.class).storeMailbox(player);
		DAOManager.getDAO(PortalCooldownsDAO.class).storePortalCooldowns(player);
		DAOManager.getDAO(CraftCooldownsDAO.class).storeCraftCooldowns(player);
		DAOManager.getDAO(PlayerNpcFactionsDAO.class).storeNpcFactions(player);
	}

	/**
	 * Returns the player with given objId (if such player exists)
	 *
	 * @param playerObjId
	 * @param account
	 * @return Player
	 */
	public static Player getPlayer(int playerObjId, Account account) {
		Player player = playerCache.get(playerObjId);
		if (player != null) {
			return player;
		}

		/**
		 * Player common data and appearance should be already loaded in account
		 */
		PlayerAccountData playerAccountData = account.getPlayerAccountData(playerObjId);
		PlayerCommonData pcd = playerAccountData.getPlayerCommonData();
		PlayerAppearance appearance = playerAccountData.getAppereance();

		player = new Player(new PlayerController(), pcd, appearance, account);
		LegionMember legionMember = LegionService.getInstance().getLegionMember(player.getObjectId());
		if (legionMember != null) {
			player.setLegionMember(legionMember);
		}

		MacroList macroses = DAOManager.getDAO(PlayerMacrossesDAO.class).restoreMacrosses(playerObjId);
		player.setMacroList(macroses);

		player.setSkillList(DAOManager.getDAO(PlayerSkillListDAO.class).loadSkillList(playerObjId));
		player.setKnownlist(new KnownList(player));
		player.setFriendList(DAOManager.getDAO(FriendListDAO.class).load(player));
		player.setBlockList(DAOManager.getDAO(BlockListDAO.class).load(player));
		player.setTitleList(DAOManager.getDAO(PlayerTitleListDAO.class).loadTitleList(playerObjId));
		DAOManager.getDAO(PlayerSettingsDAO.class).loadSettings(player);
		DAOManager.getDAO(AbyssRankDAO.class).loadAbyssRank(player);
		DAOManager.getDAO(PlayerNpcFactionsDAO.class).loadNpcFactions(player);
		DAOManager.getDAO(MotionDAO.class).loadMotionList(player);
		player.setVars(DAOManager.getDAO(PlayerVarsDAO.class).load(player.getObjectId()));
		player.setEffectController(new PlayerEffectController(player));
		player.setFlyController(new FlyController(player));
		PlayerStatFunctions.addPredefinedStatFunctions(player);

		player.setQuestStateList(DAOManager.getDAO(PlayerQuestListDAO.class).load(player));
        player.getCommonData().setCompletedPassports((DAOManager.getDAO(PlayerPassportsDAO.class).load(player)));
		player.setRecipeList(DAOManager.getDAO(PlayerRecipesDAO.class).load(player.getObjectId()));

		/**
		 * Account warehouse should be already loaded in account
		 */
		Storage accWarehouse = account.getAccountWarehouse();
		player.setStorage(accWarehouse, StorageType.ACCOUNT_WAREHOUSE);

		Storage inventory = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId, StorageType.CUBE);
		ItemService.loadItemStones(inventory.getItems());

		player.setStorage(inventory, StorageType.CUBE);

		Equipment equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player);
		ItemService.loadItemStones(equipment.getEquippedItemsWithoutStigma());
		equipment.setOwner(player);
		player.setEquipment(equipment);

		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			Storage petBag = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId,
				StorageType.getStorageTypeById(petBagId));
			ItemService.loadItemStones(petBag.getItems());

			player.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}

		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++) {
			StorageType whType = StorageType.getStorageTypeById(houseWhId);
			if (whType != null) {
				Storage cabinet = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId,
					StorageType.getStorageTypeById(houseWhId));
				ItemService.loadItemStones(cabinet.getItems());
				player.setStorage(cabinet, StorageType.getStorageTypeById(houseWhId));
			}
		}

		Storage warehouse = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId, StorageType.REGULAR_WAREHOUSE);
		ItemService.loadItemStones(warehouse.getItems());

		player.setStorage(warehouse, StorageType.REGULAR_WAREHOUSE);

		HouseRegistry houseRegistry = null;
		for (House house : player.getHouses()) {
			if (house.getStatus() == HouseStatus.ACTIVE || house.getStatus() == HouseStatus.SELL_WAIT) {
				houseRegistry = house.getRegistry();
				break;
			}
		}
		player.setHouseRegistry(houseRegistry);

		/**
		 * Apply equipment stats (items and manastones were loaded in account)
		 */
		player.getEquipment().onLoadApplyEquipmentStats();

		DAOManager.getDAO(PlayerPunishmentsDAO.class).loadPlayerPunishments(player, PunishmentType.PRISON);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).loadPlayerPunishments(player, PunishmentType.GATHER);

		// update passive stats after effect controller, stats and equipment are initialized
		player.getController().updatePassiveStats();
		// load saved effects
		DAOManager.getDAO(PlayerEffectsDAO.class).loadPlayerEffects(player);
		// load saved player cooldowns
		DAOManager.getDAO(PlayerCooldownsDAO.class).loadPlayerCooldowns(player);
		// load item cooldowns
		DAOManager.getDAO(ItemCooldownsDAO.class).loadItemCooldowns(player);
		// load portal cooldowns
		DAOManager.getDAO(PortalCooldownsDAO.class).loadPortalCooldowns(player);
		// load house object use cooldowns
		DAOManager.getDAO(HouseObjectCooldownsDAO.class).loadHouseObjectCooldowns(player);
		// load bind point
		DAOManager.getDAO(PlayerBindPointDAO.class).loadBindPoint(player);
		// load craft cooldowns
		DAOManager.getDAO(CraftCooldownsDAO.class).loadCraftCooldowns(player);

		if (player.getCommonData().getBonusTitleId() > 0) {
			TitleChangeListener.onBonusTitleChange(player.getGameStats(), player.getCommonData().getBonusTitleId(), true);
		}

		DAOManager.getDAO(PlayerLifeStatsDAO.class).loadPlayerLifeStat(player);
		DAOManager.getDAO(PlayerEmotionListDAO.class).loadEmotions(player);

		if (CacheConfig.CACHE_PLAYERS) {
			playerCache.put(playerObjId, player);
		}

		return player;
	}

	/**
	 * This method is used for creating new players
	 *
	 * @param playerCommonData
	 * @param playerAppearance
	 * @param account
	 * @return Player
	 */
	public static Player newPlayer(PlayerCommonData playerCommonData, PlayerAppearance playerAppearance, Account account) {
		PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;
		LocationData ld = playerInitialData.getSpawnLocation(playerCommonData.getRace());

		WorldPosition position = World.getInstance().createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(),
			ld.getHeading(), 0);
		playerCommonData.setPosition(position);

		Player newPlayer = new Player(new PlayerController(), playerCommonData, playerAppearance, account);

		// Starting skills
		newPlayer.setSkillList(new PlayerSkillList());
		SkillLearnService.addNewSkills(newPlayer);

		// Starting items
		PlayerCreationData playerCreationData = playerInitialData.getPlayerCreationData(playerCommonData.getPlayerClass());
		Storage playerInventory = new PlayerStorage(StorageType.CUBE);
		Storage regularWarehouse = new PlayerStorage(StorageType.REGULAR_WAREHOUSE);
		Storage accountWarehouse = new PlayerStorage(StorageType.ACCOUNT_WAREHOUSE);
		newPlayer.setStorage(playerInventory, StorageType.CUBE);
		newPlayer.setStorage(regularWarehouse, StorageType.REGULAR_WAREHOUSE);
		newPlayer.setStorage(accountWarehouse, StorageType.ACCOUNT_WAREHOUSE);

		Equipment equipment = new Equipment(newPlayer);
		if (playerCreationData != null) { // player transfer
			List<ItemType> items = playerCreationData.getItems();
			for (ItemType itemType : items) {
				int itemId = itemType.getTemplate().getTemplateId();
				Item item = ItemFactory.newItem(itemId, itemType.getCount());
				if (item == null) {
					continue;
				}

				// When creating new player - all equipment that has slot values will be equipped
				// Make sure you will not put into xml file more items than possible to equip.
				ItemTemplate itemTemplate = item.getItemTemplate();

				if ((itemTemplate.isArmor() || itemTemplate.isWeapon())
					&& !(equipment.isSlotEquipped(itemTemplate.getItemSlot()))) {
					item.setEquipped(true);
					ItemSlot itemSlot = ItemSlot.getSlotFor(itemTemplate.getItemSlot());
					item.setEquipmentSlot(itemSlot.getSlotIdMask());
					equipment.onLoadHandler(item);
				} else {
					playerInventory.onLoadHandler(item);
				}
			}
		}
		newPlayer.setEquipment(equipment);
		newPlayer.setMailbox(new Mailbox(newPlayer));

		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			Storage petBag = new PlayerStorage(StorageType.getStorageTypeById(petBagId));
			newPlayer.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}

		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++) {
			StorageType whType = StorageType.getStorageTypeById(houseWhId);
			if (whType != null) {
				Storage cabinet = new PlayerStorage(whType);
				newPlayer.setStorage(cabinet, StorageType.getStorageTypeById(houseWhId));
			}
		}

		/**
		 * Mark inventory and equipment as UPDATE_REQUIRED to be saved during
		 * character creation
		 */
		playerInventory.setPersistentState(PersistentState.UPDATE_REQUIRED);
		equipment.setPersistentState(PersistentState.UPDATE_REQUIRED);
		return newPlayer;
	}

	/**
	 * Cancel Player deletion process if its possible.
	 *
	 * @param accData PlayerAccountData
	 * @return True if deletion was successful canceled.
	 */
	public static boolean cancelPlayerDeletion(PlayerAccountData accData) {
		if (accData.getDeletionDate() == null) {
			return true;
		}

		if (accData.getDeletionDate().getTime() > System.currentTimeMillis()) {
			accData.setDeletionDate(null);
			storeDeletionTime(accData);
			return true;
		}
		return false;
	}

	/**
	 * Starts player deletion process if its possible. If deletion is possible
	 * character should be deleted after 5 minutes.
	 *
	 * @param accData PlayerAccountData
	 */
	public static void deletePlayer(PlayerAccountData accData) {
		if (accData.getDeletionDate() != null) {
			return;
		}

		accData.setDeletionDate(new Timestamp(System.currentTimeMillis() + SecurityConfig.DELETE_MINUTE * 60000));
		storeDeletionTime(accData);
	}

	/**
	 * Completely removes player from database
	 *
	 * @param playerId id of player to delete from db
	 */
	public static void deletePlayerFromDB(int playerId) {
		DAOManager.getDAO(InventoryDAO.class).deletePlayerItems(playerId);
		DAOManager.getDAO(PlayerDAO.class).deletePlayer(playerId);
	}

	/**
	 * Completely removes player from database
	 *
	 * @param accountId id of account to delete player on
	 * @return number of deleted chars
	 */
	public static int deleteAccountsCharsFromDB(int accountId) {
		List<Integer> charIds = DAOManager.getDAO(PlayerDAO.class).getPlayerOidsOnAccount(accountId);
		for (int playerId : charIds) {
			deletePlayerFromDB(playerId);
		}

		return charIds.size();
	}

	/**
	 * Updates deletion time in database
	 *
	 * @param accData PlayerAccountData
	 */
	private static void storeDeletionTime(PlayerAccountData accData) {
		DAOManager.getDAO(PlayerDAO.class).updateDeletionTime(accData.getPlayerCommonData().getPlayerObjId(),
			accData.getDeletionDate());
	}

	/**
	 * @param objectId
	 * @param creationDate
	 */
	public static void storeCreationTime(int objectId, Timestamp creationDate) {
		DAOManager.getDAO(PlayerDAO.class).storeCreationTime(objectId, creationDate);
	}

	/**
	 * Add macro for player
	 *
	 * @param player Player
	 * @param macroOrder Macro order
	 * @param macroXML Macro XML
	 */
	public static void addMacro(Player player, int macroOrder, String macroXML) {
		if (player.getMacroList().addMacro(macroOrder, macroXML)) {
			DAOManager.getDAO(PlayerMacrossesDAO.class).addMacro(player.getObjectId(), macroOrder, macroXML);
		} else {
			DAOManager.getDAO(PlayerMacrossesDAO.class).updateMacro(player.getObjectId(), macroOrder, macroXML);
		}
	}

	/**
	 * Remove macro with specified index from specified player
	 *
	 * @param player Player
	 * @param macroOrder Macro order index
	 */
	public static void removeMacro(Player player, int macroOrder) {
		if (player.getMacroList().removeMacro(macroOrder)) {
			DAOManager.getDAO(PlayerMacrossesDAO.class).deleteMacro(player.getObjectId(), macroOrder);
		}
	}

	/**
	 * Gets a player ONLY if he is in the cache
	 *
	 * @return Player or null if not cached
	 */
	public static Player getCachedPlayer(int playerObjectId) {
		return playerCache.get(playerObjectId);
	}

	public static String getPlayerName(Integer objectId) {
		return getPlayerNames(Collections.singleton(objectId)).get(objectId);
	}

	public static Map<Integer, String> getPlayerNames(Collection<Integer> playerObjIds) {

		// if there is no ids - return just empty map
		if (GenericValidator.isBlankOrNull(playerObjIds)) {
			return Collections.emptyMap();
		}

		final Map<Integer, String> result = Maps.newHashMap();

		// Copy ids to separate set
		// It's dangerous to modify input collection, can have side results
		final Set<Integer> playerObjIdsCopy = Sets.newHashSet(playerObjIds);

		// Get names of all online players
		// Certain names can be changed in runtime
		// this should prevent errors
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player object) {
				if (playerObjIdsCopy.contains(object.getObjectId())) {
					result.put(object.getObjectId(), object.getName());
					playerObjIdsCopy.remove(object.getObjectId());
				}
			}
		});

		result.putAll(DAOManager.getDAO(PlayerDAO.class).getPlayerNames(playerObjIdsCopy));
		return result;
	}
    public static void scheduleCoolDownCountUpdate() {
        CronService.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                String dayOfWeek = new SimpleDateFormat("EE").format(c);


                for (Player player : World.getInstance().getAllPlayers()) {
                    for (PortalCooldown pc : player.getPortalCooldownList().getPortalCoolDowns().values()) {
                        InstanceCooltime ct = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByInstanceId(pc.getId());
                        if (ct.getCoolTimeType().isDaily() || ct.getCoolTimeType().isRelative() || (ct.getCoolTimeType().isWeekly() && dayOfWeek.equals(ct.getTypeValue())))
                            pc.setCount(ct.getCount());
                    }
                    PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(player, false));
                }


                for (Map.Entry<Integer, InstanceCooltime> ic : DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimes().entrySet())
                    if (ic.getValue().getCoolTimeType().isDaily() || ic.getValue().getCoolTimeType().isRelative() || (ic.getValue().getCoolTimeType().isWeekly() && dayOfWeek.equals(ic.getValue().getTypeValue())))
                        DAOManager.getDAO(PortalCooldownsDAO.class).resetPortalCooldownCount(ic.getKey(), ic.getValue().getCount());
            }

        }, COOLDOWN_COUNT_UPDATE);
    }
}
