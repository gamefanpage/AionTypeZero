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

public class GeoDataConfig {

	/**
	 * Geodata enable
	 */
	@Property(key = "gameserver.geodata.enable", defaultValue = "false")
	public static boolean GEO_ENABLE;

    @Property(key = "gameserver.geo.npcs.exclude", defaultValue = "0")
    public static String GEO_NPCS_EXCUDE;
    /**
	 * Enable canSee checks using geodata.
	 */
	@Property(key = "gameserver.geodata.cansee.enable", defaultValue = "true")
	public static boolean CANSEE_ENABLE;

	/**
	 * Enable Fear skill using geodata.
	 */
	@Property(key = "gameserver.geodata.fear.enable", defaultValue = "true")
	public static boolean FEAR_ENABLE;

	/**
	 * Enable Geo checks during npc movement (prevent flying mobs)
	 */
	@Property(key = "gameserver.geo.npc.move", defaultValue = "false")
	public static boolean GEO_NPC_MOVE;

	/**
	 * Enable npc checks aggro target visibility range (canSee)
	 */
	@Property(key = "gameserver.geo.npc.aggro", defaultValue = "false")
	public static boolean GEO_NPC_AGGRO;

	/**
	 * Enable geo materials using skills
	 */
	@Property(key = "gameserver.geo.materials.enable", defaultValue = "false")
	public static boolean GEO_MATERIALS_ENABLE;

	/**
	 * Show collision zone name and skill id
	 */
	@Property(key = "gameserver.geo.materials.showdetails", defaultValue = "false")
	public static boolean GEO_MATERIALS_SHOWDETAILS;

	/**
	 * Enable geo shields
	 */
	@Property(key = "gameserver.geo.shields.enable", defaultValue = "false")
	public static boolean GEO_SHIELDS_ENABLE;

	/**
	 * Enable geo doors
	 */
	@Property(key = "gameserver.geo.doors.enable", defaultValue = "false")
	public static boolean GEO_DOORS_ENABLE;

	/**
	 * Object factory for geodata primitives enabled
	 */
	@Property(key = "gameserver.geodata.objectfactory.enabled", defaultValue = "true")
	public static boolean GEO_OBJECT_FACTORY_ENABLE;
}
