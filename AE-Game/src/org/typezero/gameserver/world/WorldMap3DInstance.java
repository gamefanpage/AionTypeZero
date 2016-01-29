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

import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public class WorldMap3DInstance extends WorldMapInstance {

	/**
	 * @param parent
	 * @param instanceId
	 */
	public WorldMap3DInstance(WorldMap parent, int instanceId) {
		super(parent, instanceId);
	}

	@Override
	public MapRegion getRegion(float x, float y, float z) {
		int regionId = RegionUtil.get3dRegionId(x, y, z);
		return regions.get(regionId);
	}

	protected void initMapRegions() {
		int size = this.getParent().getWorldSize();
		float maxZ = Math.round((float) size / regionSize) * regionSize;

		// Create all mapRegion
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z < maxZ; z = z + regionSize) {
					int regionId = RegionUtil.get3dRegionId(x, y, z);
					regions.put(regionId, createMapRegion(regionId));
				}
			}
		}

		// Add Neighbour
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z < maxZ; z = z + regionSize) {
					int regionId = RegionUtil.get3dRegionId(x, y, z);
					MapRegion mapRegion = regions.get(regionId);
					for (int x2 = x - regionSize; x2 <= x + regionSize; x2 += regionSize) {
						for (int y2 = y - regionSize; y2 <= y + regionSize; y2 += regionSize) {
							for (int z2 = z - regionSize; z2 < z + regionSize; z2 += regionSize) {
								if (x2 == x && y2 == y && z2 == z)
									continue;
								int neighbourId = RegionUtil.get3dRegionId(x2, y2, z2);
								MapRegion neighbour = regions.get(neighbourId);
								if (neighbour != null)
									mapRegion.addNeighbourRegion(neighbour);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected MapRegion createMapRegion(int regionId) {
		float startX = RegionUtil.getXFrom3dRegionId(regionId);
		float startY = RegionUtil.getYFrom3dRegionId(regionId);
		float startZ = RegionUtil.getZFrom3dRegionId(regionId);
		ZoneInstance[] zones = filterZones(this.getMapId(), regionId, startX, startY, startZ, startZ + regionSize);
		return new MapRegion(regionId, this, zones);
	}

	@Override
	public boolean isPersonal() {
		return false;
	}

	@Override
	public int getOwnerId() {
		return 0;
	}
}
