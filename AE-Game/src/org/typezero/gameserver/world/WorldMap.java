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

package org.typezero.gameserver.world;

import javolution.util.FastMap;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.world.zone.ZoneAttributes;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This object is representing one in-game map and can have instances.
 *
 * @author -Nemesiss-
 */
public class WorldMap {

	private WorldMapTemplate worldMapTemplate;

	private AtomicInteger nextInstanceId = new AtomicInteger(0);
	/**
	 * List of instances.
	 */
	private Map<Integer, WorldMapInstance> instances = new FastMap<Integer, WorldMapInstance>().shared();

	/** World to which belongs this WorldMap */
	private World world;
	private int worldOptions;

	public WorldMap(WorldMapTemplate worldMapTemplate, World world) {
		this.world = world;
		this.worldMapTemplate = worldMapTemplate;
		this.worldOptions = worldMapTemplate.getFlags();

		if (worldMapTemplate.getTwinCount() != 0) {
			for (int i = 1; i <= worldMapTemplate.getTwinCount(); i++) {
				int nextId = getNextInstanceId();
				addInstance(nextId, WorldMapInstanceFactory.createWorldMapInstance(this, nextId));
			}
		}
		else {
			int nextId = getNextInstanceId();
			addInstance(nextId, WorldMapInstanceFactory.createWorldMapInstance(this, nextId));
		}
	}

	public String getName() {
		return worldMapTemplate.getName();
	}

	public int getWaterLevel() {
		return worldMapTemplate.getWaterLevel();
	}

	public int getDeathLevel() {
		return worldMapTemplate.getDeathLevel();
	}

	public WorldType getWorldType() {
		return worldMapTemplate.getWorldType();
	}

	public int getWorldSize() {
		return worldMapTemplate.getWorldSize();
	}

	public Integer getMapId() {
		return worldMapTemplate.getMapId();
	}

	public boolean isPossibleFly(){
		return (worldOptions & ZoneAttributes.FLY.getId()) != 0;
	}

	public boolean isExceptBuff(){
		return worldMapTemplate.isExceptBuff();
	}

	public boolean canGlide() {
		return (worldOptions & ZoneAttributes.GLIDE.getId()) != 0;
	}

	public boolean canPutKisk() {
		return (worldOptions & ZoneAttributes.BIND.getId()) != 0;
	}

	public boolean canRecall() {
		return (worldOptions & ZoneAttributes.RECALL.getId()) != 0;
	}

	public boolean canRide() {
		return (worldOptions & ZoneAttributes.RIDE.getId()) != 0;
	}

	public boolean canFlyRide() {
		return (worldOptions & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}

	public boolean isPvpAllowed() {
		return (worldOptions & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}

	public boolean isSameRaceDuelsAllowed() {
		return (worldOptions & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}

	public boolean isOtherRaceDuelsAllowed() {
		return (worldOptions & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}

	public void setWorldOption(ZoneAttributes option) {
		worldOptions |= option.getId();
	}

	public void removeWorldOption(ZoneAttributes option) {
		worldOptions &= ~option.getId();
	}

	public boolean hasOverridenOption(ZoneAttributes option) {
		if ((worldMapTemplate.getFlags() & option.getId()) == 0)
			return (worldOptions & option.getId()) != 0;
		return (worldOptions & option.getId()) == 0;
	}

	public int getInstanceCount() {
		int twinCount = worldMapTemplate.getTwinCount();
		return twinCount > 0 ? twinCount : 1;
	}

	/**
	 * Return a WorldMapInstance - depends on map configuration one map may have twins instances to balance player. This
	 * method will return WorldMapInstance by server chose.
	 *
	 * @return WorldMapInstance.
	 */
	public WorldMapInstance getWorldMapInstance() {
		// TODO Balance players into instances.
		return getWorldMapInstance(1);
	}

	/**
	 * This method return WorldMapInstance by specified instanceId
	 *
	 * @param instanceId
	 * @return WorldMapInstance
	 */
	public WorldMapInstance getWorldMapInstanceById(int instanceId) {
		if (worldMapTemplate.getTwinCount() != 0) {
			if (instanceId > worldMapTemplate.getTwinCount()) {
				throw new IllegalArgumentException("WorldMapInstance " + getMapId()
					+ " has lower instances count than " + instanceId);
			}
		}
		return getWorldMapInstance(instanceId);
	}

	/**
	 * Returns WorldMapInstance by instanceId.
	 *
	 * @param instanceId
	 * @return WorldMapInstance/
	 */
	private WorldMapInstance getWorldMapInstance(int instanceId) {
		return instances.get(instanceId);
	}

	/**
	 * Remove WorldMapInstance by instanceId.
	 *
	 * @param instanceId
	 */
	public void removeWorldMapInstance(int instanceId) {
		instances.remove(instanceId);
	}

	/**
	 * Add instance to map
	 *
	 * @param instanceId
	 * @param instance
	 */
	public void addInstance(int instanceId, WorldMapInstance instance) {
		instances.put(instanceId, instance);
	}

	/**
	 * Returns the World containing this WorldMap.
	 */
	public World getWorld() {
		return world;
	}

	public final WorldMapTemplate getTemplate() {
		return worldMapTemplate;
	}

	/**
	 * @return the nextInstanceId
	 */
	public int getNextInstanceId() {
		return nextInstanceId.incrementAndGet();
	}

	/**
	 * Whether this world map is instance type
	 *
	 * @return
	 */
	public boolean isInstanceType() {
		return worldMapTemplate.isInstance();
	}

	/**
	 * @return
	 */
	public Iterator<WorldMapInstance> iterator() {
		return instances.values().iterator();
	}

	/**
	 * All instance ids of this map
	 */
	public Collection<Integer> getAvailableInstanceIds() {
		return instances.keySet();
	}
}
