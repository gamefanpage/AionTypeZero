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
import java.util.Calendar;

public class GSConfig {

	/**
	* Gameserver
	*/

	/* Server Country Code */
	@Property(key = "gameserver.country.code", defaultValue = "1")
	public static int SERVER_COUNTRY_CODE;

	/* Server Credits Name */
	@Property(key = "gameserver.name", defaultValue = "Aion")
	public static String SERVER_NAME;

	@Property(key = "gameserver.site", defaultValue = "Aion")
	public static String SITE;

	@Property(key = "gameserver.forum", defaultValue = "Aion")
	public static String FORUM;

	@Property(key = "gameserver.vk", defaultValue = "Aion")
	public static String VK;

	@Property(key = "gameserver.mmotop", defaultValue = "Aion")
	public static String MMOTOP;

	@Property(key = "gameserver.cash_point", defaultValue = "Aion")
	public static String CASH_POINT;

     /* Server Language */
    @Property(key = "gameserver.language", defaultValue = "en")
    public static String SERVER_LANGUAGE;

	/* Players Max Level */
	@Property(key = "gameserver.players.max.level", defaultValue = "65")
	public static int PLAYER_MAX_LEVEL;

	/* Time Zone name (used for events & timed spawns) */
	@Property(key = "gameserver.timezone", defaultValue = "")
	public static String TIME_ZONE_ID = Calendar.getInstance().getTimeZone().getID();

	/* Enable connection with CS (ChatServer) */
	@Property(key = "gameserver.chatserver.enable", defaultValue = "false")
	public static boolean ENABLE_CHAT_SERVER;

	/* Server MOTD Display revision */
	@Property(key = "gameserver.revisiondisplay.enable", defaultValue = "false")
	public static boolean SERVER_MOTD_DISPLAYREV;

	/**
	* Character creation
	*/

	@Property(key = "gameserver.character.creation.mode", defaultValue = "0")
	public static int CHARACTER_CREATION_MODE;

	@Property(key = "gameserver.character.limit.count", defaultValue = "8")
	public static int CHARACTER_LIMIT_COUNT;

	@Property(key = "gameserver.character.faction.limitation.mode", defaultValue = "0")
	public static int CHARACTER_FACTION_LIMITATION_MODE;

	@Property(key = "gameserver.ratio.limitation.enable", defaultValue = "false")
	public static boolean ENABLE_RATIO_LIMITATION;

	@Property(key = "gameserver.ratio.min.value", defaultValue = "60")
	public static int RATIO_MIN_VALUE;

	@Property(key = "gameserver.ratio.min.required.level", defaultValue = "10")
	public static int RATIO_MIN_REQUIRED_LEVEL;

	@Property(key = "gameserver.ratio.min.characters_count", defaultValue = "50")
	public static int RATIO_MIN_CHARACTERS_COUNT;

	@Property(key = "gameserver.ratio.high_player_count.disabling", defaultValue = "500")
	public static int RATIO_HIGH_PLAYER_COUNT_DISABLING;

	/**
	* Misc
	*/

	@Property(key = "gameserver.character.reentry.time", defaultValue = "20")
	public static int CHARACTER_REENTRY_TIME;

}
