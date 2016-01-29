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

package org.typezero.gameserver.services.instance;

import java.util.Iterator;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.instance.InstanceEngine;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.league.League;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.AutoGroupService;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import org.typezero.gameserver.world.WorldMap2DInstance;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldMapInstanceFactory;
import org.typezero.gameserver.world.WorldMapType;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public class InstanceService {

	private static final Logger log = LoggerFactory.getLogger(InstanceService.class);
	private static final FastList<Integer> instanceAggro = new FastList<Integer>();
	private static final FastList<Integer> instanceCoolDownFilter = new FastList<Integer>();
	private static final int SOLO_INSTANCES_DESTROY_DELAY = 10*60*1000; //10 minutes

	public static void load() {
		for (String s : CustomConfig.INSTANCES_MOB_AGGRO.split(",")) {
			instanceAggro.add(Integer.parseInt(s));
		}
		for (String s : CustomConfig.INSTANCES_COOL_DOWN_FILTER.split(",")) {
			instanceCoolDownFilter.add(Integer.parseInt(s));
		}
	}

	/**
	 * @param worldId
	 * @param ownerId
	 *          - playerObjectId or Legion id in future
	 * @return
	 */
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, int ownerId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);

		if (!map.isInstanceType())
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);

		int nextInstanceId = map.getNextInstanceId();
		log.info("Creating new instance:" + worldId + " id:" + nextInstanceId + " owner:" + ownerId);
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId, ownerId);

		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), (byte) 0, ownerId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);

		// finally start the checker
		if (map.isInstanceType()) {
			startInstanceChecker(worldMapInstance);
		}

		return worldMapInstance;
	}

	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId) {
		return getNextAvailableInstance(worldId, 0);
	}

	/**
	 * Instance will be destroyed All players moved to bind location All objects - deleted
	 */
	public static void destroyInstance(WorldMapInstance instance) {
		if (instance.getEmptyInstanceTask() != null) {
			instance.getEmptyInstanceTask().cancel(false);
		}

		int worldId = instance.getMapId();
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType())
			return;
		int instanceId = instance.getInstanceId();

		map.removeWorldMapInstance(instanceId);

		log.info("Destroying instance:" + worldId + " " + instanceId);

		Iterator<VisibleObject> it = instance.objectIterator();
		while (it.hasNext()) {
			VisibleObject obj = it.next();
			if (obj instanceof Player) {
				Player player = (Player) obj;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.LEAVE_INSTANCE_NOT_PARTY));
				moveToExitPoint((Player) obj);
			}
			else {
				obj.getController().onDelete();
			}
		}
		instance.getInstanceHandler().onInstanceDestroy();
		if (instance instanceof WorldMap2DInstance) {
			WorldMap2DInstance w2d = (WorldMap2DInstance) instance;
			if (w2d.isPersonal())
				HousingService.getInstance().onInstanceDestroy(w2d.getOwnerId());
		}
	}

	/**
	 * @param instance
	 * @param player
	 */
	public static void registerPlayerWithInstance(WorldMapInstance instance, Player player) {
		Integer obj = player.getObjectId();
		instance.register(obj);
		instance.setSoloPlayerObj(obj);
	}

	/**
	 * @param instance
	 * @param group
	 */
	public static void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group) {
		instance.registerGroup(group);
	}

	/**
	 * @param instance
	 * @param group
	 */
	public static void registerAllianceWithInstance(WorldMapInstance instance, PlayerAlliance group) {
		instance.registerGroup(group);
	}

	/**
	 * @param instance
	 * @param leaguee
	 */
	public static void registerLeagueWithInstance(WorldMapInstance instance, League group) {
		instance.registerGroup(group);
	}

	/**
	 * @param worldId
	 * @param objectId
	 * @return instance or null
	 */
	public static WorldMapInstance getRegisteredInstance(int worldId, int objectId) {
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();

			if (instance.isRegistered(objectId)) {
				return instance;
			}
		}
		return null;
	}

	public static WorldMapInstance getPersonalInstance(int worldId, int ownerId) {
		if (ownerId == 0)
			return null;

		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isPersonal() && instance.getOwnerId() == ownerId)
				return instance;
		}
		return null;
	}

	/**
	 * @param player
	 */
	public static void onPlayerLogin(Player player) {
		int worldId = player.getWorldId();
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if (worldTemplate.isInstance()) {
			int lookupId;
			if (WorldMapType.getWorld(player.getWorldId()) == null)
			{
				boolean isPersonal = false;
			}
			boolean isPersonal = WorldMapType.getWorld(player.getWorldId()).isPersonal();
			if (player.isInGroup2()) {
				lookupId = player.getPlayerGroup2().getTeamId();
			}
			else if (player.isInAlliance2()) {
				lookupId = player.getPlayerAlliance2().getTeamId();
				if (player.isInLeague()) {
					lookupId = player.getPlayerAlliance2().getLeague().getObjectId();
				}
			}
			else if (isPersonal && player.getCommonData().getWorldOwnerId() != 0) {
				lookupId = player.getCommonData().getWorldOwnerId();
			}
			else {
				lookupId = player.getObjectId();
			}

			WorldMapInstance registeredInstance = isPersonal ? getPersonalInstance(worldId, lookupId)
					: getRegisteredInstance(worldId, lookupId);

			if (isPersonal) {
				if (registeredInstance == null)
					registeredInstance = getNextAvailableInstance(player.getWorldId(), lookupId);

				if (!registeredInstance.isRegistered(player.getObjectId()))
					registerPlayerWithInstance(registeredInstance, player);
			}

			if (registeredInstance != null) {
				World.getInstance().setPosition(player, worldId, registeredInstance.getInstanceId(),
						player.getX(), player.getY(), player.getZ(),
					player.getHeading());
				player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogin(player);
				return;
			}

			moveToExitPoint(player);
		}
	}

	/**
	 * @param player
	 * @param portalTemplates
	 */
	public static void moveToExitPoint(Player player) {
		TeleportService2.moveToInstanceExit(player, player.getWorldId(), player.getRace());
	}

	/**
	 * @param worldId
	 * @param instanceId
	 * @return
	 */
	public static boolean isInstanceExist(int worldId, int instanceId) {
		return World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}

	/**
	 * @param worldMapInstance
	 */
	private static void startInstanceChecker(WorldMapInstance worldMapInstance) {

		int delay = 150000; // 2.5 minutes
		int period = 60000; // 1 minute
		worldMapInstance.setEmptyInstanceTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new EmptyInstanceCheckerTask(worldMapInstance), delay, period));
	}

	private static class EmptyInstanceCheckerTask implements Runnable {

		private WorldMapInstance worldMapInstance;
		private long soloInstanceDestroyTime;

		private EmptyInstanceCheckerTask(WorldMapInstance worldMapInstance) {
			this.worldMapInstance = worldMapInstance;
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}

		private boolean canDestroySoloInstance() {
			return System.currentTimeMillis() > this.soloInstanceDestroyTime;
		}

		private void updateSoloInstanceDestroyTime() {
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}

		@Override
		public void run() {
			int instanceId = worldMapInstance.getInstanceId();
			int worldId = worldMapInstance.getMapId();
			WorldMap map = World.getInstance().getWorldMap(worldId);
			PlayerGroup registeredGroup = worldMapInstance.getRegisteredGroup();
			if (registeredGroup == null) {
				if(worldMapInstance.playersCount() > 0) {
						updateSoloInstanceDestroyTime();
						return;
				}
				if(worldMapInstance.playersCount() == 0) {
					if(canDestroySoloInstance()) {
						map.removeWorldMapInstance(instanceId);
						destroyInstance(worldMapInstance);
						return;
					}
					else {
						return;
					}
				}
				Iterator<Player> playerIterator = worldMapInstance.playerIterator();
				int mapId = worldMapInstance.getMapId();
				while (playerIterator.hasNext()) {
					Player player = playerIterator.next();
					if (player.isOnline() && player.getWorldId() == mapId)
						return;
				}
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
			else if (registeredGroup.size() == 0) {
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
		}

	}

	public static void onLogOut(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogOut(player);
	}

	public static void onEnterInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterInstance(player);
		AutoGroupService.getInstance().onEnterInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() == 0)
				continue;
			if (item.getItemTemplate().getOwnershipWorld() != player.getWorldId())
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
		}
	}

	public static void onLeaveInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() == player.getWorldId())
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
		}
		if (AutoGroupConfig.AUTO_GROUP_ENABLE) {
			AutoGroupService.getInstance().onLeaveInstance(player);
		}
	}

	public static void onEnterZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterZone(player, zone);
	}

	public static void onLeaveZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveZone(player, zone);
	}

	public static boolean isAggro(int mapId) {
		return instanceAggro.contains(mapId);
	}

	public static int getInstanceRate(Player player, int mapId) {
		int instanceCooldownRate = player.havePermission(MembershipConfig.INSTANCES_COOLDOWN)
				&& !instanceCoolDownFilter.contains(mapId) ? CustomConfig.INSTANCES_RATE : 1;
		if (instanceCoolDownFilter.contains(mapId)) {
			instanceCooldownRate = 1;
		}
		return instanceCooldownRate;
	}

}
