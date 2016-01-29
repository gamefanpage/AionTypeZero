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

package org.typezero.gameserver.services;

import java.util.ArrayList;
import java.util.List;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.CollisionDieActor;
import org.typezero.gameserver.controllers.observer.ShieldObserver;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.shield.Shield;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeShield;
import org.typezero.gameserver.model.templates.shield.ShieldTemplate;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author xavier
 * @modified Rolandas
 */
public class ShieldService {

	Logger log = LoggerFactory.getLogger(ShieldService.class);

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ShieldService instance = new ShieldService();
	}

	private final FastMap<Integer, Shield> sphereShields = new FastMap<Integer, Shield>();
	private final FastMap<Integer, List<SiegeShield>> registeredShields = new FastMap<Integer, List<SiegeShield>>(0);

	public static final ShieldService getInstance() {
		return SingletonHolder.instance;
	}

	private ShieldService() {
	}

	public void load(int mapId) {
		for (ShieldTemplate template : DataManager.SHIELD_DATA.getShieldTemplates()) {
			if (template.getMap() != mapId)
				continue;
			Shield f = new Shield(template);
			sphereShields.put(f.getId(), f);
		}
	}

	public void spawnAll() {
		for (Shield shield : sphereShields.values()) {
			shield.spawn();
			log.debug("Added " + shield.getName() + " at m=" + shield.getWorldId() + ",x=" + shield.getX() + ",y=" + shield.getY() + ",z="
				+ shield.getZ());
		}
		// TODO: check this list of not bound meshes (would remain inactive)
		for (List<SiegeShield> otherShields : registeredShields.values()) {
			for (SiegeShield shield : otherShields)
				log.debug("Not bound shield " + shield.getGeometry().getName());
		}
	}

	public ActionObserver createShieldObserver(int locationId, Creature observed) {
		if (sphereShields.containsKey(locationId))
			return new ShieldObserver(sphereShields.get(locationId), observed);
		return null;
	}

	public ActionObserver createShieldObserver(SiegeShield geoShield, Creature observed) {
		ActionObserver observer = null;
		if (GeoDataConfig.GEO_SHIELDS_ENABLE) {
			observer = new CollisionDieActor(observed, geoShield.getGeometry());
			((CollisionDieActor) observer).setEnabled(true);
		}
		return observer;
	}

	/**
	 * Registers geo shield for zone lookup
	 *
	 * @param shield
	 *          - shield to be registered
	 */
	public void registerShield(int worldId, SiegeShield shield) {
		List<SiegeShield> mapShields = registeredShields.get(worldId);
		if (mapShields == null) {
			mapShields = new ArrayList<SiegeShield>();
			registeredShields.put(worldId, mapShields);
		}
		mapShields.add(shield);
	}

	/**
	 * Attaches geo shield and removes obsolete sphere shield if such exists. Should be called when geo shields and
	 * SiegeZoneInstance were created.
	 *
	 * @param location
	 *          - siege location id
	 */
	public void attachShield(SiegeLocation location) {
		List<SiegeShield> mapShields = registeredShields.get(location.getTemplate().getWorldId());
		if (mapShields == null)
			return;

		ZoneInstance zone = location.getZone().get(0);
		List<SiegeShield> shields = new ArrayList<SiegeShield>();

		for (int index = mapShields.size() - 1; index >= 0; index--) {
			SiegeShield shield = mapShields.get(index);
			Vector3f center = shield.getGeometry().getWorldBound().getCenter();
			if (zone.getAreaTemplate().isInside3D(center.x, center.y, center.z)) {
				shields.add(shield);
				mapShields.remove(index);
				Shield sphereShield = sphereShields.get(location.getLocationId());
				if (sphereShield != null) {
					sphereShields.remove(location.getLocationId());
				}
				shield.setSiegeLocationId(location.getLocationId());
			}
		}
		if (shields.size() == 0) {
			log.warn("Could not find a shield for locId: " + location.getLocationId());
		}
		else {
			location.setShields(shields);
		}
	}

}
