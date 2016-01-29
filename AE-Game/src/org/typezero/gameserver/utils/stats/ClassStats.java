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

package org.typezero.gameserver.utils.stats;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.utils.stats.enums.ACCURACY;
import org.typezero.gameserver.utils.stats.enums.AGILITY;
import org.typezero.gameserver.utils.stats.enums.ATTACK_RANGE;
import org.typezero.gameserver.utils.stats.enums.ATTACK_SPEED;
import org.typezero.gameserver.utils.stats.enums.BLOCK;
import org.typezero.gameserver.utils.stats.enums.EARTH_RESIST;
import org.typezero.gameserver.utils.stats.enums.EVASION;
import org.typezero.gameserver.utils.stats.enums.FIRE_RESIST;
import org.typezero.gameserver.utils.stats.enums.FLY_SPEED;
import org.typezero.gameserver.utils.stats.enums.HEALTH;
import org.typezero.gameserver.utils.stats.enums.KNOWLEDGE;
import org.typezero.gameserver.utils.stats.enums.MAGIC_ACCURACY;
import org.typezero.gameserver.utils.stats.enums.MAIN_HAND_ACCURACY;
import org.typezero.gameserver.utils.stats.enums.MAIN_HAND_ATTACK;
import org.typezero.gameserver.utils.stats.enums.MAIN_HAND_CRITRATE;
import org.typezero.gameserver.utils.stats.enums.MAXHP;
import org.typezero.gameserver.utils.stats.enums.PARRY;
import org.typezero.gameserver.utils.stats.enums.POWER;
import org.typezero.gameserver.utils.stats.enums.SPEED;
import org.typezero.gameserver.utils.stats.enums.WATER_RESIST;
import org.typezero.gameserver.utils.stats.enums.WILL;
import org.typezero.gameserver.utils.stats.enums.WIND_RESIST;

/**
 * @author ATracer
 */
public class ClassStats {

	/**
	 * @param playerClass
	 * @param level
	 * @return maximum HP stat for player class and level
	 */
	public static int getMaxHpFor(PlayerClass playerClass, int level) {
		return MAXHP.valueOf(playerClass.toString()).getMaxHpFor(level);
	}

	/**
	 * @param playerClass
	 * @return power stat for player class and level
	 */
	public static int getPowerFor(PlayerClass playerClass) {
		return POWER.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getHealthFor(PlayerClass playerClass) {
		return HEALTH.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAgilityFor(PlayerClass playerClass) {
		return AGILITY.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAccuracyFor(PlayerClass playerClass) {
		return ACCURACY.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getKnowledgeFor(PlayerClass playerClass) {
		return KNOWLEDGE.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWillFor(PlayerClass playerClass) {
		return WILL.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandAttackFor(PlayerClass playerClass) {
		return MAIN_HAND_ATTACK.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandCritRateFor(PlayerClass playerClass) {
		return MAIN_HAND_CRITRATE.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandAccuracyFor(PlayerClass playerClass) {
		return MAIN_HAND_ACCURACY.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWaterResistFor(PlayerClass playerClass) {
		return WATER_RESIST.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWindResistFor(PlayerClass playerClass) {
		return WIND_RESIST.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getEarthResistFor(PlayerClass playerClass) {
		return EARTH_RESIST.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getFireResistFor(PlayerClass playerClass) {
		return FIRE_RESIST.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMagicAccuracyFor(PlayerClass playerClass) {
		return MAGIC_ACCURACY.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getEvasionFor(PlayerClass playerClass) {
		return EVASION.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getBlockFor(PlayerClass playerClass) {
		return BLOCK.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getParryFor(PlayerClass playerClass) {
		return PARRY.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAttackRangeFor(PlayerClass playerClass) {
		return ATTACK_RANGE.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAttackSpeedFor(PlayerClass playerClass) {
		return ATTACK_SPEED.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getFlySpeedFor(PlayerClass playerClass) {
		return FLY_SPEED.valueOf(playerClass.toString()).getValue();
	}

	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getSpeedFor(PlayerClass playerClass) {
		return SPEED.valueOf(playerClass.toString()).getValue();
	}
}
