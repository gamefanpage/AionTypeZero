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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.model.templates.event.EventTemplate;
import org.typezero.gameserver.spawnengine.SpawnHandlerType;

/**
 * @author xTz
 * @modified Rolandas
 */
public class SpawnTemplate {

	private float x;
	private float y;
	private float z;
	private byte h;
	private int staticId;
	private int randomWalk;
	private String walkerId;
	private int walkerIdx;
	private int fly;
	private String anchor;
	private boolean isUsed;
	private SpawnGroup2 spawnGroup;
	private EventTemplate eventTemplate;
	private SpawnModel model;
	private int state;
	private int creatorId;
	private String masterName = StringUtils.EMPTY;
	private TemporarySpawn temporarySpawn;
	private VisibleObject visibleObject;

	public SpawnTemplate(SpawnGroup2 spawnGroup, SpawnSpotTemplate spot) {
		this.spawnGroup = spawnGroup;
		x = spot.getX();
		y = spot.getY();
		z = spot.getZ();
		h = spot.getHeading();
		staticId = spot.getStaticId();
		randomWalk = spot.getRandomWalk();
		walkerId = spot.getWalkerId();
		fly = spot.getFly();
		anchor = spot.getAnchor();
		walkerIdx = spot.getWalkerIndex();
		model = spot.getModel();
		state = spot.getState();
		temporarySpawn = spot.getTemporarySpawn();
	}

	public SpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId,
		int staticId, int fly) {
		this.spawnGroup = spawnGroup;
		this.x = x;
		this.y = y;
		this.z = z;
		h = heading;
		this.randomWalk = randWalk;
		this.walkerId = walkerId;
		this.staticId = staticId;
		this.fly = fly;
		addTemplate();
	}

	private void addTemplate() {
		spawnGroup.addSpawnTemplate(this);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public byte getHeading() {
		return h;
	}

	public int getStaticId() {
		return staticId;
	}

	public void setStaticId(int staticId) {
		this.staticId = staticId;
	}

	public int getRandomWalk() {
		return randomWalk;
	}

	public void setRandomWalk(int randomWalk) {
		this.randomWalk = randomWalk;
	}

	public int getFly() {
		return fly;
	}

	public boolean canFly() {
		return fly > 0;
	}

	public void setUse(boolean use) {
		isUsed = use;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public int getNpcId() {
		return spawnGroup.getNpcId();
	}

	public int getWorldId() {
		return spawnGroup.getWorldId();
	}

	public SpawnTemplate changeTemplate() {
		return spawnGroup.getRndTemplate();
	}

	public int getRespawnTime() {
		return spawnGroup.getRespawnTime();
	}

    public int getRandomSpawntime() {
        return spawnGroup.getRandomSpawnTime();
    }

	public void setRespawnTime(int respawnTime) {
		spawnGroup.setRespawnTime(respawnTime);
	}

	public TemporarySpawn getTemporarySpawn() {
		return temporarySpawn != null ? temporarySpawn : spawnGroup.geTemporarySpawn();
	}

	public SpawnHandlerType getHandlerType() {
		return spawnGroup.getHandlerType();
	}

	public String getAnchor() {
		return anchor;
	}

	public boolean hasRandomWalk() {
		return randomWalk != 0;
	}

	public boolean isNoRespawn() {
		return spawnGroup.getRespawnTime() == 0;
	}

	public boolean hasPool() {
		return spawnGroup.hasPool();
	}

	public String getWalkerId() {
		return walkerId;
	}

	public void setWalkerId(String walkerId) {
		this.walkerId = walkerId;
	}

	public int getWalkerIndex() {
		return walkerIdx;
	}

	public boolean isTemporarySpawn() {
		return spawnGroup.isTemporarySpawn();
	}

	public boolean isEventSpawn() {
		return eventTemplate != null;
	}

	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}

	public SpawnModel getModel() {
		return model;
	}

	public int getState() {
		return state;
	}

	/**
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the masterName
	 */
	public String getMasterName() {
		return masterName;
	}

	/**
	 * @param masterName the masterName to set
	 */
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public VisibleObject getVisibleObject() {
		return visibleObject;
	}

	public void setVisibleObject(VisibleObject visibleObject) {
		this.visibleObject = visibleObject;
	}
}
