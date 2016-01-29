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
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.spawns.*;
import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawn;
import org.typezero.gameserver.model.templates.spawns.beritraspawns.BeritraSpawn;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawn;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.spawnengine.SpawnHandlerType;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 * @author xTz
 * @modified Rolandas
 */
@XmlRootElement(name = "spawns")
@XmlType(namespace = "", name = "SpawnsData2")
@XmlAccessorType(XmlAccessType.NONE)
public class SpawnsData2 {

	private static final Logger log = LoggerFactory.getLogger(SpawnsData2.class);
	@XmlElement(name = "spawn_map", type = SpawnMap.class)
	protected List<SpawnMap> templates;
	private TIntObjectHashMap<FastMap<Integer, SimpleEntry<SpawnGroup2, Spawn>>> allSpawnMaps = new TIntObjectHashMap<FastMap<Integer, SimpleEntry<SpawnGroup2, Spawn>>>();
	private TIntObjectHashMap<List<SpawnGroup2>> riftSpawnMaps = new TIntObjectHashMap<List<SpawnGroup2>>();
	private TIntObjectHashMap<List<SpawnGroup2>> siegeSpawnMaps = new TIntObjectHashMap<List<SpawnGroup2>>();
	private TIntObjectHashMap<List<SpawnGroup2>> vortexSpawnMaps = new TIntObjectHashMap<List<SpawnGroup2>>();
	private TIntObjectHashMap<List<SpawnGroup2>> baseSpawnMaps = new TIntObjectHashMap<List<SpawnGroup2>>();
	private TIntObjectHashMap<List<SpawnGroup2>> beritraSpawnMaps = new TIntObjectHashMap<List<SpawnGroup2>>();
	private TIntObjectHashMap<Spawn> customs = new TIntObjectHashMap<Spawn>();

