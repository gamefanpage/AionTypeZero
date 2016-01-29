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

package org.typezero.gameserver.model.instance.playerreward;

/**
 *
 * @author xTz
 */
public class CruciblePlayerReward extends InstancePlayerReward {

	private int spawnPosition;
	private boolean isRewarded = false;
	private int insignia;
	private boolean isPlayerLeave = false;
	private boolean isPlayerDefeated = false;

	public CruciblePlayerReward(Integer object) {
		super(object);
	}

	public boolean isRewarded() {
		return isRewarded;
	}

	public void setRewarded() {
		isRewarded = true;
	}

	public void setInsignia(int insignia) {
		this.insignia = insignia;
	}

	public int getInsignia() {
		return insignia;
	}

	public void setSpawnPosition(int spawnPosition) {
		this.spawnPosition = spawnPosition;
	}

	public int getSpawnPosition() {
		return spawnPosition;
	}

	public boolean isPlayerLeave() {
		return isPlayerLeave;
	}

	public void setPlayerLeave() {
		isPlayerLeave = true;
	}

	public void setPlayerDefeated(boolean value) {
		isPlayerDefeated = value;
	}

	public boolean isPlayerDefeated() {
		return isPlayerDefeated;
	}
}
