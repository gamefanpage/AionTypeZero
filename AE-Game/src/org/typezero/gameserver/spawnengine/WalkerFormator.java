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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.lambdaj.Lambda.*;
import ch.lambdaj.group.Group;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.walker.WalkerTemplate;

/**
 * Forms the walker groups on initial spawn<br>
 * Brings NPCs back to their positions if they die<br>
 * Cleanup and rework will be made after tests and error handling<br>
 * To use only with patch!
 *
 * @author vlog
 * @based on Imaginary's imagination
 * @modified Rolandas
 */
public class WalkerFormator {

	private static final Logger log = LoggerFactory.getLogger(WalkerFormator.class);
	private Map<String, List<ClusteredNpc>> groupedSpawnObjects;
	private Map<String, WalkerGroup> walkFormations = new HashMap<String, WalkerGroup>();

	/**
	 * If it's the instance first spawn, WalkerFormator verifies and creates groups;
	 * {@link #organizeAndSpawn()} must be called after to speed up spawning.
	 * If it's a respawn, nothing to verify, then the method places NPC to the first step
	 * and resets data to the saved, no organizing is needed.
	 * @param npc
	 * @param instance
	 * @return <tt>true</tt> if npc was brought into world by the method call.
	 */
	public boolean processClusteredNpc(Npc npc, int instance) {
		SpawnTemplate spawn = npc.getSpawn();
		if (spawn.getWalkerId() != null) {
			if (walkFormations.containsKey(spawn.getWalkerId())) {
				WalkerGroup wg = walkFormations.get(spawn.getWalkerId());
				npc.setWalkerGroup(wg);
				wg.respawn(npc);
				return false;
			}
			WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(spawn.getWalkerId());
			if (template == null) {
				log.warn("Missing walker ID: " + spawn.getWalkerId());
				return false;
			}
			if (template.getPool() < 2)
				return false;
			ClusteredNpc candidate = new ClusteredNpc(npc, instance, template);
			List<ClusteredNpc> candidateList = null;
			if (groupedSpawnObjects.containsKey(spawn.getWalkerId()))
				candidateList = groupedSpawnObjects.get(spawn.getWalkerId());
			else {
				candidateList = new ArrayList<ClusteredNpc>();
				groupedSpawnObjects.put(spawn.getWalkerId(), candidateList);
			}
			return candidateList.add(candidate);
		}
		return false;
	}

	/**
	 * Organizes spawns in all processed walker groups. Must be called only when spawning
	 * all npcs for the instance of world.
	 */
	public void organizeAndSpawn() {
		for (List<ClusteredNpc> candidates : groupedSpawnObjects.values()) {
			Group<ClusteredNpc> bySize = group(candidates, by(on(ClusteredNpc.class).getPositionHash()));
			Set<String> keys = bySize.keySet();
			int maxSize = 0;
			List<ClusteredNpc> npcs = null;
			for (String key : keys) {
				if (bySize.find(key).size() > maxSize) {
					npcs = bySize.find(key);
					maxSize = npcs.size();
				}
			}
			if (maxSize == 1) {
				for (ClusteredNpc snpc : candidates)
					snpc.spawn(snpc.getNpc().getSpawn().getZ());
			}
			else {
				WalkerGroup wg = new WalkerGroup(npcs);
				if (candidates.get(0).getWalkTemplate().getPool() != candidates.size())
					log.warn("Incorrect pool for route: " + candidates.get(0).getWalkTemplate().getRouteId());
				wg.form();
				wg.spawn();
				walkFormations.put(candidates.get(0).getWalkTemplate().getRouteId(), wg);
				// spawn the rest which didn't have the same coordinates
				for (ClusteredNpc snpc : candidates) {
					if (npcs.contains(snpc))
						continue;
					snpc.spawn(snpc.getNpc().getZ());
				}
			}
		}
		clear();
	}

	private void clear() {
		groupedSpawnObjects.clear();
	}

	private WalkerFormator() {
		groupedSpawnObjects = new HashMap<String, List<ClusteredNpc>>();
	}

	public static final WalkerFormator getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final WalkerFormator instance = new WalkerFormator();
	}
}
