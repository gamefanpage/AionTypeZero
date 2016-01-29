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
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.*;
import org.typezero.gameserver.model.beritra.BeritraLocation;
import org.typezero.gameserver.model.beritra.BeritraStateType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.beritraspawns.*;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.services.beritraservice.BeritraInvasion;
import org.typezero.gameserver.services.beritraservice.Invade;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rinzler (Encom)
 */

public class BeritraService
{
	private Map<Integer, BeritraLocation> beritra;
	private static final int duration = SiegeConfig.BERITRA_DURATION;
	private final Map<Integer, BeritraInvasion<?>> activeInvasions = new FastMap<Integer, BeritraInvasion<?>>().shared();
	private static final Logger log = LoggerFactory.getLogger(BeritraService.class);

	public void initBeritraLocations() {
		if (SiegeConfig.BERITRA_ENABLED) {
			log.info("Loading Beritra Invasions...");
			beritra = DataManager.BERITRA_DATA.getBeritraLocations();
			for (BeritraLocation loc: getBeritraLocations().values()) {
				spawn(loc, BeritraStateType.PEACE);
			}
			log.info("Loaded " + beritra.size() + " Beritra Invasions.");
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					for (BeritraLocation loc: getBeritraLocations().values()) {
				        startBeritraInvasion(loc.getId());
					}
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					    @Override
					    public void visit(Player player) {
						    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_WORLDRAID_INVADE_VRITRA_SPECIAL);
					    }
					});
				}
			}, SiegeConfig.BERITRA_SCHEDULE);
		} else {
			beritra = Collections.emptyMap();
		}
	}

	public void startBeritraInvasion(final int id) {
		final BeritraInvasion<?> invade;
		synchronized (this) {
			if (activeInvasions.containsKey(id)) {
				return;
			}
			invade = new Invade(beritra.get(id));
			activeInvasions.put(id, invade);
		}
		invade.start();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				stopBeritraInvasion(id);
			}
		}, duration * 3600 * 1000);
	}

	public void stopBeritraInvasion(int id) {
		if (!isInvasionInProgress(id)) {
			return;
		}
		BeritraInvasion<?> invade;
		synchronized (this) {
			invade = activeInvasions.remove(id);
		} if (invade == null || invade.isFinished()) {
			return;
		}
		invade.stop();
	}

	public void spawn(BeritraLocation loc, BeritraStateType bstate) {
		if (bstate.equals(BeritraStateType.INVASION)) {
		}
		List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getBeritraSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group : locSpawns) {
			for (SpawnTemplate st : group.getSpawnTemplates()) {
				BeritraSpawnTemplate beritratemplate = (BeritraSpawnTemplate) st;
				if (beritratemplate.getBStateType().equals(bstate)) {
					loc.getSpawned().add(SpawnEngine.spawnObject(beritratemplate, 1));
				}
			}
		}
	}

	public void despawn(BeritraLocation loc) {
		for (VisibleObject npc : loc.getSpawned()) {
			((Npc) npc).getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}
		loc.getSpawned().clear();
	}

	public boolean isInvasionInProgress(int id) {
		return activeInvasions.containsKey(id);
	}

	public Map<Integer, BeritraInvasion<?>> getActiveInvasions() {
		return activeInvasions;
	}

	public int getDuration() {
		return duration;
	}

	public BeritraLocation getBeritraLocation(int id) {
		return beritra.get(id);
	}

	public Map<Integer, BeritraLocation> getBeritraLocations() {
		return beritra;
	}

	public static BeritraService getInstance() {
		return BeritraServiceHolder.INSTANCE;
	}

	private static class BeritraServiceHolder {
		private static final BeritraService INSTANCE = new BeritraService();
	}
}
