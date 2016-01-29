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

package org.typezero.gameserver.model.templates.spawns.beritraspawns;

import org.typezero.gameserver.model.beritra.BeritraStateType;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnSpotTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author Rinzler (Encom)
 */

public class BeritraSpawnTemplate extends SpawnTemplate
{
	private int id;
	private BeritraStateType beritraType;

	public BeritraSpawnTemplate(SpawnGroup2 spawnGroup, SpawnSpotTemplate spot) {
		super(spawnGroup, spot);
	}

	public BeritraSpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int entityId, int fly) {
		super(spawnGroup, x, y, z, heading, randWalk, walkerId, entityId, fly);
	}

	public int getId() {
		return id;
	}

	public BeritraStateType getBStateType() {
		return beritraType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBStateType(BeritraStateType beritraType) {
		this.beritraType = beritraType;
	}

	public final boolean isBeritraInvasion() {
		return beritraType.equals(BeritraStateType.INVASION);
	}

	public final boolean isBeritraPeace() {
		return beritraType.equals(BeritraStateType.PEACE);
	}
}
