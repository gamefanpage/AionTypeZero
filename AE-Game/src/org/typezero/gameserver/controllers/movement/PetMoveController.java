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

package org.typezero.gameserver.controllers.movement;

import org.typezero.gameserver.model.gameobjects.Pet;

/**
 * @author ATracer
 */
public class PetMoveController extends CreatureMoveController<Pet> {

	protected float targetDestX;
	protected float targetDestY;
	protected float targetDestZ;
	protected byte heading;
	protected byte movementMask;

	public PetMoveController() {
		super(null);// not used yet
	}

	@Override
	public void moveToDestination() {
	}

	@Override
	public float getTargetX2() {
		return targetDestX;
	}

	@Override
	public float getTargetY2() {
		return targetDestY;
	}

	@Override
	public float getTargetZ2() {
		return targetDestZ;
	}

	@Override
	public void setNewDirection(float x2, float y2, float z2) {
		setNewDirection(x2, y2, z2, (byte) 0);
	}

	@Override
	public void setNewDirection(float x, float y, float z, byte heading) {
		this.targetDestX = x;
		this.targetDestY = y;
		this.targetDestZ = z;
		this.heading = heading;
	}

	@Override
	public void startMovingToDestination() {
	}

	@Override
	public void abortMove() {
	}

	@Override
	public byte getMovementMask() {
		return movementMask;
	}

	@Override
	public boolean isInMove() {
		return true;
	}

	@Override
	public void setInMove(boolean value) {
	}
}
