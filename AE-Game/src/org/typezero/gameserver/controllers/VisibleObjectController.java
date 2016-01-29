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

package org.typezero.gameserver.controllers;

import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.world.World;

/**
 * This class is for controlling VisibleObjects [players, npc's etc]. Its controlling movement, visibility etc.
 *
 * @author -Nemesiss-
 */
public abstract class VisibleObjectController<T extends VisibleObject> {

	/**
	 * Object that is controlled by this controller.
	 */
	private T owner;

	/**
	 * Set owner (controller object).
	 *
	 * @param owner
	 */
	public void setOwner(T owner) {
		this.owner = owner;
	}

	/**
	 * Get owner (controller object).
	 */
	public T getOwner() {
		return owner;
	}

	/**
	 * Called when controlled object is seeing other VisibleObject.
	 *
	 * @param object
	 */
	public void see(VisibleObject object) {

	}

	/**
	 * Called when controlled object no longer see some other VisibleObject.
	 *
	 * @param object
	 * @param isOutOfRange
	 */
	public void notSee(VisibleObject object, boolean isOutOfRange) {

	}

	/**
	 * Removes controlled object from the world.
	 */
	public void delete() {
		/**
		 * despawn object from world.
		 */
		if (getOwner().isSpawned())
			World.getInstance().despawn(getOwner());
		/**
		 * Delete object from World.
		 */

		World.getInstance().removeObject(getOwner());
	}

	/**
	 * Called before object is placed into world
	 */
	public void onBeforeSpawn() {

	}

	/**
	 * Called after object was placed into world
	 */
	public void onAfterSpawn() {

	}

	/**
	 * Properly despawn object
	 */
	public void onDespawn() {
	}

	/**
	 * This method should be called to make despawn of VisibleObject and delete it from the world
	 */
	public void onDelete() {
		if (getOwner().isInWorld()) {
			this.onDespawn();
			this.delete();
		}
	}
}
