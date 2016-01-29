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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.internal.chmv8.PlatformDependent;
import org.typezero.gameserver.controllers.HouseController;
import org.typezero.gameserver.dao.HousesDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.HouseDecoration;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.HousingFlags;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.templates.housing.*;
import org.typezero.gameserver.model.templates.spawns.SpawnType;
import org.typezero.gameserver.network.aion.serverpackets.SM_MARK_FRIENDLIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;

/**
 * @author Rolandas
 */
public class HousingService {

	private static final Logger log = LoggerFactory.getLogger(HousingService.class);
	// Contains non-instance houses initially (which are spawned)
	private static final Map<Integer, List<House>> housesByMapId = new HashMap<Integer, List<House>>();
	// Contains all houses by their addresses
	private final Map<Integer, House> customHouses;
	private final Map<Integer, House> studios;

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final HousingService instance = new HousingService();
	}

	public static HousingService getInstance() {
		return SingletonHolder.instance;
	}

	private HousingService() {
		log.info("Loading housing data...");
		customHouses = PlatformDependent.newConcurrentHashMap(DAOManager.getDAO(HousesDAO.class).loadHouses(DataManager.HOUSE_DATA.getLands(), false));
		studios = PlatformDependent.newConcurrentHashMap(DAOManager.getDAO(HousesDAO.class).loadHouses(DataManager.HOUSE_DATA.getLands(), true));
		log.info("Housing Service loaded.");
	}

	/**
	 * @param worldId
	 * @param instanceId
	 * @param registredId
	 */
	public void spawnHouses(int worldId, int instanceId, int registeredId) {
		Set<HousingLand> lands = DataManager.HOUSE_DATA.getLandsForWorldId(worldId);
		if (lands == null) {
			if (registeredId > 0) {
				House studio;
				synchronized (studios) {
					studio = studios.get(registeredId);
				}
				if (studio == null)
					return;
				HouseAddress addr = studio.getAddress();
				if (addr.getMapId() != worldId)
					return;
				VisibleObject existing = World.getInstance().findVisibleObject(studio.getObjectId());
				WorldPosition position = null;
				if (existing != null)
					position = existing.getPosition();
				if (position == null) {
					position = World.getInstance().createPosition(addr.getMapId(), addr.getX(), addr.getY(), addr.getZ(), (byte) 0, instanceId);
					studio.setPosition(position);
				}
				if (!position.isSpawned())
					SpawnEngine.bringIntoWorld(studio);
				// spawn only npcs
				studio.spawn(instanceId);

				Player enteredPlayer = World.getInstance().findPlayer(registeredId);
				if (enteredPlayer != null)
					enteredPlayer.setHouseRegistry(studio.getRegistry());
			}
			return;
		}

		int spawnedCounter = 0;
		for (HousingLand land : lands) {
			Building defaultBuilding = land.getDefaultBuilding();
			if (defaultBuilding.getType() == BuildingType.PERSONAL_INS)
				continue; // ignore studios

			for (HouseAddress address : land.getAddresses()) {
				if (address.getMapId() != worldId)
					continue;

				House customHouse = customHouses.get(address.getId());
				if (customHouse == null) {
					customHouse = new House(defaultBuilding, address, instanceId);
					// house without owner when acquired will be inserted to DB
					customHouse.setPersistentState(PersistentState.NEW);
					customHouses.put(address.getId(), customHouse);
				}
				customHouse.spawn(instanceId);
				spawnedCounter++;

				List<House> housesForMap = housesByMapId.get(worldId);
				if (housesForMap == null) {
					housesForMap = new ArrayList<House>();
					housesByMapId.put(worldId, housesForMap);
				}
				housesForMap.add(customHouse);
			}
		}
		if (spawnedCounter > 0) {
			log.info("Spawned houses " + worldId + " [" + instanceId + "] : " + spawnedCounter);
		}
	}

	public List<House> searchPlayerHouses(int playerObjId) {
		List<House> houses = new ArrayList<House>();
		synchronized (studios) {
			if (studios.containsKey(playerObjId)) {
				houses.add(studios.get(playerObjId));
				return houses;
			}
		}
		for (House house : customHouses.values()) {
			if (house.getOwnerId() == playerObjId)
				houses.add(house);
		}
		return houses;
	}

	public int getPlayerAddress(int playerId) {
		synchronized (studios) {
			if (studios.containsKey(playerId))
				return studios.get(playerId).getAddress().getId();
		}

		for (House house : customHouses.values()) {
			if (house.getStatus() == HouseStatus.INACTIVE)
				continue;
			if (house.getOwnerId() == playerId
							&& (house.getStatus() == HouseStatus.ACTIVE || house.getStatus() == HouseStatus.SELL_WAIT))
				return house.getAddress().getId();
		}
		return 0;
	}

	public void resetAppearance(House house) {
		FastList<HouseDecoration> customParts = house.getRegistry().getCustomParts();
		for (HouseDecoration deco : customParts) {
			deco.setPersistentState(PersistentState.DELETED);
		}
		for (HouseDecoration deco : customParts) {
			house.getRegistry().removeCustomPart(deco.getObjectId());
		}
	}

	public House getHouseByName(String houseName) {
		for (House house : customHouses.values()) {
			if (house.getName().equals(houseName))
				return house;
		}
		return null;
	}

	public House getHouseByAddress(int address) {
		for (House house : customHouses.values()) {
			if (house.getAddress().getId() == address)
				return house;
		}
		return null;
	}

	public House activateBoughtHouse(int playerId) {
		for (House house : customHouses.values()) {
			if (house.getOwnerId() == playerId && house.getStatus() == HouseStatus.INACTIVE) {
				house.revokeOwner();
				house.setOwnerId(playerId);
				house.setFeePaid(true);
				house.setNextPay(null);
				house.setSellStarted(null);
				house.reloadHouseRegistry();
				house.save();
				return house;
			}
		}
		return null;
	}

	public House getPlayerStudio(int playerId) {
		synchronized (studios) {
			if (studios.containsKey(playerId))
				return studios.get(playerId);
		}
		return null;
	}

	public void removeStudio(int playerId) {
		if (playerId != 0) {
			synchronized (studios) {
				studios.remove(playerId);
			}
		}
	}

	public void registerPlayerStudio(Player player) {
		createStudio(player);
	}

	public void recreatePlayerStudio(Player player) {
		// Price for both races is the same, use any template
		HousingLand land = DataManager.HOUSE_DATA.getLand(329001);
		final long fee = land.getSaleOptions().getGoldPrice();
		if (player.getInventory().getKinah() < fee) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		createStudio(player);

		player.getInventory().decreaseKinah(fee);
	}

	private void createStudio(Player player) {
		if (!searchPlayerHouses(player.getObjectId()).isEmpty()) //should not happen
			return;
		HousingLand land = DataManager.HOUSE_DATA.getLand(player.getRace() == Race.ELYOS ? 329001 : 339001);
		House studio = new House(land.getDefaultBuilding(), land.getAddresses().get(0), 0);
		studio.setOwnerId(player.getObjectId());

		synchronized (studios) {
			studios.put(player.getObjectId(), studio);
		}
		studio.setStatus(HouseStatus.ACTIVE);
		studio.setAcquiredTime(new Timestamp(System.currentTimeMillis()));
		studio.setFeePaid(true);
		studio.setNextPay(null);
		studio.setPersistentState(PersistentState.NEW);
		player.setHousingStatus(HousingFlags.HOUSE_OWNER.getId());
		PacketSendUtility.sendPacket(player, new SM_HOUSE_ACQUIRE(player.getObjectId(), studio.getAddress().getId(), true));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_INS_OWN_SUCCESS);
		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, studio));
	}

	public void switchHouseBuilding(House currentHouse, int newBuildingId) {
		Building otherBuilding = DataManager.HOUSE_BUILDING_DATA.getBuilding(newBuildingId);
		currentHouse.setBuilding(otherBuilding);
		currentHouse.getRegistry().despawnObjects(false);
		currentHouse.reloadHouseRegistry(); // load new defaults
		DAOManager.getDAO(HousesDAO.class).storeHouse(currentHouse);
		HouseController controller = ((HouseController) currentHouse.getController());
		controller.broadcastAppearance();
		controller.spawnObjects();
	}

	public FastList<House> getCustomHouses() {
		FastList<House> houses = FastList.newInstance();
		for (List<House> mapHouses : housesByMapId.values())
			houses.addAll(mapHouses);
		return houses;
	}

	public void onInstanceDestroy(int ownerId) {
		House studio;
		synchronized (studios) {
			studio = studios.get(ownerId);
		}
		if (studio != null) {
			studio.setSpawn(SpawnType.MANAGER, null);
			studio.setSpawn(SpawnType.TELEPORT, null);
			studio.setSpawn(SpawnType.SIGN, null);
			studio.save();
		}
	}

	public void onPlayerLogin(Player player) {
		House activeHouse = null;
		byte housingStatus = HousingFlags.BUY_STUDIO_ALLOWED.getId();
		for (House house : player.getHouses()) {
			if (house.getStatus() == HouseStatus.ACTIVE || house.getStatus() == HouseStatus.SELL_WAIT) {
				activeHouse = house;
			}
		}
		if (activeHouse == null) {
			QuestState qs;
			qs = player.getQuestStateList().getQuestState(player.getRace() == Race.ELYOS ? 18802 : 28802);
			if (qs != null && qs.getStatus().equals(QuestStatus.COMPLETE)) {
				housingStatus |= HousingFlags.BIDDING_ALLOWED.getId();
			}
		}
		else {
			if (activeHouse.getStatus() == HouseStatus.SELL_WAIT)
				housingStatus = HousingFlags.SELLING_HOUSE.getId();
			else
				housingStatus = HousingFlags.HOUSE_OWNER.getId();
		}
		player.setHousingStatus(housingStatus);

		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, activeHouse));
		if (!player.getFriendList().getIsFriendListSent())
			PacketSendUtility.sendPacket(player, new SM_FRIEND_LIST());
		PacketSendUtility.sendPacket(player, new SM_MARK_FRIENDLIST());
	}
}
