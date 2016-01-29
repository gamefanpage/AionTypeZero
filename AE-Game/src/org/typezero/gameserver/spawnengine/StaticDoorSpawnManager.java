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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.controllers.StaticObjectController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.staticdoor.DoorType;
import org.typezero.gameserver.model.templates.staticdoor.StaticDoorTemplate;
import org.typezero.gameserver.model.templates.staticdoor.StaticDoorWorld;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;
import org.typezero.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * @author MrPoke
 */
public class StaticDoorSpawnManager {

	private static Logger log = LoggerFactory.getLogger(StaticDoorSpawnManager.class);

	/**
	 * @param spawnGroup
	 * @param instanceIndex
	 */
	public static void spawnTemplate(int worldId, int instanceIndex) {
		StaticDoorWorld staticDoorWorld = DataManager.STATICDOOR_DATA.getStaticDoorWorlds(worldId);
		if (staticDoorWorld == null)
			return;
		int counter = 0;
		for (StaticDoorTemplate data : staticDoorWorld.getStaticDoors()) {
			if (data.getDoorType() != DoorType.DOOR)
				continue;
			SpawnTemplate spawn = new SpawnTemplate(new SpawnGroup2(worldId, 300001), data.getX(), data.getY(), data.getZ(), (byte) 0, 0, null, 0, 0);
			spawn.setStaticId(data.getDoorId());
			int objectId = IDFactory.getInstance().nextId();
			StaticDoor staticDoor = new StaticDoor(objectId, new StaticObjectController(), spawn, data, instanceIndex);
			staticDoor.setKnownlist(new PlayerAwareKnownList(staticDoor));
			bringIntoWorld(staticDoor, spawn, instanceIndex);
			if (staticDoor.getDoorName() != null) {
				GeoService.getInstance().setDoorState(worldId, instanceIndex, staticDoor.getDoorName(), staticDoor.isOpen());
			}
			counter++;
		}
		if (counter > 0)
			log.info("Spawned static doors: " + worldId + " [" + instanceIndex + "] : " + counter);
	}

	/**
	 * @param visibleObject
	 * @param spawn
	 * @param instanceIndex
	 */
	private static void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex) {
		World world = World.getInstance();
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(),
			spawn.getHeading());
		world.spawn(visibleObject);
	}
}
