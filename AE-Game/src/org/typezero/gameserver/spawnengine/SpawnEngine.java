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

package org.typezero.gameserver.spawnengine;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.administration.DeveloperConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.rift.RiftManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * This class is responsible for NPCs spawn management. Current implementation
 * is temporal and will be replaced in the future.
 *
 * @author Luno modified by ATracer, Source, Wakizashi, xTz, nrg
 */
public class SpawnEngine {

	private static Logger log = LoggerFactory.getLogger(SpawnEngine.class);

	/**
	 * Creates VisibleObject instance and spawns it using given
	 * {@link SpawnTemplate} instance.
	 *
	 * @param spawn
	 * @return created and spawned VisibleObject
	 */
	public static VisibleObject spawnObject(SpawnTemplate spawn, int instanceIndex) {
		final VisibleObject visObj = getSpawnedObject(spawn, instanceIndex);
		if (spawn.isEventSpawn())
			spawn.getEventTemplate().addSpawnedObject(visObj);

		spawn.setVisibleObject(visObj);
		return visObj;
	}

	private static VisibleObject getSpawnedObject(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();

		if (objectId > 400000 && objectId < 499999) {
			return VisibleObjectSpawner.spawnGatherable(spawn, instanceIndex);
		}
		else if (spawn instanceof RiftSpawnTemplate) {
			return VisibleObjectSpawner.spawnRiftNpc((RiftSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof BaseSpawnTemplate) {
			return VisibleObjectSpawner.spawnBaseNpc((BaseSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof SiegeSpawnTemplate) {
			return VisibleObjectSpawner.spawnSiegeNpc((SiegeSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof VortexSpawnTemplate) {
			return VisibleObjectSpawner.spawnInvasionNpc((VortexSpawnTemplate) spawn, instanceIndex);
		}
		else {
			return VisibleObjectSpawner.spawnNpc(spawn, instanceIndex);
		}
	}

	/**
	 * @param worldId
	 * @param npcId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @return
	 */
	static SpawnTemplate createSpawnTemplate(int worldId, int npcId, float x, float y, float z, byte heading) {
		return new SpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, 0, null, 0, 0);
	}

	static SpawnTemplate createSpawnTemplate(int worldId, int npcId, float x, float y, float z, byte heading,
			int creatorId, String masterName) {
		SpawnTemplate template = createSpawnTemplate(worldId, npcId, x, y, z, heading);
		template.setCreatorId(creatorId);
		template.setMasterName(masterName);
		return template;
	}

	/**
	 * Should be used when you need to add a siegespawn through code and not
	 * from static_data spawns (e.g. CustomBalaurAssault)
	 */
	public static SiegeSpawnTemplate addNewSiegeSpawn(int worldId, int npcId, int siegeId, SiegeRace race,
			SiegeModType mod, float x, float y, float z, byte heading) {
		SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, 0,
				null, 0, 0);
		spawnTemplate.setSiegeId(siegeId);
		spawnTemplate.setSiegeRace(race);
		spawnTemplate.setSiegeModType(mod);
		return spawnTemplate;
	}

	/**
	 * Should be used when need to define whether spawn will be deleted after
	 * death Using this method spawns will not be saved with //save_spawn
	 * command
	 *
	 * @param worldId
	 * @param npcId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param respawnTime
	 * @param permanent
	 * @return SpawnTemplate
	 */
	public static SpawnTemplate addNewSpawn(int worldId, int npcId, float x, float y, float z, byte heading,
			int respawnTime) {
		SpawnTemplate spawnTemplate = createSpawnTemplate(worldId, npcId, x, y, z, heading);
		spawnTemplate.setRespawnTime(respawnTime);
		return spawnTemplate;
	}

	/**
	 * Create non-permanent spawn template with no respawn
	 *
	 * @param worldId
	 * @param npcId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @return
	 */
	public static SpawnTemplate addNewSingleTimeSpawn(int worldId, int npcId, float x, float y, float z, byte heading) {
		return addNewSpawn(worldId, npcId, x, y, z, heading, 0);
	}

	public static SpawnTemplate addNewSingleTimeSpawn(int worldId, int npcId, float x, float y, float z, byte heading,
			int creatorId, String masterName) {
		SpawnTemplate template = addNewSpawn(worldId, npcId, x, y, z, heading, 0);
		template.setCreatorId(creatorId);
		template.setMasterName(masterName);
		return template;
	}

	static void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex) {
		bringIntoWorld(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(),
				spawn.getHeading());
	}

	public static void bringIntoWorld(VisibleObject visibleObject, int worldId, int instanceIndex, float x, float y,
			float z, byte h) {
		World world = World.getInstance();
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, worldId, instanceIndex, x, y, z, h);
		world.spawn(visibleObject);
	}

	public static void bringIntoWorld(VisibleObject visibleObject) {
		if (visibleObject.getPosition() == null)
			throw new IllegalArgumentException("Position is null");
		World world = World.getInstance();
		world.storeObject(visibleObject);
		world.spawn(visibleObject);
	}

	/**
	 * Spawn all NPC's from templates
	 */
	public static void spawnAll() {
		if (!DeveloperConfig.SPAWN_ENABLE) {
			log.info("Spawns are disabled");
			return;
		}
		for (WorldMapTemplate worldMapTemplate : DataManager.WORLD_MAPS_DATA) {
			if (worldMapTemplate.isInstance()) {
				continue;
			}
			spawnBasedOnTemplate(worldMapTemplate);
		}
		DataManager.SPAWNS_DATA2.clearTemplates();
		printWorldSpawnStats();
	}

	/**
	 * @param worldId
	 */
	public static void spawnWorldMap(int worldId) {
		WorldMapTemplate template = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if (template != null && !template.isInstance()) {
			spawnBasedOnTemplate(template);
		}
	}

	/**
	 * @param worldMapTemplate
	 */
	private static void spawnBasedOnTemplate(WorldMapTemplate worldMapTemplate) {
		int maxTwin = worldMapTemplate.getTwinCount();
		final int mapId = worldMapTemplate.getMapId();
		int numberToSpawn = maxTwin > 0 ? maxTwin : 1;

		for (int instanceId = 1; instanceId <= numberToSpawn; instanceId++) {
			spawnInstance(mapId, instanceId, (byte) 0);
		}
	}

	public static void spawnInstance(int worldId, int instanceId, byte difficultId) {
		spawnInstance(worldId, instanceId, difficultId, 0);
	}

	/**
	 * @param worldId
	 * @param instanceId
	 */
	public static void spawnInstance(int worldId, int instanceId, byte difficultId, int ownerId) {
		List<SpawnGroup2> worldSpawns = DataManager.SPAWNS_DATA2.getSpawnsByWorldId(worldId);
		StaticDoorSpawnManager.spawnTemplate(worldId, instanceId);

		int spawnedCounter = 0;
		if (worldSpawns != null) {
			for (SpawnGroup2 spawn : worldSpawns) {
				int difficult = spawn.getDifficultId();
				if (difficult != 0 && difficult != difficultId) {
					continue;
				}
				if (spawn.isTemporarySpawn()) {
					TemporarySpawnEngine.addSpawnGroup(spawn);
				}
				else {
					if (spawn.getHandlerType() != null) {
						switch (spawn.getHandlerType()) {
							case RIFT:
								RiftManager.addRiftSpawnTemplate(spawn);
								break;
							case STATIC:
								StaticObjectSpawnManager.spawnTemplate(spawn, instanceId);
							default:
								break;
						}
					}
					else if (spawn.hasPool() && checkPool(spawn)) {
						for (int i = 0; i < spawn.getPool(); i++) {
							SpawnTemplate template = spawn.getRndTemplate();
							if(template == null)
								break;
							spawnObject(template, instanceId);
							spawnedCounter++;
						}
					}
					else {
						for (SpawnTemplate template : spawn.getSpawnTemplates()) {
							spawnObject(template, instanceId);
							spawnedCounter++;
						}
					}
				}
			}
			WalkerFormator.getInstance().organizeAndSpawn();
		}
		log.info("Spawned " + worldId + " [" + instanceId + "] : " + spawnedCounter);
		HousingService.getInstance().spawnHouses(worldId, instanceId, ownerId);
	}

	private static boolean checkPool(SpawnGroup2 spawn) {
		if (spawn.getSpawnTemplates().size() < spawn.getPool()) {
			log.warn("Pool size more then spots, npcId: " + spawn.getNpcId() + ", worldId: " + spawn.getWorldId());
			return false;
		}
		return true;
	}

	public static void printWorldSpawnStats() {
		StatsCollector visitor = new StatsCollector();
		World.getInstance().doOnAllObjects(visitor);
		log.info("Loaded " + visitor.getNpcCount() + " npc spawns");
		log.info("Loaded " + visitor.getGatherableCount() + " gatherable spawns");
	}

	static class StatsCollector implements Visitor<VisibleObject> {

		int npcCount;
		int gatherableCount;

		@Override
		public void visit(VisibleObject object) {
			if (object instanceof Npc) {
				npcCount++;
			}
			else if (object instanceof Gatherable) {
				gatherableCount++;
			}
		}

		public int getNpcCount() {
			return npcCount;
		}

		public int getGatherableCount() {
			return gatherableCount;
		}

	}

}
