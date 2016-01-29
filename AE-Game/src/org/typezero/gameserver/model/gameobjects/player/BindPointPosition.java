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

package org.typezero.gameserver.model.gameobjects.player;

import org.typezero.gameserver.model.gameobjects.PersistentState;

/**
 * @author evilset
 */
public class BindPointPosition {

	private int mapId;
	private float x;
	private float y;
	private float z;
	private byte heading;
	private PersistentState persistentState;

	/**
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	public BindPointPosition(int mapId, float x, float y, float z, byte heading) {
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.persistentState = PersistentState.NEW;
	}

	/**
	 * @return Returns the mapId.
	 */
	public int getMapId() {
		return mapId;
	}

	/**
	 * @return Returns the x.
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return Returns the y.
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return Returns the z.
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @return Returns the heading.
	 */
	public byte getHeading() {
		return heading;
	}

	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 *          the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
	}
}
