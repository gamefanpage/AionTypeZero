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

import org.typezero.gameserver.controllers.VisibleObjectController;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.MapRegion;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.WorldType;
import org.typezero.gameserver.world.knownlist.KnownList;

/**
 * This class is representing visible objects. It's a base class for all in-game
 * objects that can be spawned in the world at some particular position (such as
 * players, npcs).<br> <br> Objects of this class, as can be spawned in game,
 * can be seen by other visible objects. To keep track of which objects are
 * already "known" by this visible object and which are not, VisibleObject is
 * containing {@link KnownList} which is responsible for holding this
 * information.
 *
 * @author -Nemesiss-
 */
public abstract class VisibleObject extends AionObject {

	protected VisibleObjectTemplate objectTemplate;

	// how far player will see visible object
	public static final float VisibilityDistance = 95;

	// maxZvisibleDistance
	public static final float maxZvisibleDistance = 95;

	/**
	 * Constructor.
	 *
	 * @param objId
	 * @param objectTemplate
	 */
	public VisibleObject(int objId, VisibleObjectController<? extends VisibleObject> controller,
			SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, WorldPosition position) {
		super(objId);
		this.controller = controller;
		this.position = position;
		this.spawn = spawnTemplate;
		this.objectTemplate = objectTemplate;
	}

	/**
	 * Position of object in the world.
	 */
	protected WorldPosition position;

	/**
	 * KnownList of this VisibleObject.
	 */
	private KnownList knownlist;

	/**
	 * Controller of this VisibleObject
	 */
	private final VisibleObjectController<? extends VisibleObject> controller;

	/**
	 * Visible object's target
	 */
	private VisibleObject target;

	/**
	 * Spawn template of this visibleObject. .
	 */
	private SpawnTemplate spawn;

	/**
	 * Returns current WorldRegion AionObject is in.
	 */
	public MapRegion getActiveRegion() {
		return position.getMapRegion();
	}

	public int getInstanceId() {
		return position.getInstanceId();
	}

	/**
	 * Return World map id.
	 */
	public int getWorldId() {
		return position.getMapId();
	}

	/**
	 * Return the WorldType of the current location
	 */
	public WorldType getWorldType() {
		return World.getInstance().getWorldMap(getWorldId()).getWorldType();
	}

	/**
	 * Return World position x
	 */
	public float getX() {
		return position.getX();
	}

	/**
	 * Return World position y
	 */
	public float getY() {
		return position.getY();
	}

	/**
	 * Return World position z
	 */
	public float getZ() {
		return position.getZ();
	}

	public void setXYZH(Float x, Float y, Float z, Byte h) {
		position.setXYZH(x, y, z, h);
	}

	/**
	 * Heading of the object. Values from <0,120)
	 */
	public byte getHeading() {
		return position.getHeading();
	}

	/**
	 * Return object position
	 *
	 * @return position.
	 */
	public WorldPosition getPosition() {
		return position;
	}

	/**
	 * Check if object is spawned.
	 *
	 * @return true if object is spawned.
	 */
	public boolean isSpawned() {
		return position.isSpawned();
	}

	/**
	 * @return
	 */
	public boolean isInWorld() {
		return World.getInstance().findVisibleObject(getObjectId()) != null;
	}

	/**
	 * Check if map is instance
	 *
	 * @return true if object in one of the instance maps
	 */
	public boolean isInInstance() {
		return position.isInstanceMap();
	}

	public void clearKnownlist() {
		getKnownList().clear();
	}

	public void updateKnownlist() {
		getKnownList().doUpdate();
	}

	public boolean canSee(Creature creature) {
		return creature != null;
	}

	/**
	 * Set KnownList to this VisibleObject
	 *
	 * @param knownlist
	 */
	public void setKnownlist(KnownList knownlist) {
		this.knownlist = knownlist;
	}

	/**
	 * Returns KnownList of this VisibleObject.
	 *
	 * @return knownList.
	 */
	public KnownList getKnownList() {
		return knownlist;
	}

	/**
	 * Return VisibleObjectController of this VisibleObject
	 *
	 * @return VisibleObjectController.
	 */
	public VisibleObjectController<? extends VisibleObject> getController() {
		return controller;
	}

	/**
	 * @return VisibleObject
	 */
	public final VisibleObject getTarget() {
		return target;
	}

	/**
	 * @return distance to target or 0 if no target
	 */
	public float getDistanceToTarget() {
		VisibleObject currTarget = target;
		if (currTarget == null) {
			return 0;
		}
		return (float) MathUtil.getDistance(getX(), getY(), getZ(), currTarget.getX(), currTarget.getY(), currTarget.getZ())
				- this.getObjectTemplate().getBoundRadius().getCollision()
				- currTarget.getObjectTemplate().getBoundRadius().getCollision();
	}

	/**
	 * @param creature
	 */
	public void setTarget(VisibleObject creature) {
		target = creature;
	}

	/**
	 * @param objectId
	 * @return target is object with id equal to objectId
	 */
	public boolean isTargeting(int objectId) {
		return target != null && target.getObjectId() == objectId;
	}

	/**
	 * Return spawn template of this VisibleObject
	 *
	 * @return SpawnTemplate
	 */
	public SpawnTemplate getSpawn() {
		return spawn;
	}

	public void setSpawn(SpawnTemplate spawn) {
		this.spawn = spawn;
	}

	/**
	 * @return the objectTemplate
	 */
	public VisibleObjectTemplate getObjectTemplate() {
		return objectTemplate;
	}

	/**
	 * @param objectTemplate the objectTemplate to set
	 */
	public void setObjectTemplate(VisibleObjectTemplate objectTemplate) {
		this.objectTemplate = objectTemplate;
	}

	/**
	 * @param position
	 */
	public void setPosition(WorldPosition position) {
		this.position = position;
	}

	public float getVisibilityDistance() {
		return VisibilityDistance;
	}

	public float getMaxZVisibleDistance() {
		return maxZvisibleDistance;
	}

	@Override
	public String toString() {
		if (objectTemplate == null)
			return super.toString();
		return objectTemplate.getName() + " (" + objectTemplate.getTemplateId() + ")";
	}

}
