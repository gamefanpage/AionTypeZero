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

package org.typezero.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.typezero.gameserver.model.templates.spawns.Spawn;
import org.typezero.gameserver.model.templates.towns.TownLevel;
import org.typezero.gameserver.model.templates.towns.TownSpawn;
import org.typezero.gameserver.model.templates.towns.TownSpawnMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * @author ViAl
 *
 */
@XmlRootElement(name = "town_spawns_data")
public class TownSpawnsData {
	@XmlElement(name = "spawn_map")
	private List<TownSpawnMap> spawnMap;

	private TIntObjectHashMap<TownSpawnMap> spawnMapsData = new TIntObjectHashMap<TownSpawnMap>();

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		spawnMapsData.clear();

		for (TownSpawnMap map : spawnMap) {
			spawnMapsData.put(map.getMapId(), map);
		}
		spawnMap.clear();
		spawnMap = null;
	}

	/**
	 * @return
	 */
	public int getSpawnsCount() {
		int counter = 0;
		for(TownSpawnMap spawnMap : spawnMapsData.valueCollection())
			for(TownSpawn townSpawn : spawnMap.getTownSpawns())
				for(TownLevel townLevel : townSpawn.getTownLevels())
					counter+= townLevel.getSpawns().size();
		return counter;
	}

	/**
	 * @param townId
	 * @param townLevel
	 * @return
	 */
	public List<Spawn> getSpawns(int townId, int townLevel) {
		for(TownSpawnMap spawnMap : spawnMapsData.valueCollection()) {
			if(spawnMap.getTownSpawn(townId) != null) {
				TownSpawn townSpawn = spawnMap.getTownSpawn(townId);
				return townSpawn.getSpawnsForLevel(townLevel).getSpawns();
			}
		}
		return null;
	}

	public int getWorldIdForTown(int townId) {
		for(TownSpawnMap spawnMap : spawnMapsData.valueCollection())
			if(spawnMap.getTownSpawn(townId) != null)
				return spawnMap.getMapId();
		return 0;
	}

}
