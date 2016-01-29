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

package org.typezero.gameserver.services.rift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.controllers.RVController;
import org.typezero.gameserver.controllers.effect.EffectController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.NpcKnownList;
import java.util.List;

/**
 * @author Source
 */
public class RiftManager {

	private static Logger log = LoggerFactory.getLogger(RiftManager.class);
	private static List<Npc> rifts = new ArrayList<Npc>();
	private static Map<String, SpawnTemplate> riftGroups = new HashMap<String, SpawnTemplate>();

	public static void addRiftSpawnTemplate(SpawnGroup2 spawn) {
		if (spawn.hasPool()) {
			SpawnTemplate template = spawn.getSpawnTemplates().get(0);
			riftGroups.put(template.getAnchor(), template);
		}
		else {
			for (SpawnTemplate template : spawn.getSpawnTemplates()) {
				riftGroups.put(template.getAnchor(), template);
			}
		}
	}

	public void spawnRift(RiftLocation loc) {
		RiftEnum rift = RiftEnum.getRift(loc.getId());
		spawnRift(rift, null, loc);
	}

	public void spawnVortex(VortexLocation loc) {
		RiftEnum rift = RiftEnum.getVortex(loc.getDefendersRace());
		spawnRift(rift, loc, null);
	}

	private void spawnRift(RiftEnum rift, VortexLocation vl, RiftLocation rl) {
		SpawnTemplate masterTemplate = riftGroups.get(rift.getMaster());
		SpawnTemplate slaveTemplate = riftGroups.get(rift.getSlave());

		if (masterTemplate == null || slaveTemplate == null) {
			return;
		}

		int spawned = 0;
		int instanceCount = World.getInstance().getWorldMap(masterTemplate.getWorldId()).getInstanceCount();

		if (slaveTemplate.hasPool()) {
			slaveTemplate = slaveTemplate.changeTemplate();
		}

		for (int i = 1; i <= instanceCount; i++) {
			Npc slave = spawnInstance(i, slaveTemplate, new RVController(null, rift));
			Npc master = spawnInstance(i, masterTemplate, new RVController(slave, rift));

			if (rift.isVortex()) {
				vl.setVortexController((RVController) master.getController());
				spawned = vl.getSpawned().size();
				vl.getSpawned().add(master);
				vl.getSpawned().add(slave);
			}
			else {
				spawned = rl.getSpawned().size();
				rl.getSpawned().add(master);
				rl.getSpawned().add(slave);
			}
		}

		log.info("Rift opened: " + rift.name() + " successfully spawned " + spawned + " Npc.");
	}

	private Npc spawnInstance(int instance, SpawnTemplate template, RVController controller) {
		NpcTemplate masterObjectTemplate = DataManager.NPC_DATA.getNpcTemplate(template.getNpcId());
		Npc npc = new Npc(IDFactory.getInstance().nextId(), controller, template, masterObjectTemplate);

		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));

		World world = World.getInstance();
		world.storeObject(npc);
		world.setPosition(npc, template.getWorldId(), instance, template.getX(),
				template.getY(), template.getZ(), template.getHeading());
		world.spawn(npc);
		rifts.add(npc);

		return npc;
	}

	public static List<Npc> getSpawned() {
		return rifts;
	}

	public static RiftManager getInstance() {
		return RiftManagerHolder.INSTANCE;
	}

	private static class RiftManagerHolder {

		private static final RiftManager INSTANCE = new RiftManager();
	}

}
