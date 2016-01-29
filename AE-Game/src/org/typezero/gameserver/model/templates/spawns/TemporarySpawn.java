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

import org.typezero.gameserver.utils.gametime.GameTime;
import org.typezero.gameserver.utils.gametime.GameTimeManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "TemporarySpawn")
public class TemporarySpawn {
	@XmlAttribute(name = "spawn_time") // *.*.*  hour.day.month (* == all)
	private String spawnTime;

	@XmlAttribute(name = "despawn_time") // *.*.*  hour.day.month (* == all)
	private String despawnTime;

	public String getSpawnTime() {
		return spawnTime;
	}

	public Integer geSpawnHour() {
		return getTime(spawnTime, 0);
	}

	public Integer geSpawnDay() {
		return getTime(spawnTime, 1);
	}

	public Integer getSpawnMonth() {
		return getTime(spawnTime, 2);
	}

	public Integer geDespawnHour() {
		return getTime(despawnTime, 0);
	}

	public Integer geDespawnDay() {
		return getTime(despawnTime, 1);
	}

	public Integer getDespawnMonth() {
		return getTime(despawnTime, 2);
	}

	private Integer getTime(String time, int type) {
		String result = time.split("\\.")[type];
		if (result.equals("*")) {
			return null;
		}
		return Integer.parseInt(result);
	}

	public String getDespawnTime() {
		return despawnTime;
	}

	private boolean isTime(Integer hour, Integer day, Integer month) {
		GameTime gameTime = GameTimeManager.getGameTime();
		if (hour != null && hour == gameTime.getHour()) {
			if (day == null) {
				return true;
			}
			if (day == gameTime.getDay()) {
				return month == null || month == gameTime.getMonth();
			}
		}
		return false;
	}

	public boolean canSpawn() {
		return isTime(geSpawnHour(), geSpawnDay(), getSpawnMonth());
	}

	public boolean canDespawn() {
		return isTime(geDespawnHour(), geDespawnDay(), getDespawnMonth());
	}

	public boolean isInSpawnTime() {
		GameTime gameTime = GameTimeManager.getGameTime();
		Integer spawnHour = geSpawnHour();
		Integer spawnDay = geSpawnDay();
		Integer spawnMonth = getSpawnMonth();
		Integer despawnHour = geDespawnHour();
		Integer despawnDay = geDespawnDay();
		Integer despawnMonth = getDespawnMonth();
		int curentHour = gameTime.getHour();
		int curentDay = gameTime.getDay();
		int curentMonth = gameTime.getMonth();

		if (spawnMonth != null) {
			if (!checkTime(curentMonth, spawnMonth, despawnMonth)) {
				return false;
			}
		}
		if (spawnDay != null) {
			if (!checkTime(curentDay, spawnDay, despawnDay)) {
				return false;
			}
		}
		if (spawnMonth == null && spawnDay == null && !checkHour(curentHour, spawnHour, despawnHour)) {
			return false;
		}
		return true;
	}

	private boolean checkTime(int curentTime, int spawnTime, int despawnTime) {
		if (spawnTime < despawnTime) {
			if (!(curentTime >= spawnTime && curentTime <= despawnTime)) {
				return false;
			}
		}
		else if (spawnTime > despawnTime) {
			if (!(curentTime >= spawnTime || curentTime <= despawnTime)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkHour(int curentTime, int spawnTime, int despawnTime) {
		if (spawnTime < despawnTime) {
			if (!(curentTime >= spawnTime && curentTime < despawnTime)) {
				return false;
			}
		}
		else if (spawnTime > despawnTime) {
			if (!(curentTime >= spawnTime || curentTime < despawnTime)) {
				return false;
			}
		}
		return true;
	}

}
