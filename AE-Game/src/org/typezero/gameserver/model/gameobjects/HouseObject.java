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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.controllers.PlaceableObjectController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.IExpirable;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.AbstractHouseObject;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.model.templates.housing.HousingCategory;
import org.typezero.gameserver.model.templates.housing.LimitType;
import org.typezero.gameserver.model.templates.housing.PlaceArea;
import org.typezero.gameserver.model.templates.housing.PlaceLocation;
import org.typezero.gameserver.model.templates.housing.PlaceableHouseObject;
import org.typezero.gameserver.model.templates.item.ItemQuality;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * @author Rolandas
 */
public abstract class HouseObject<T extends PlaceableHouseObject> extends VisibleObject implements IExpirable {

	private int expireEnd;
	private float x;
	private float y;
	private float z;
	private byte heading;
	private int ownerUsedCount = 0;
	private int visitorUsedCount = 0;
	private Integer color = null;
	private int colorExpireEnd;

	private House ownerHouse;
	// don't set it directly, ever!!! Use setPersistentState() method instead
	private PersistentState persistentState = PersistentState.NEW;

	public HouseObject(House owner, int objId, int templateId) {
		super(objId, new PlaceableObjectController<T>(), null, DataManager.HOUSING_OBJECT_DATA.getTemplateById(templateId), null);
		this.ownerHouse = owner;
		getController().setOwner(this);
		setKnownlist(new PlayerAwareKnownList(this));
	}

	public PersistentState getPersistentState() {
		return persistentState;
	}

	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
					this.persistentState = PersistentState.NOACTION;
				else if (this.persistentState != PersistentState.DELETED) {
					this.persistentState = PersistentState.DELETED;
					ownerHouse.getRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
					break;
			default:
				if (this.persistentState != persistentState) {
					this.persistentState = persistentState;
					ownerHouse.getRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
		}
	}

	@Override
	public int getExpireTime() {
		return expireEnd;
	}

	public void setExpireTime(int time) {
		expireEnd = time;
	}

	@Override
	public void expireEnd(Player player) {
		setPersistentState(PersistentState.DELETED);
	}

	/**
	 * Gets seconds left for the object use. If has no expiration return -1
	 *
	 * @return
	 */
	public int getUseSecondsLeft() {
		if (expireEnd == 0)
			return -1;
		int diff = expireEnd - (int) (System.currentTimeMillis() / 1000);
		if (diff < 0)
			return 0;
		return diff;
	}

	@Override
	public void expireMessage(Player player, int time) {
		// TODO Add if it exists
	}

	@Override
	public String getName() {
		return String.valueOf(objectTemplate.getNameId());
	}

	@SuppressWarnings("unchecked")
	public T getObjectTemplate() {
		return (T) objectTemplate;
	}

	@Override
	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (this.x != x) {
			this.x = x;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
				position.setXYZH(x, null, null, null);
		}
	}

	@Override
	public float getY() {
		return y;
	}

	public void setY(float y) {
		if (this.y != y) {
			this.y = y;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
				position.setXYZH(null, y, null, null);
		}
	}

	@Override
	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		if (this.z != z) {
			this.z = z;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
				position.setXYZH(null, null, z, null);
		}
	}

	@Override
	public byte getHeading() {
		return heading;
	}

	public void setHeading(byte heading) {
		if (this.heading != heading) {
			this.heading = heading;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
				position.setXYZH(null, null, null, heading);
		}
	}

	public int getRotation() {
		int rotation = this.heading & 0xFF;
		return rotation * 3;
	}

	public void setRotation(int rotation) {
		setHeading((byte) Math.ceil(rotation / 3f));
	}

	public PlaceLocation getPlaceLocation() {
		return ((PlaceableHouseObject) objectTemplate).getLocation();
	}

	public PlaceArea getPlaceArea() {
		return ((PlaceableHouseObject) objectTemplate).getArea();
	}

	public int getPlacementLimit(boolean trial) {
		LimitType limitType = ((PlaceableHouseObject) objectTemplate).getPlacementLimit();
		HouseType size = HouseType.fromValue(ownerHouse.getBuilding().getSize());
		if (trial)
			return limitType.getTrialObjectPlaceLimit(size);
		return limitType.getObjectPlaceLimit(size);
	}

	public ItemQuality getQuality() {
		return ((AbstractHouseObject) objectTemplate).getQuality();
	}

	public float getTalkingDistance() {
		return ((AbstractHouseObject) objectTemplate).getTalkingDistance();
	}

	public HousingCategory getCategory() {
		return ((AbstractHouseObject) objectTemplate).getCategory();
	}

	public House getOwnerHouse() {
		return ownerHouse;
	}

	public int getPlayerId() {
		return ownerHouse.getOwnerId();
	}

	public int getOwnerUsedCount() {
		return ownerUsedCount;
	}

	public void incrementOwnerUsedCount() {
		this.ownerUsedCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public void incrementVisitorUsedCount() {
		this.visitorUsedCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public void setOwnerUsedCount(int ownerUsedCount) {
		if (this.ownerUsedCount != ownerUsedCount) {
			this.ownerUsedCount = ownerUsedCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}

	public int getVisitorUsedCount() {
		return visitorUsedCount;
	}

	public void setVisitorUsedCount(int visitorUsedCount) {
		if (this.visitorUsedCount != visitorUsedCount) {
			this.visitorUsedCount = visitorUsedCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}

	/**
	 * Means the player has it spawned, not the game server
	 */
	public boolean isSpawnedByPlayer() {
		return x != 0 || y != 0 || z != 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PlaceableObjectController<T> getController() {
		return (PlaceableObjectController<T>) super.getController();
	}

	public void spawn() {
		if (!isSpawnedByPlayer())
			return;
		World w = World.getInstance();
		if (position == null || !isSpawned()) {
			position = w.createPosition(ownerHouse.getWorldId(), x, y, z, heading, ownerHouse.getInstanceId());
			SpawnEngine.bringIntoWorld(this);
		}
		updateKnownlist();
	}

	/**
	 * Removes house from spawn but it remains in registry
	 */
	public void removeFromHouse() {
		this.setX(0);
		this.setY(0);
		this.setZ(0);
		this.setHeading((byte) 0);
	}

	public void onUse(Player player) {
	}

	public void onDialogRequest(Player player) {
		onUse(player);
	}

	public void onDespawn() {

	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		if (color != this.color) {
			this.color = color;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}

	public int getColorExpireEnd() {
		return colorExpireEnd;
	}

	public void setColorExpireEnd(int colorExpireEnd) {
		this.colorExpireEnd = colorExpireEnd;
	}

}
