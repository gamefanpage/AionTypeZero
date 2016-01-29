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

package org.typezero.gameserver.world.zone;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.GameServerError;
import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.ZoneData;
import org.typezero.gameserver.geoEngine.scene.Spatial;
import org.typezero.gameserver.model.GameEngine;
import org.typezero.gameserver.model.geometry.*;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeShield;
import org.typezero.gameserver.model.templates.materials.MaterialTemplate;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.model.templates.zone.MaterialZoneTemplate;
import org.typezero.gameserver.model.templates.zone.WorldZoneTemplate;
import org.typezero.gameserver.model.templates.zone.ZoneInfo;
import org.typezero.gameserver.model.templates.zone.ZoneTemplate;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.services.ShieldService;
import org.typezero.gameserver.world.zone.handler.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author ATracer modified by antness
 */
public final class ZoneService implements GameEngine {

	private static final Logger log = LoggerFactory.getLogger(ZoneService.class);
	private TIntObjectHashMap<List<ZoneInfo>> zoneByMapIdMap;
	private final Map<ZoneName, Class<? extends ZoneHandler>> handlers = new HashMap<ZoneName, Class<? extends ZoneHandler>>();
	private final FastMap<ZoneName, ZoneHandler> collidableHandlers = new FastMap<ZoneName, ZoneHandler>();
	public static final ZoneHandler DUMMY_ZONE_HANDLER = new GeneralZoneHandler();
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File ZONE_DESCRIPTOR_FILE = new File("./data/scripts/system/zonehandlers.xml");

	private ZoneService() {
		this.zoneByMapIdMap = DataManager.ZONE_DATA.getZones();
	}

