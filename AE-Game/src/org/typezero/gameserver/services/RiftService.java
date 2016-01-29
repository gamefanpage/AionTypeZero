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

import com.aionemu.commons.services.CronService;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.shedule.RiftSchedule;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.model.templates.rift.OpenRift;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import org.typezero.gameserver.services.rift.RiftInformer;
import org.typezero.gameserver.services.rift.RiftManager;
import org.typezero.gameserver.services.rift.RiftOpenRunnable;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class RiftService {

	private RiftSchedule schedule;
	private Map<Integer, RiftLocation> locations;
	private final Lock closing = new ReentrantLock();
	private static final int duration = CustomConfig.RIFT_DURATION;
	private FastMap<Integer, RiftLocation> activeRifts = new FastMap<Integer, RiftLocation>();

	public void initRiftLocations() {
		if (CustomConfig.RIFT_ENABLED) {
			locations = DataManager.RIFT_DATA.getRiftLocations();
		}
		else {
			locations = Collections.emptyMap();
		}
	}

	public void initRifts() {
		if (CustomConfig.RIFT_ENABLED) {
			schedule = RiftSchedule.load();
			for (RiftSchedule.Rift rift : schedule.getRiftsList()) {
				for (OpenRift open : rift.getRift()) {
					CronService.getInstance().schedule(new RiftOpenRunnable(rift.getWorldId(), open.spawnGuards()), open.getSchedule());
				}
			}
		}
	}

	public boolean isValidId(int id) {
		if (isRift(id)) {
			return RiftService.getInstance().getRiftLocations().keySet().contains(id);
		}
		else {
			for (RiftLocation loc : RiftService.getInstance().getRiftLocations().values()) {
				if (loc.getWorldId() == id) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isRift(int id) {
		return id < 10000;
	}

	public boolean openRifts(int id, boolean guards) {
		if (isValidId(id)) {
			if (isRift(id)) {
				RiftLocation rift = getRiftLocation(id);
				if (rift.getSpawned().isEmpty()) {
					openRifts(rift, guards);

					// Broadcast rift spawn on map
					RiftInformer.sendRiftsInfo(rift.getWorldId());
					return true;
				}
			}
			else {
				boolean opened = false;
				for (RiftLocation rift : getRiftLocations().values()) {
					if (rift.getWorldId() == id && rift.getSpawned().isEmpty()) {
						openRifts(rift, guards);
						opened = true;
					}
				}

				// Broadcast rift spawn on map
				RiftInformer.sendRiftsInfo(id);
				return opened;
			}
		}
		return false;
	}

	public boolean closeRifts(int id) {
		if (isValidId(id)) {
			if (isRift(id)) {
				RiftLocation rift = getRiftLocation(id);
				if (!rift.getSpawned().isEmpty()) {
					closeRift(rift);
					return true;
				}
			}
			else {
				boolean opened = false;
				for (RiftLocation rift : getRiftLocations().values()) {
					if (rift.getWorldId() == id && !rift.getSpawned().isEmpty()) {
						closeRift(rift);
						opened = true;
					}
				}
				return opened;
			}
		}
		return false;
	}

	public void openRifts(RiftLocation location, boolean guards) {
		location.setOpened(true);

		// Spawn NPC guards
		if (guards) {
			List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getRiftSpawnsByLocId(location.getId());
			for (SpawnGroup2 group : locSpawns) {
				for (SpawnTemplate st : group.getSpawnTemplates()) {
					RiftSpawnTemplate template = (RiftSpawnTemplate) st;
					location.getSpawned().add(SpawnEngine.spawnObject(template, 1));
				}
			}
		}

		// Spawn rifts
		RiftManager.getInstance().spawnRift(location);
		activeRifts.putEntry(location.getId(), location);
	}

	public void closeRift(RiftLocation location) {
		location.setOpened(false);

		// Despawn NPC
		for (VisibleObject obj : location.getSpawned()) {
			Npc spawned = (Npc) obj;
            if(spawned == null) continue;
			spawned.setDespawnDelayed(true);
			if (spawned.getAggroList().getList().isEmpty()) {
				spawned.getController().cancelTask(TaskId.RESPAWN);
				obj.getController().onDelete();
			}
		}

		// Clear spawned list
		location.getSpawned().clear();
	}

	public void closeRifts() {
		closing.lock();

		try {
			for (RiftLocation rift : activeRifts.values()) {
				closeRift(rift);
			}

			activeRifts.clear();
		}
		finally {
			closing.unlock();
		}
	}

	public int getDuration() {
		return duration;
	}

	public RiftLocation getRiftLocation(int id) {
		return locations.get(id);
	}

	public Map<Integer, RiftLocation> getRiftLocations() {
		return locations;
	}

	public static RiftService getInstance() {
		return RiftServiceHolder.INSTANCE;
	}

	private static class RiftServiceHolder {

		private static final RiftService INSTANCE = new RiftService();
	}

}
