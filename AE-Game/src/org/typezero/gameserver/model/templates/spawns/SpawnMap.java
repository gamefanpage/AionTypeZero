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

import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawn;
import org.typezero.gameserver.model.templates.spawns.beritraspawns.BeritraSpawn;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SpawnMap")
public class SpawnMap {

	@XmlElement(name = "spawn")
	private List<Spawn> spawns;
	@XmlElement(name = "rift_spawn")
	private List<RiftSpawn> riftSpawns;
	@XmlElement(name = "base_spawn")
	private List<BaseSpawn> baseSpawns;
	@XmlElement(name = "siege_spawn")
	private List<SiegeSpawn> siegeSpawns;
	@XmlElement(name = "vortex_spawn")
	private List<VortexSpawn> vortexSpawns;
    @XmlElement(name = "beritra_spawn")
	private List<BeritraSpawn> beritraSpawns;
	@XmlAttribute(name = "map_id")
	private int mapId;

	public SpawnMap() {
	}

	public SpawnMap(int mapId) {
		this.mapId = mapId;
	}

	public int getMapId() {
		return mapId;
	}

	public List<Spawn> getSpawns() {
		if (spawns == null) {
			spawns = new ArrayList<Spawn>();
		}
		return spawns;
	}

	public void addSpawns(Spawn spawns) {
		getSpawns().add(spawns);
	}

	public void removeSpawns(Spawn spawns) {
		getSpawns().remove(spawns);
	}

	public List<RiftSpawn> getRiftSpawns() {
		if (riftSpawns == null) {
			riftSpawns = new ArrayList<RiftSpawn>();
		}
		return riftSpawns;
	}

	public void addRiftSpawns(RiftSpawn spawns) {
		getRiftSpawns().add(spawns);
	}

	public List<BaseSpawn> getBaseSpawns() {
		if (baseSpawns == null) {
			baseSpawns = new ArrayList<BaseSpawn>();
		}
		return baseSpawns;
	}

	public void addBaseSpawn(BaseSpawn spawns) {
		getBaseSpawns().add(spawns);
	}

	public List<SiegeSpawn> getSiegeSpawns() {
		if (siegeSpawns == null) {
			siegeSpawns = new ArrayList<SiegeSpawn>();
		}
		return siegeSpawns;
	}

	public void addSiegeSpawns(SiegeSpawn spawns) {
		getSiegeSpawns().add(spawns);
	}

	public List<VortexSpawn> getVortexSpawns() {
		if (vortexSpawns == null) {
			vortexSpawns = new ArrayList<VortexSpawn>();
		}
		return vortexSpawns;
	}

	public void addVortexSpawns(VortexSpawn spawns) {
		getVortexSpawns().add(spawns);
	}

    public List<BeritraSpawn> getBeritraSpawns() {
		if (beritraSpawns == null) {
			beritraSpawns = new ArrayList<BeritraSpawn>();
		}
		return beritraSpawns;
	}

	public void addBeritraSpawns(BeritraSpawn spawns) {
		getBeritraSpawns().add(spawns);
	}

}
