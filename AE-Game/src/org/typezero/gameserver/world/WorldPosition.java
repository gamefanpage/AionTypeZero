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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Position of object in the world.
 * 
 * @author -Nemesiss-
 */
public class WorldPosition {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(WorldPosition.class);

	/**
	 * Map id.
	 */
	private int mapId;
	/**
	 * Map Region.
	 */
	private MapRegion mapRegion;
	/**
	 * World position x
	 */
	private float x;
	/**
	 * World position y
	 */
	private float y;
	/**
	 * World position z
	 */
	private float z;

	/**
	 * Value from 0 to 120 (120==0 actually)
	 */
	private byte heading;
	/**
	 * indicating if object is spawned or not.
	 */
	private boolean isSpawned = false;

	/**
	 * Return World map id.
	 * 
	 * @return world map id
	 */
	public int getMapId() {
		if (mapId == 0)
			log.warn("WorldPosition has (mapId == 0) " + this.toString());
		return mapId;
	}

	/**
	 * @param mapId
	 *          the mapId to set
	 */
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	/**
	 * Return World position x
	 * 
	 * @return x
	 */
	public float getX() {
		return x;
	}

	/**
	 * Return World position y
	 * 
	 * @return y
	 */
	public float getY() {
		return y;
	}

	/**
	 * Return World position z
	 * 
	 * @return z
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Return map region
	 * 
	 * @return Map region
	 */
	public MapRegion getMapRegion() {
		return isSpawned ? mapRegion : null;
	}

	/**
	 * @return
	 */
	public int getInstanceId() {
		return mapRegion.getParent().getInstanceId();
	}

	/**
	 * @return
	 */
	public int getInstanceCount() {
		return mapRegion.getParent().getParent().getInstanceCount();
	}

	/**
	 * @return
	 */
	public boolean isInstanceMap() {
		return mapRegion.getParent().getParent().isInstanceType();
	}

	/**
	 * @return
	 */
	public boolean isMapRegionActive() {
		return mapRegion.isMapRegionActive();
	}

	/**
	 * Return heading.
	 * 
	 * @return heading
	 */
	public byte getHeading() {
		return heading;
	}

	/**
	 * Returns the {@link World} instance in which this position is located. :D
	 * 
	 * @return World
	 */
	public World getWorld() {
		return mapRegion.getWorld();
	}

	/**
	 * @return worldMapInstance
	 */
	public WorldMapInstance getWorldMapInstance() {
		return mapRegion.getParent();
	}

	/**
	 * Check if object is spawned.
	 * 
	 * @return true if object is spawned.
	 */
	public boolean isSpawned() {
		return isSpawned;
	}

	/**
	 * Set isSpawned to given value.
	 * 
	 * @param val
	 */
	void setIsSpawned(boolean val) {
		isSpawned = val;
	}

	/**
	 * Set map region
	 * 
	 * @param r
	 *          - map region
	 */
	void setMapRegion(MapRegion r) {
		mapRegion = r;
	}

	/**
	 * Set world position.
	 * 
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 *          Value from 0 to 120 (120==0 actually)
	 */
	public void setXYZH(Float newX, Float newY, Float newZ, Byte newHeading) {
		if (newX != null)
			x = newX;
		if (newY != null)
			y = newY;
		if (newZ != null)
			z = newZ;
		if (newHeading != null)
			heading = newHeading;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public void setH(byte h) {
		this.heading = h;
	}

	@Override
	public String toString() {
		return "WorldPosition [heading=" + heading + ", isSpawned=" + isSpawned + ", mapRegion=" + mapRegion + ", x=" + x
			+ ", y=" + y + ", z=" + z + "]";
	}

}
