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

package org.typezero.gameserver.world.geo;

import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.geoEngine.collision.CollisionResults;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
public class GeoService {

	private static final Logger log = LoggerFactory.getLogger(GeoService.class);
	private GeoData geoData;
    private static final FastList<Integer> npcsExclude = new FastList<Integer>();

    public static FastList<Integer> getNpcsExclude() {
        return npcsExclude;
    }

    /**
	 * Initialize geodata based on configuration, load necessary structures
	 */
	public void initializeGeo() {
		switch (getConfiguredGeoType()) {
			case GEO_MESHES:
				geoData = new RealGeoData();
				break;
			case NO_GEO:
				geoData = new DummyGeoData();
				break;
		}
        for (String s : GeoDataConfig.GEO_NPCS_EXCUDE.split(",")) {
            npcsExclude.add(Integer.parseInt(s));
        }


        log.info("Configured Geo type: " + getConfiguredGeoType());
		geoData.loadGeoMaps();
	}

	public void setDoorState(int worldId, int instanceId, String name, boolean isOpened) {
		if (GeoDataConfig.GEO_ENABLE) {
			geoData.getMap(worldId).setDoorState(instanceId, name, isOpened);
		}
	}

	/**
	 * @param object
	 * @return
	 */
	public float getZAfterMoveBehind(int worldId, float x, float y, float z, int instanceId) {
		if (GeoDataConfig.GEO_ENABLE) {
			return getZ(worldId, x, y, z, 0, instanceId);
		}
		return getZ(worldId, x, y, z, 0.5f, instanceId);
	}

	/**
	 * @param object
	 * @return
	 */
	public float getZ(VisibleObject object) {
		return geoData.getMap(object.getWorldId()).getZ(object.getX(), object.getY(), object.getZ(), object.getInstanceId());
	}

	/**
	 * @param worldId
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultUp
	 * @return
	 */
	public float getZ(int worldId, float x, float y, float z, float defaultUp, int instanceId) {
		float newZ = geoData.getMap(worldId).getZ(x, y, z, instanceId);
		if (!GeoDataConfig.GEO_ENABLE) {
			newZ += defaultUp;
		}/*
		else {
			newZ += 0.5f;
		}*/
		return newZ;
	}

	/**
	 * @param worldId
	 * @param x
	 * @param y
	 * @return
	 */
	public float getZ(int worldId, float x, float y) {
		return geoData.getMap(worldId).getZ(x, y);
	}

	public String getDoorName(int worldId, String meshFile, float x, float y, float z) {
		return geoData.getMap(worldId).getDoorName(worldId, meshFile, x, y, z);
	}

	public CollisionResults getCollisions(VisibleObject object, float x, float y, float z, boolean changeDirection, byte intentions) {
		return geoData.getMap(object.getWorldId()).getCollisions(object.getX(), object.getY(), object.getZ(), x, y, z, changeDirection, false, object.getInstanceId(), intentions);
	}

	/**
	 * @param object
	 * @param target
	 * @return
	 */
	public boolean canSee(VisibleObject object, VisibleObject target) {
		if (!GeoDataConfig.CANSEE_ENABLE) {
			return true;
		}
		float limit = (float) (MathUtil.getDistance(object, target) - target.getObjectTemplate().getBoundRadius().getCollision());
		if (limit <= 0)
			return true;
		return geoData.getMap(object.getWorldId()).canSee(object.getX(), object.getY(),
			object.getZ() + object.getObjectTemplate().getBoundRadius().getUpper() / 2, target.getX(), target.getY(),
			target.getZ() + target.getObjectTemplate().getBoundRadius().getUpper() / 2, limit, object.getInstanceId());
	}

	public boolean canSee(int worldId, float x, float y, float z, float x1, float y1, float z1, float limit, int instanceId) {
		return geoData.getMap(worldId).canSee(x, y, z, x1, y1, z1, limit, instanceId);
	}

	public boolean isGeoOn() {
		return GeoDataConfig.GEO_ENABLE;
	}

	public Vector3f getClosestCollision(Creature object, float x, float y, float z, boolean changeDirection, byte intentions) {
		return geoData.getMap(object.getWorldId()).getClosestCollision(object.getX(), object.getY(), object.getZ(), x, y, z, changeDirection,
			object.isInFlyingState(), object.getInstanceId(), intentions);
	}

	public GeoType getConfiguredGeoType() {
		if (GeoDataConfig.GEO_ENABLE) {
			return GeoType.GEO_MESHES;
		}
		return GeoType.NO_GEO;
	}

	public static final GeoService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static final class SingletonHolder {

		protected static final GeoService instance = new GeoService();
	}
}
