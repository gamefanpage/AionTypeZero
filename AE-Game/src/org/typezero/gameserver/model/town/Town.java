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

package org.typezero.gameserver.model.town;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.Spawn;
import org.typezero.gameserver.model.templates.spawns.SpawnSpotTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOWNS_LIST;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author ViAl
 */
public class Town {

	private int id;
	private int level;
	private int points;
	private Timestamp levelUpDate;
	private Race race;
	private PersistentState persistentState;
	private List<Npc> spawnedNpcs;

	/**
	 * Used only from DAO.
	 *
	 * @param id
	 * @param level
	 * @param points
	 */
	public Town(int id, int level, int points, Race race, Timestamp levelUpDate) {
		this.id = id;
		this.level = level;
		this.points = points;
		this.levelUpDate = levelUpDate;
		this.race = race;
		this.persistentState = PersistentState.UPDATED;
		this.spawnedNpcs = new ArrayList<Npc>();
		spawnNewObjects();
	}

	/**
	 * Used for initial import from house templates.
	 *
	 * @param id
	 */
	public Town(int id, Race race) {
		this(id, 1, 0, race, new Timestamp(60000));
		this.persistentState = PersistentState.NEW;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public int getPoints() {
		return points;
	}

	public synchronized void increasePoints(int amount) {
		switch (this.level) {
			case 1:
				if (this.points + amount >= 1000)
					increaseLevel();
				break;
			case 2:
				if (this.points + amount >= 2000)
					increaseLevel();
				break;
			case 3:
				if (this.points + amount >= 3000)
					increaseLevel();
				break;
			case 4:
				if (this.points + amount >= 4000)
					increaseLevel();
				break;
		}
		this.points += amount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	private void increaseLevel() {
		this.level++;
		this.levelUpDate.setTime(System.currentTimeMillis());
		broadcastUpdate();
		despawnOldObjects();
		spawnNewObjects();
	}

	private void broadcastUpdate() {
		Map<Integer, Town> data = new HashMap<Integer, Town>(1);
		data.put(this.id, this);
		final SM_TOWNS_LIST packet = new SM_TOWNS_LIST(data);
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.getRace() == race)
					PacketSendUtility.sendPacket(player, packet);
			}
		});
	}

	private void spawnNewObjects() {
		List<Spawn> newSpawns = DataManager.TOWN_SPAWNS_DATA.getSpawns(this.id, this.level);
		int worldId = DataManager.TOWN_SPAWNS_DATA.getWorldIdForTown(this.id);
		for (Spawn spawn : newSpawns) {
			for (SpawnSpotTemplate sst : spawn.getSpawnSpotTemplates()) {
				SpawnTemplate spawnTemplate = SpawnEngine.addNewSpawn(worldId, spawn.getNpcId(), sst.getX(), sst.getY(), sst.getZ(),
					sst.getHeading(), spawn.getRespawnTime());
				spawnTemplate.setStaticId(sst.getStaticId());
				spawnTemplate.setRandomWalk(0);
				VisibleObject object = SpawnEngine.spawnObject(spawnTemplate, 1);
				if (object instanceof Npc) {
					((Npc) object).setTownId(this.id);
					spawnedNpcs.add((Npc) object);
				}
			}
		}
	}

	private void despawnOldObjects() {
		for (Npc npc : spawnedNpcs)
			npc.getController().delete();
		spawnedNpcs.clear();
	}

	public Race getRace() {
		return this.race;
	}

	public Timestamp getLevelUpDate() {
		return levelUpDate;
	}

	public PersistentState getPersistentState() {
		return persistentState;
	}

	public void setPersistentState(PersistentState state) {
		if (this.persistentState == PersistentState.NEW && state == PersistentState.UPDATE_REQUIRED)
			return;
		else
			this.persistentState = state;
	}

}
