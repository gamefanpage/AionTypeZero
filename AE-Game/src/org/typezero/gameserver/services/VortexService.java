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
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.model.vortex.VortexStateType;
import org.typezero.gameserver.services.rift.RiftInformer;
import org.typezero.gameserver.services.vortexservice.DimensionalVortex;
import org.typezero.gameserver.services.vortexservice.Invasion;
import org.typezero.gameserver.services.rift.RiftManager;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class VortexService {

	private static final int duration = CustomConfig.VORTEX_DURATION;
	private final Map<Integer, DimensionalVortex<?>> activeInvasions = new FastMap<Integer, DimensionalVortex<?>>().shared();
	private Map<Integer, VortexLocation> vortex;

	public void initVortexLocations() {
		if (CustomConfig.VORTEX_ENABLED) {
			vortex = DataManager.VORTEX_DATA.getVortexLocations();

			// Spawn peace
			for (VortexLocation loc : getVortexLocations().values()) {
				spawn(loc, VortexStateType.PEACE);
			}

			// Brusthonin schedule
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startInvasion(1);
				}

			}, CustomConfig.VORTEX_BRUSTHONIN_SCHEDULE);

			// Theobomos schedule
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startInvasion(0);
				}

			}, CustomConfig.VORTEX_THEOBOMOS_SCHEDULE);
		}
		else {
			vortex = Collections.emptyMap();
		}
	}

	public void startInvasion(final int id) {
		final DimensionalVortex<?> invasion;

		synchronized (this) {
			if (activeInvasions.containsKey(id)) {
				return;
			}
			invasion = new Invasion(vortex.get(id));
			activeInvasions.put(id, invasion);
		}

		invasion.start();

		// Scheduled invasion end
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!invasion.isGeneratorDestroyed()) {
					stopInvasion(id);
				}
			}

		}, duration * 3600 * 1000);
	}

	public void stopInvasion(int id) {
		if (!isInvasionInProgress(id)) {
			return;
		}

		DimensionalVortex<?> invasion;
		synchronized (this) {
			invasion = activeInvasions.remove(id);
		}

		if (invasion == null || invasion.isFinished()) {
			return;
		}

		invasion.stop();
	}

	public void spawn(VortexLocation loc, VortexStateType state) {
		// Spawn Dimensional Vortex
		if (state.equals(VortexStateType.INVASION)) {
			RiftManager.getInstance().spawnVortex(loc);
			RiftInformer.sendRiftsInfo(loc.getHomeWorldId());
		}

		// Spawn NPC
		List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getVortexSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group : locSpawns) {
			for (SpawnTemplate st : group.getSpawnTemplates()) {
				VortexSpawnTemplate vortextemplate = (VortexSpawnTemplate) st;
				if (vortextemplate.getStateType().equals(state)) {
					loc.getSpawned().add(SpawnEngine.spawnObject(vortextemplate, 1));
				}
			}
		}
	}

	public void despawn(VortexLocation loc) {
		// Unset Vortex controller
		loc.setVortexController(null);

		// Despawn all NPC
		for (VisibleObject npc : loc.getSpawned()) {
			((Npc) npc).getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}

		loc.getSpawned().clear();
	}

	public boolean isInvasionInProgress(int id) {
		return activeInvasions.containsKey(id);
	}

	public Map<Integer, DimensionalVortex<?>> getActiveInvasions() {
		return activeInvasions;
	}

	public int getDuration() {
		return duration;
	}

	public void removeDefenderPlayer(Player player) {
		for (DimensionalVortex<?> invasion : activeInvasions.values()) {
			if (invasion.getDefenders().containsKey(player.getObjectId())) {
				invasion.kickPlayer(player, false);
				return;
			}
		}
	}

	public void removeInvaderPlayer(Player player) {
		for (DimensionalVortex<?> invasion : activeInvasions.values()) {
			if (invasion.getInvaders().containsKey(player.getObjectId())) {
				invasion.kickPlayer(player, true);
				return;
			}
		}
	}

	public boolean isInvaderPlayer(Player player) {
		for (DimensionalVortex<?> invasion : activeInvasions.values()) {
			if (invasion.getInvaders().containsKey(player.getObjectId())) {
				return true;
			}
		}

		return false;
	}

	public boolean isInsideVortexZone(Player player) {
		int playerWorldId = player.getWorldId();

		if (playerWorldId == 210060000 || playerWorldId == 220050000) {
			VortexLocation loc = getLocationByWorld(playerWorldId);
			if (loc != null) {
				return loc.getPlayers().containsKey(player.getObjectId());
			}
		}

		return false;
	}

	public VortexLocation getLocationByRift(int npcId) {
		return getVortexLocation(npcId == 831141 ? 1 : 0);
	}

	public VortexLocation getLocationByWorld(int worldId) {
		if (worldId == 210060000) {
			return getVortexLocation(0);
		}
		else if (worldId == 220050000) {
			return getVortexLocation(1);
		}
		else {
			return null;
		}
	}

	public VortexLocation getVortexLocation(int id) {
		return vortex.get(id);
	}

	public Map<Integer, VortexLocation> getVortexLocations() {
		return vortex;
	}

	public void validateLoginZone(Player player) {
		VortexLocation loc = getLocationByWorld(player.getWorldId());
		if (loc != null && player.getRace().equals(loc.getInvadersRace())) {
			if (loc.isInsideLocation(player) && loc.isActive()
					&& loc.getVortexController().getPassedPlayers().containsKey(player.getObjectId())) {
				return;
			}

			int mapId = loc.getHomeWorldId();
			float x = loc.getHomePoint().getX();
			float y = loc.getHomePoint().getY();
			float z = loc.getHomePoint().getZ();
			byte h = loc.getHomePoint().getHeading();
			World.getInstance().setPosition(player, mapId, x, y, z, h);
		}
	}

	public static VortexService getInstance() {
		return VortexServiceHolder.INSTANCE;
	}

	private static class VortexServiceHolder {

		private static final VortexService INSTANCE = new VortexService();
	}

}