	/**
	 * @param u
	 * @param parent
	 * @throws PropertyException
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (templates != null) {
			for (SpawnMap spawnMap : templates) {
				int mapId = spawnMap.getMapId();
				if (!allSpawnMaps.containsKey(mapId)) {
					allSpawnMaps.put(mapId, new FastMap<Integer, SimpleEntry<SpawnGroup2, Spawn>>());
				}
				for (Spawn spawn : spawnMap.getSpawns()) {
					if (spawn.isCustom()) {
						if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
							allSpawnMaps.get(mapId).remove(spawn.getNpcId());
						customs.put(spawn.getNpcId(), spawn);
					}
					else if (customs.containsKey(spawn.getNpcId()))
						continue;
					allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(new SpawnGroup2(mapId, spawn), spawn));
				}
				if (!allSpawnMaps.containsKey(mapId)) {
					allSpawnMaps.put(mapId, new FastMap<Integer, SimpleEntry<SpawnGroup2, Spawn>>());
				}
				for (BaseSpawn BaseSpawn : spawnMap.getBaseSpawns()) {
					int baseId = BaseSpawn.getId();
					if (!baseSpawnMaps.containsKey(baseId)) {
						baseSpawnMaps.put(baseId, new ArrayList<SpawnGroup2>());
					}
					for (BaseSpawn.SimpleRaceTemplate simpleRace : BaseSpawn.getBaseRaceTemplates()) {
						for (Spawn spawn : simpleRace.getSpawns()) {
							if (spawn.isCustom()) {
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
								continue;
							SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, baseId, simpleRace.getBaseRace());
							allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
							baseSpawnMaps.get(baseId).add(spawnGroup);
						}
					}
				}
				for (RiftSpawn rift : spawnMap.getRiftSpawns()) {
					int id = rift.getId();
					if (!riftSpawnMaps.containsKey(id)) {
						riftSpawnMaps.put(id, new ArrayList<SpawnGroup2>());
					}
					for (Spawn spawn : rift.getSpawns()) {
						if (spawn.isCustom()) {
							if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId())) {
								allSpawnMaps.get(mapId).remove(spawn.getNpcId());
							}
							customs.put(spawn.getNpcId(), spawn);
						}
						else if (customs.containsKey(spawn.getNpcId()))
							continue;
						SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id);
						allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
						riftSpawnMaps.get(id).add(spawnGroup);
					}
				}
				for (SiegeSpawn SiegeSpawn : spawnMap.getSiegeSpawns()) {
					int siegeId = SiegeSpawn.getSiegeId();
					if (!siegeSpawnMaps.containsKey(siegeId)) {
						siegeSpawnMaps.put(siegeId, new ArrayList<SpawnGroup2>());
					}
					for (SiegeSpawn.SiegeRaceTemplate race : SiegeSpawn.getSiegeRaceTemplates()) {
						for (SiegeSpawn.SiegeRaceTemplate.SiegeModTemplate mod : race.getSiegeModTemplates()) {
							if (mod == null || mod.getSpawns() == null) {
								continue;
							}
							for (Spawn spawn : mod.getSpawns()) {
								if (spawn.isCustom()) {
									if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
										allSpawnMaps.get(mapId).remove(spawn.getNpcId());
									customs.put(spawn.getNpcId(), spawn);
								}
								else if (customs.containsKey(spawn.getNpcId()))
									continue;
								SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, siegeId, race.getSiegeRace(),
										mod.getSiegeModType());
								allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
								siegeSpawnMaps.get(siegeId).add(spawnGroup);
							}
						}
					}
				}
				for (VortexSpawn VortexSpawn : spawnMap.getVortexSpawns()) {
					int id = VortexSpawn.getId();
					if (!vortexSpawnMaps.containsKey(id)) {
						vortexSpawnMaps.put(id, new ArrayList<SpawnGroup2>());
					}
					for (VortexSpawn.VortexStateTemplate type : VortexSpawn.getSiegeModTemplates()) {
						if (type == null || type.getSpawns() == null) {
							continue;
						}
						for (Spawn spawn : type.getSpawns()) {
							if (spawn.isCustom()) {
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId())) {
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								}
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
								continue;
							SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id, type.getStateType());
							vortexSpawnMaps.get(id).add(spawnGroup);
						}
					}
				}
                for (BeritraSpawn BeritraSpawn : spawnMap.getBeritraSpawns()) {
					int id = BeritraSpawn.getId();
					if (!beritraSpawnMaps.containsKey(id)) {
						beritraSpawnMaps.put(id, new ArrayList<SpawnGroup2>());
					}
					for (BeritraSpawn.BeritraStateTemplate type : BeritraSpawn.getSiegeModTemplates()) {
						if (type == null || type.getSpawns() == null) {
							continue;
						}
						for (Spawn spawn : type.getSpawns()) {
							if (spawn.isCustom()) {
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId())) {
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								}
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
								continue;
							SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id, type.getBeritraType());
							beritraSpawnMaps.get(id).add(spawnGroup);
						}
					}
				}
                customs.clear();
            }
        }
    }

	public void clearTemplates() {
		if (templates != null) {
			templates.clear();
			templates = null;
		}
	}

	public List<SpawnGroup2> getSpawnsByWorldId(int worldId) {
		if (!allSpawnMaps.containsKey(worldId))
			return Collections.emptyList();
		return flatten(extractIterator(allSpawnMaps.get(worldId).values(), on(SimpleEntry.class).getKey()));
	}

	public Spawn getSpawnsForNpc(int worldId, int npcId) {
		if (!allSpawnMaps.containsKey(worldId) || !allSpawnMaps.get(worldId).containsKey(npcId))
			return null;
		return allSpawnMaps.get(worldId).get(npcId).getValue();
	}

	public List<SpawnGroup2> getBaseSpawnsByLocId(int id) {
		return baseSpawnMaps.get(id);
	}

	public List<SpawnGroup2> getRiftSpawnsByLocId(int id) {
		return riftSpawnMaps.get(id);
	}

	public List<SpawnGroup2> getSiegeSpawnsByLocId(int siegeId) {
		return siegeSpawnMaps.get(siegeId);
	}

	public List<SpawnGroup2> getVortexSpawnsByLocId(int id) {
		return vortexSpawnMaps.get(id);
	}

    public List<SpawnGroup2> getBeritraSpawnsByLocId(int id) {
		return beritraSpawnMaps.get(id);
	}

	public synchronized boolean saveSpawn(Player admin, VisibleObject visibleObject, boolean delete) throws IOException {
		SpawnTemplate spawn = visibleObject.getSpawn();
		Spawn oldGroup = DataManager.SPAWNS_DATA2.getSpawnsForNpc(visibleObject.getWorldId(), spawn.getNpcId());

		File xml = new File("./data/static_data/spawns/" + getRelativePath(visibleObject));
		SpawnsData2 data = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		JAXBContext jc = null;
		boolean addGroup = false;

		try {
			schema = sf.newSchema(new File("./data/static_data/spawns/spawns.xsd"));
			jc = JAXBContext.newInstance(SpawnsData2.class);
		}
		catch (Exception e) {
			// ignore, if schemas are wrong then we even could not call the command;
		}

		FileInputStream fin = null;
		if (xml.exists()) {
			try {
				fin = new FileInputStream(xml);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				unmarshaller.setSchema(schema);
				data = (SpawnsData2) unmarshaller.unmarshal(fin);
			}
			catch (Exception e) {
				log.error(e.getMessage());
				PacketSendUtility.sendMessage(admin, "Could not load old XML file!");
				return false;
			}
			finally {
				if (fin != null)
					fin.close();
			}
		}

		if (oldGroup == null || oldGroup.isCustom()) {
			if (data == null)
				data = new SpawnsData2();

			oldGroup = data.getSpawnsForNpc(visibleObject.getWorldId(), spawn.getNpcId());
			if (oldGroup == null) {
				oldGroup = new Spawn(spawn.getNpcId(), spawn.getRespawnTime(), spawn.getHandlerType());
				addGroup = true;
			}
		}
		else {
			if (data == null)
				data = DataManager.SPAWNS_DATA2;
			// only remove from memory, will be added back later
			allSpawnMaps.get(visibleObject.getWorldId()).remove(spawn.getNpcId());
			addGroup = true;
		}

		SpawnSpotTemplate spot = new SpawnSpotTemplate(visibleObject.getX(), visibleObject.getY(), visibleObject.getZ(),
				visibleObject.getHeading(), visibleObject.getSpawn().getRandomWalk(), visibleObject.getSpawn().getWalkerId(),
				visibleObject.getSpawn().getWalkerIndex());
		boolean changeX = visibleObject.getX() != spawn.getX();
		boolean changeY = visibleObject.getY() != spawn.getY();
		boolean changeZ = visibleObject.getZ() != spawn.getZ();
		boolean changeH = visibleObject.getHeading() != spawn.getHeading();
		if (changeH && visibleObject instanceof Npc) {
			Npc npc = (Npc) visibleObject;
			if (!npc.isAtSpawnLocation() || !npc.isInState(CreatureState.NPC_IDLE) || changeX || changeY || changeZ) {
				// if H changed, XSD validation fails, because it may be negative; thus, reset it back
				visibleObject.setXYZH(null, null, null, spawn.getHeading());
				changeH = false;
			}
		}

		SpawnSpotTemplate oldSpot = null;
		for (SpawnSpotTemplate s : oldGroup.getSpawnSpotTemplates()) {
			if (s.getX() == spot.getX() && s.getY() == spot.getY() && s.getZ() == spot.getZ()
					&& s.getHeading() == spot.getHeading()) {
				if (delete || !StringUtils.equals(s.getWalkerId(), spot.getWalkerId())) {
					oldSpot = s;
					break;
				}
				else
					return false; // nothing to change
			}
			else if (changeX && s.getY() == spot.getY() && s.getZ() == spot.getZ() && s.getHeading() == spot.getHeading()
					|| changeY && s.getX() == spot.getX() && s.getZ() == spot.getZ() && s.getHeading() == spot.getHeading()
					|| changeZ && s.getX() == spot.getX() && s.getY() == spot.getY() && s.getHeading() == spot.getHeading()
					|| changeH && s.getX() == spot.getX() && s.getY() == spot.getY() && s.getZ() == spot.getZ()) {
				oldSpot = s;
				break;
			}
		}

		if (oldSpot != null)
			oldGroup.getSpawnSpotTemplates().remove(oldSpot);
		if (!delete)
			oldGroup.addSpawnSpot(spot);
		oldGroup.setCustom(true);

		SpawnMap map = null;
		if (data.templates == null) {
			data.templates = new ArrayList<SpawnMap>();
			map = new SpawnMap(spawn.getWorldId());
			data.templates.add(map);
		}
		else {
			map = data.templates.get(0);
		}

		if (addGroup)
			map.addSpawns(oldGroup);

		FileOutputStream fos = null;
		try {
			xml.getParentFile().mkdir();
			fos = new FileOutputStream(xml);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(data, fos);
			DataManager.SPAWNS_DATA2.templates = data.templates;
			DataManager.SPAWNS_DATA2.afterUnmarshal(null, null);
			DataManager.SPAWNS_DATA2.clearTemplates();
			data.clearTemplates();
		}
		catch (Exception e) {
			log.error(e.getMessage());
			PacketSendUtility.sendMessage(admin, "Could not save XML file!");
			return false;
		}
		finally {
			if (fos != null)
				fos.close();
		}
		return true;
	}

	String getRelativePath(VisibleObject visibleObject) {
		String path;
		WorldMap map = World.getInstance().getWorldMap(visibleObject.getWorldId());
		if (visibleObject.getSpawn().getHandlerType() == SpawnHandlerType.RIFT)
			path = "Rifts";
		else if (visibleObject instanceof Gatherable)
			path = "Gather";
		else if (map.isInstanceType())
			path = "Instances";
		else
			path = "Npcs";
		return path + "/New/" + visibleObject.getWorldId() + "_" + map.getName().replace(' ', '_') + ".xml";
	}

	public int size() {
		return allSpawnMaps.size();
	}

	/**
	 * @param worldId Optional. If provided, searches in this world first
	 * @param npcId
	 * @return template for the spot
	 */
	public SpawnSearchResult getFirstSpawnByNpcId(int worldId, int npcId) {
		Spawn spawns = DataManager.SPAWNS_DATA2.getSpawnsForNpc(worldId, npcId);

		if (spawns == null) {
			for (WorldMapTemplate template : DataManager.WORLD_MAPS_DATA) {
				if (template.getMapId() == worldId)
					continue;
				spawns = DataManager.SPAWNS_DATA2.getSpawnsForNpc(template.getMapId(), npcId);
				if (spawns != null) {
					worldId = template.getMapId();
					break;
				}
			}
			if (spawns == null)
				return null;
		}
        if (spawns.getSpawnSpotTemplates().size() == 0)
        {
            return null;
        }
		return new SpawnSearchResult(worldId, spawns.getSpawnSpotTemplates().get(0));
	}

	/**
	 * Used by Event Service to add additional spawns
	 *
	 * @param spawnMap templates to add
	 */
	public void addNewSpawnMap(SpawnMap spawnMap) {
		if (templates == null)
			templates = new ArrayList<SpawnMap>();
		templates.add(spawnMap);
	}

	public void removeEventSpawnObjects(List<VisibleObject> objects) {
		for (VisibleObject visObj : objects) {
			if (!allSpawnMaps.contains(visObj.getWorldId()))
				continue;
			SimpleEntry<SpawnGroup2, Spawn> entry = allSpawnMaps.get(visObj.getWorldId()).get(
					visObj.getObjectTemplate().getTemplateId());
			if (!entry.getValue().isEventSpawn())
				continue;
			if (entry.getValue().getEventTemplate().equals(visObj.getSpawn().getEventTemplate()))
				allSpawnMaps.get(visObj.getWorldId()).remove(entry);
		}
	}

	public List<SpawnMap> getTemplates() {
		return templates;
	}

}
