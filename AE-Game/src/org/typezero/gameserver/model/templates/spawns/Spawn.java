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

import org.typezero.gameserver.model.templates.event.EventTemplate;
import org.typezero.gameserver.spawnengine.SpawnHandlerType;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xTz
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spawn")
public class Spawn {

	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;

	@XmlAttribute(name = "respawn_time")
	private Integer respawnTime = 0;

    @XmlAttribute(name = "respawn_random")
    private Integer respawnrandom = 0;

	@XmlAttribute(name = "pool")
	private Integer pool = 0;

	@XmlAttribute(name = "difficult_id")
	private byte difficultId;

	@XmlAttribute(name = "custom")
	private Boolean isCustom = false;

	@XmlAttribute(name = "handler")
	private SpawnHandlerType handler;

	@XmlElement(name = "temporary_spawn")
	private TemporarySpawn temporaySpawn;

	@XmlElement(name = "spot")
	private List<SpawnSpotTemplate> spawnTemplates;

	@XmlTransient
	private EventTemplate eventTemplate;

	public Spawn() {
	}

    public Spawn(int npcId, int respawnTime, SpawnHandlerType handler) {
		this.npcId = npcId;
		this.respawnTime = respawnTime;
		this.handler = handler;
	}

	void beforeMarshal(Marshaller marshaller) {
		if (pool == 0)
			pool = null;
		if (isCustom == false)
			isCustom = null;
	}

	void afterMarshal(Marshaller marshaller) {
		if (isCustom == null)
			isCustom = false;
		if (pool == null)
			pool = 0;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getPool() {
		return pool;
	}

	public TemporarySpawn getTemporarySpawn() {
		return temporaySpawn;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

    public int getRandomspawnTime() {
        return respawnrandom;
    }

	public SpawnHandlerType getSpawnHandlerType() {
		return handler;
	}

	public List<SpawnSpotTemplate> getSpawnSpotTemplates() {
		if (spawnTemplates == null)
			spawnTemplates = new ArrayList<SpawnSpotTemplate>();
		return spawnTemplates;
	}

	public void addSpawnSpot(SpawnSpotTemplate template) {
		getSpawnSpotTemplates().add(template);
	}

	public boolean isCustom() {
		return isCustom == null ? false : isCustom;
	}

	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}

	public boolean isEventSpawn() {
		return eventTemplate != null;
	}

	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}

	public byte getDifficultId() {
		return difficultId;
	}
}
