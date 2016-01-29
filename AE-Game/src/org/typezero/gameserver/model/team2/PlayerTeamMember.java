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

package org.typezero.gameserver.model.team2;

import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public class PlayerTeamMember implements TeamMember<Player> {

	final Player player;

	private long lastOnlineTime;

	public PlayerTeamMember(Player player) {
		this.player = player;
	}

	@Override
	public Integer getObjectId() {
		return player.getObjectId();
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public Player getObject() {
		return player;
	}

	public long getLastOnlineTime() {
		return lastOnlineTime;
	}

	public void updateLastOnlineTime() {
		lastOnlineTime = System.currentTimeMillis();
	}

	public boolean isOnline() {
		return player.isOnline();
	}

	public float getX() {
		return player.getX();
	}

	public float getY() {
		return player.getY();
	}

	public float getZ() {
		return player.getZ();
	}

	public byte getHeading() {
		return player.getHeading();
	}

	public byte getLevel() {
		return player.getLevel();
	}
}