	public static ZoneService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ZoneService instance = new ZoneService();
	}

	public ZoneHandler getNewZoneHandler(ZoneName zoneName) {
		ZoneHandler zoneHandler = collidableHandlers.get(zoneName);
		if (zoneHandler != null)
			return zoneHandler;
		Class<? extends ZoneHandler> zoneClass = handlers.get(zoneName);
		if (zoneClass != null) {
			try {
				zoneHandler = zoneClass.newInstance();
			}
			catch (IllegalAccessException ex) {
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
			catch (Exception ex) {
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
		}
		if (zoneHandler == null) {
			zoneHandler = DUMMY_ZONE_HANDLER;
		}
		return zoneHandler;
	}

	/**
	 * @param handler
	 */
	public final void addZoneHandlerClass(Class<? extends ZoneHandler> handler) {
		ZoneNameAnnotation idAnnotation = handler.getAnnotation(ZoneNameAnnotation.class);
		if (idAnnotation != null) {
			String[] zoneNames = idAnnotation.value().split(" ");
			for (String zoneNameString : zoneNames) {
				try {
					ZoneName zoneName = ZoneName.get(zoneNameString.trim());
					if (zoneName == ZoneName.get("NONE"))
						throw new RuntimeException();
					handlers.put(zoneName, handler);
				}
				catch (Exception e) {
					log.warn("Missing ZoneName: " + idAnnotation.value());
				}
			}
		}
	}

	public final void addZoneHandlerClass(ZoneName zoneName, Class<? extends ZoneHandler> handler) {
		handlers.put(zoneName, handler);
	}

	@Override
	public void load(CountDownLatch progressLatch) {
		log.info("Zone engine load started");
		scriptManager = new ScriptManager();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ZoneHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);

		try {
			scriptManager.load(ZONE_DESCRIPTOR_FILE);
			log.info("Loaded " + handlers.size() + " zone handlers.");
		}
		catch (IllegalStateException e) {
			log.warn("Can't initialize instance handlers.", e.getMessage());
		}
		catch (Exception e) {
			throw new GameServerError("Can't initialize instance handlers.", e);
		}
		finally {
			if (progressLatch != null) {
				progressLatch.countDown();
			}
		}
	}

	@Override
	public void shutdown() {
		log.info("Zone engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("Zone engine shutdown complete");
	}

	/**
	 * @param mapId
	 * @return
	 */
	public Map<ZoneName, ZoneInstance> getZoneInstancesByWorldId(int mapId) {
		Map<ZoneName, ZoneInstance> zones = new HashMap<ZoneName, ZoneInstance>();
		int worldSize = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getWorldSize();
		WorldZoneTemplate zone = new WorldZoneTemplate(worldSize, mapId);
		PolyArea fullArea = new PolyArea(zone.getName(), mapId, zone.getPoints().getPoint(), zone.getPoints().getBottom(), zone.getPoints()
			.getTop());
		ZoneInstance fullMap = new ZoneInstance(mapId, new ZoneInfo(fullArea, zone));
		fullMap.addHandler(getNewZoneHandler(zone.getName()));
		zones.put(zone.getName(), fullMap);

		Collection<ZoneInfo> areas = this.zoneByMapIdMap.get(mapId);
		if (areas == null)
			return zones;
		ShieldService.getInstance().load(mapId);

		for (ZoneInfo area : areas) {
			ZoneInstance instance = null;
			switch (area.getZoneTemplate().getZoneType()) {
				case FLY:
					instance = new FlyZoneInstance(mapId, area);
					break;
				case FORT:
					instance = new SiegeZoneInstance(mapId, area);
					SiegeLocation siege = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations().get(area.getZoneTemplate().getSiegeId().get(0));
					if (siege != null) {
						siege.addZone((SiegeZoneInstance) instance);
						if (GeoDataConfig.GEO_SHIELDS_ENABLE)
							ShieldService.getInstance().attachShield(siege);
					}
					break;
				case ARTIFACT:
					instance = new SiegeZoneInstance(mapId, area);
					for (int artifactId : area.getZoneTemplate().getSiegeId()) {
						SiegeLocation artifact = DataManager.SIEGE_LOCATION_DATA.getArtifacts().get(artifactId);
						if (artifact == null) {
							log.warn("Missing siege location data for zone " + area.getZoneTemplate().getName().name());
						}
						else {
							artifact.addZone((SiegeZoneInstance) instance);
						}
					}
					break;
				case PVP:
					instance = new PvPZoneInstance(mapId, area);
					break;
				default:
					InvasionZoneInstance invasionZone = getIZI(area);
					if (invasionZone != null) {
						instance = invasionZone;
					}
					else {
						instance = new ZoneInstance(mapId, area);
					}
			}
			instance.addHandler(getNewZoneHandler(area.getZoneTemplate().getName()));
			zones.put(area.getZoneTemplate().getName(), instance);
		}
		return zones;
	}

	private InvasionZoneInstance getIZI(ZoneInfo area) {
		if (area.getZoneTemplate().getName().name().equals("WAILING_CLIFFS_220050000")
				|| area.getZoneTemplate().getName().name().equals("BALTASAR_CEMETERY_220050000")
				|| area.getZoneTemplate().getName().name().equals("THE_LEGEND_SHRINE_220050000")
				|| area.getZoneTemplate().getName().name().equals("SUDORVILLE_220050000")
				|| area.getZoneTemplate().getName().name().equals("BALTASAR_HILL_VILLAGE_220050000")
				|| area.getZoneTemplate().getName().name().equals("BRUSTHONIN_MITHRIL_MINE_220050000")) {
			return validateZone(area);
		}
		else if (area.getZoneTemplate().getName().name().equals("JAMANOK_INN_210060000")
				|| area.getZoneTemplate().getName().name().equals("THE_STALKING_GROUNDS_210060000")
				|| area.getZoneTemplate().getName().name().equals("BLACK_ROCK_HOT_SPRING_210060000")
				|| area.getZoneTemplate().getName().name().equals("FREGIONS_FLAME_210060000")) {
			return validateZone(area);
		}
		return null;
	}

	private InvasionZoneInstance validateZone(ZoneInfo area) {
		int mapId = area.getZoneTemplate().getMapid();
		VortexLocation vortex = DataManager.VORTEX_DATA.getVortexLocation(mapId);
		if (vortex != null) {
			InvasionZoneInstance instance = new InvasionZoneInstance(mapId, area);
			vortex.addZone(instance);
			return instance;
		}
		return null;
	}

	/**
	 * Method for single instances of meshes (if specified in mesh_materials.xml)
	 *
	 * @param geometry
	 * @param worldId
	 * @param materialId
	 */
	public void createMaterialZoneTemplate(Spatial geometry, int worldId, int materialId, boolean failOnMissing) {
		ZoneName zoneName = null;
		if (failOnMissing)
			zoneName = ZoneName.get(geometry.getName() + "_" + worldId);
		else
			zoneName = ZoneName.createOrGet(geometry.getName() + "_" + worldId);

		if (zoneName.name().equals(ZoneName.NONE))
			return;

		ZoneHandler handler = collidableHandlers.get(zoneName);
		if (handler == null) {
			if (materialId == 11) {
				if (GeoDataConfig.GEO_SHIELDS_ENABLE) {
					handler = new SiegeShield(geometry);
					ShieldService.getInstance().registerShield(worldId, (SiegeShield) handler);
				}
				else
					return;
			}
			else {
				MaterialTemplate template = DataManager.MATERIAL_DATA.getTemplate(materialId);
				if (template == null)
					return;
				handler = new MaterialZoneHandler(geometry, template);
			}
			collidableHandlers.put(zoneName, handler);
		}
		else {
			log.warn("Duplicate material mesh: " + zoneName.toString());
		}

		Collection<ZoneInfo> areas = this.zoneByMapIdMap.get(worldId);
		if (areas == null) {
			this.zoneByMapIdMap.put(worldId, new ArrayList<ZoneInfo>());
			areas = this.zoneByMapIdMap.get(worldId);
		}
		ZoneInfo zoneInfo = null;
		for (ZoneInfo area : areas) {
			if (area.getZoneTemplate().getName().equals(zoneName)) {
				zoneInfo = area;
				break;
			}
		}
		if (zoneInfo == null) {
			MaterialZoneTemplate zoneTemplate = new MaterialZoneTemplate(geometry, worldId);
			// maybe add to zone data if needed search ?
			Area zoneInfoArea = null;
			if (zoneTemplate.getSphere() != null) {
				zoneInfoArea = new SphereArea(zoneName, worldId, zoneTemplate.getSphere().getX(), zoneTemplate.getSphere().getY(), zoneTemplate
					.getSphere().getZ(), zoneTemplate.getSphere().getR());
			}
			else if (zoneTemplate.getCylinder() != null) {
				zoneInfoArea = new CylinderArea(zoneName, worldId, zoneTemplate.getCylinder().getX(), zoneTemplate.getCylinder().getY(),
					zoneTemplate.getCylinder().getR(), zoneTemplate.getCylinder().getBottom(), zoneTemplate.getCylinder().getTop());
			}
			else if (zoneTemplate.getSemisphere() != null) {
				zoneInfoArea = new SemisphereArea(zoneName, worldId, zoneTemplate.getSemisphere().getX(), zoneTemplate.getSemisphere().getY(),
					zoneTemplate.getSemisphere().getZ(), zoneTemplate.getSemisphere().getR());
			}
			if (zoneInfoArea != null) {
				zoneInfo = new ZoneInfo(zoneInfoArea, zoneTemplate);
				areas.add(zoneInfo);
			}
		}
	}

	/**
	 * Method for dynamic zone template creation for geometries; could be saved later in XML
	 *
	 * @param geometry
	 * @param regionId
	 *          - generated by RegionUtil from Bounding Volume center coordinates
	 * @param worldId
	 * @param materialId
	 */
	public void createMaterialZoneTemplate(Spatial geometry, int regionId, int worldId, int materialId) {
		geometry.setName(geometry.getName() + "_" + regionId);
		createMaterialZoneTemplate(geometry, worldId, materialId, false);
	}

	public void saveMaterialZones() {
		List<ZoneTemplate> templates = new ArrayList<ZoneTemplate>();
		for (WorldMapTemplate map : DataManager.WORLD_MAPS_DATA) {
			Collection<ZoneInfo> areas = this.zoneByMapIdMap.get(map.getMapId());
			if (areas == null)
				continue;
			for (ZoneInfo zone : areas) {
				if (collidableHandlers.containsKey(zone.getArea().getZoneName())) {
					templates.add(zone.getZoneTemplate());
				}
			}
		}
		Collections.sort(templates, new Comparator<ZoneTemplate>() {

			@Override
			public int compare(ZoneTemplate o1, ZoneTemplate o2) {
				return o1.getMapid() - o2.getMapid();
			}
		});

		ZoneData zoneData = new ZoneData();
		zoneData.zoneList = templates;
		zoneData.saveData();
	}

}
