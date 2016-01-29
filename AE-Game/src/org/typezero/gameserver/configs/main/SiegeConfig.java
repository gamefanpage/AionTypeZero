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

package org.typezero.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author Sarynth, xTz, Source
 */
public class SiegeConfig {

	/**
	 * Siege Enabled
	 */
	@Property(key = "gameserver.siege.enable", defaultValue = "true")
	public static boolean SIEGE_ENABLED;
	/**
	 * Siege Reward Rate
	 */
	@Property(key = "gameserver.siege.medal.rate", defaultValue = "1")
	public static int SIEGE_MEDAL_RATE;
	/**
	 * Siege sield Enabled
	 */
	@Property(key = "gameserver.siege.shield.enable", defaultValue = "true")
	public static boolean SIEGE_SHIELD_ENABLED;
	/**
	 * Balaur Assaults Enabled
	 */
	@Property(key = "gameserver.siege.assault.enable", defaultValue = "false")
	public static boolean BALAUR_AUTO_ASSAULT;
	@Property(key = "gameserver.siege.assault.rate", defaultValue = "1")
	public static float BALAUR_ASSAULT_RATE;

	/**
	 * Siege Race Protector spawn shedule
	 */
	@Property(key = "gameserver.siege.protector.time", defaultValue = "0 0 21 ? * *")
	public static String RACE_PROTECTOR_SPAWN_SCHEDULE;
	/**
	 * Berserker Sunayaka spawn time
	 */
	@Property(key = "gameserver.sunayaka.time", defaultValue = "0 0 23 ? * *")
	public static String BERSERKER_SUNAYAKA_SPAWN_SCHEDULE;
	/**
	 * Berserker Sunayaka spawn time
	 */
	@Property(key = "gameserver.moltenus.time", defaultValue = "0 0 22 ? * SUN")
	public static String MOLTENUS_SPAWN_SCHEDULE;

	@Property(key = "gameserver.katalamboss.time", defaultValue = "0 0 20 ? * *")
	public static String KATALAM_BOSS_SPAWN_SCHEDULE;

	@Property(key = "gameserver.geravstegrak.time", defaultValue = "0 0 23 ? * WED,SUN")
	public static String GERA_VS_TEGRAK_SPAWN_SCHEDULE;
	/**
	 * Legendary npc's health mod
	 */
	@Property(key = "gameserver.siege.health.mod", defaultValue = "false")
	public static boolean SIEGE_HEALTH_MOD_ENABLED;
	/**
	 * Legendary npc's health multiplier
	 */
	@Property(key = "gameserver.siege.health.multiplier", defaultValue = "1.0")
	public static double SIEGE_HEALTH_MULTIPLIER = 1.0;
	/**
	 * Tiamat's Incarnation dispell avatars
	 */
	@Property(key = "gameserver.siege.ida", defaultValue = "false")
	public static boolean SIEGE_IDA_ENABLED;

	/**
	 * Beritra Invasions
	 */
	@Property(key = "gameserver.beritra.enable", defaultValue = "true")
	public static boolean BERITRA_ENABLED;
	@Property(key = "gameserver.beritra.schedule", defaultValue = "0 0 4 ? * *")
	public static String BERITRA_SCHEDULE;
	@Property(key = "gameserver.beritra.duration", defaultValue = "2")
	public static int BERITRA_DURATION;

}
