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

package org.typezero.gameserver.model.gameobjects.state;

/**
 * @author ATracer, Sweetkr
 */
public enum CreatureState {

	ACTIVE(1), // basic 1
	FLYING(1 << 1), // 2
	FLIGHT_TELEPORT(1 << 1), // 2
	RESTING(1 << 2), // 4
	DEAD(3 << 1), // 6
	CHAIR(3 << 1), // 6
	FLOATING_CORPSE(1 << 3), // 8
	PRIVATE_SHOP(5 << 1), // 10
	LOOTING(3 << 2), // 12
	WEAPON_EQUIPPED(1 << 5), // 32
	WALKING(1 << 6), // 64
	NPC_IDLE(1 << 6), // 64 (for npc)
	POWERSHARD(1 << 7), // 128
	TREATMENT(1 << 8), // 256
	GLIDING(1 << 9); // 512

	/**
	 * Standing, path flying, free flying, riding, sitting, sitting on chair, dead, fly dead, private shop, looting, fly
	 * looting, default
	 */

	private int id;

	private CreatureState(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
