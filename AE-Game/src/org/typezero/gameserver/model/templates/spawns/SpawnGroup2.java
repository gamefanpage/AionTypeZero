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

package org.typezero.gameserver.model.templates.spawns;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.beritra.BeritraStateType;
import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.beritraspawns.BeritraSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import org.typezero.gameserver.model.vortex.VortexStateType;
import org.typezero.gameserver.spawnengine.SpawnHandlerType;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xTz
 * @modified Rolandas
 */
public class SpawnGroup2 {

	private static final Logger log = LoggerFactory.getLogger(SpawnGroup2.class);

	private int worldId;
	private int npcId;
	private int pool;
	private byte difficultId;
	private TemporarySpawn temporarySpawn;
	private int respawnTime;
	private int respawnrandom;
	private SpawnHandlerType handlerType;
	private List<SpawnTemplate> spots = new ArrayList<SpawnTemplate>();

	public SpawnGroup2(int worldId, Spawn spawn) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SpawnTemplate spawnTemplate = new SpawnTemplate(this, template);
			if (spawn.isEventSpawn())
				spawnTemplate.setEventTemplate(spawn.getEventTemplate());
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int id) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			RiftSpawnTemplate spawnTemplate = new RiftSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int id, Race race) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			BaseSpawnTemplate spawnTemplate = new BaseSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setBaseRace(race);
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int id, VortexStateType type) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			VortexSpawnTemplate spawnTemplate = new VortexSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setStateType(type);
			spots.add(spawnTemplate);
		}
	}

    public SpawnGroup2(int worldId, Spawn spawn, int id, BeritraStateType type) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			BeritraSpawnTemplate spawnTemplate = new BeritraSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setBStateType(type);
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int siegeId, SiegeRace race, SiegeModType mod) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(this, template);
			spawnTemplate.setSiegeId(siegeId);
			spawnTemplate.setSiegeRace(race);
			spawnTemplate.setSiegeModType(mod);
			spots.add(spawnTemplate);
		}
	}

	private void initializing(Spawn spawn) {
		temporarySpawn = spawn.getTemporarySpawn();
		respawnTime = spawn.getRespawnTime();
		respawnrandom = spawn.getRandomspawnTime();
		pool = spawn.getPool();
		npcId = spawn.getNpcId();
		handlerType = spawn.getSpawnHandlerType();
		difficultId = spawn.getDifficultId();
	}

	public SpawnGroup2(int worldId, int npcId) {
		this.worldId = worldId;
		this.npcId = npcId;
	}

	public List<SpawnTemplate> getSpawnTemplates() {
		return spots;
	}

	public void addSpawnTemplate(SpawnTemplate spawnTemplate) {
		spots.add(spawnTemplate);
	}

	public int getWorldId() {
		return worldId;
	}

	public int getNpcId() {
		return npcId;
	}

	public TemporarySpawn geTemporarySpawn() {
		return temporarySpawn;
	}

	public int getPool() {
		return pool;
	}

	public boolean hasPool() {
		return pool > 0;
	}

    public int getRespawnTime() {
        return respawnTime;
    }

    public int getRandomSpawnTime() {
        return respawnrandom;
    }

    public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public boolean isTemporarySpawn() {
		return temporarySpawn != null;
	}

	public SpawnHandlerType getHandlerType() {
		return handlerType;
	}

	public synchronized SpawnTemplate getRndTemplate() {
		List<SpawnTemplate> templates = new ArrayList<SpawnTemplate>();
		for (SpawnTemplate template : spots) {
			if (!template.isUsed()) {
				templates.add(template);
			}
		}
		if(templates.size() == 0) {
			log.warn("Pool size more then spots, npcId: " + npcId + ", worldId: " + worldId);
			return null;
		}
		SpawnTemplate spawnTemplate = templates.get(Rnd.get(0, templates.size() - 1));
		spawnTemplate.setUse(true);
		return spawnTemplate;
	}

	public byte getDifficultId() {
		return difficultId;
	}

}
