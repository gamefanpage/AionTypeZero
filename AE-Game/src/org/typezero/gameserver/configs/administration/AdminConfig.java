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

package org.typezero.gameserver.configs.administration;

import com.aionemu.commons.configuration.Property;

/**
 * @author ATracer
 */
public class AdminConfig {

	/**
	 * Admin properties
	 */
	@Property(key = "gameserver.administration.gmlevel", defaultValue = "3")
	public static int GM_LEVEL;
	@Property(key = "gameserver.administration.gmpanel", defaultValue = "3")
	public static int GM_PANEL;
	@Property(key = "gameserver.administration.baseshield", defaultValue = "3")
	public static int COMMAND_BASESHIELD;
	@Property(key = "gameserver.administration.flight.freefly", defaultValue = "3")
	public static int GM_FLIGHT_FREE;
	@Property(key = "gameserver.administration.flight.unlimited", defaultValue = "3")
	public static int GM_FLIGHT_UNLIMITED;
	@Property(key = "gameserver.administration.doors.opening", defaultValue = "3")
	public static int DOORS_OPEN;
	@Property(key = "gameserver.administration.auto.res", defaultValue = "3")
	public static int ADMIN_AUTO_RES;
	@Property(key = "gameserver.administration.instancereq", defaultValue = "3")
	public static int INSTANCE_REQ;
	@Property(key = "gameserver.administration.view.player", defaultValue = "3")
	public static int ADMIN_VIEW_DETAILS;
	@Property(key = "gameserver.administration.special.skill", defaultValue = "3")
	public static int COMMAND_SPECIAL_SKILL;

	/**
	 * Admin options
	 */
	@Property(key = "gameserver.administration.invis.gm.connection", defaultValue = "false")
	public static boolean INVISIBLE_GM_CONNECTION;
	@Property(key = "gameserver.administration.enemity.gm.connection", defaultValue = "Normal")
	public static String ENEMITY_MODE_GM_CONNECTION;
	@Property(key = "gameserver.administration.invul.gm.connection", defaultValue = "false")
	public static boolean INVULNERABLE_GM_CONNECTION;
	@Property(key = "gameserver.administration.vision.gm.connection", defaultValue = "false")
	public static boolean VISION_GM_CONNECTION;
	@Property(key = "gameserver.administration.whisper.gm.connection", defaultValue = "false")
	public static boolean WHISPER_GM_CONNECTION;
	@Property(key = "gameserver.administration.quest.dialog.log", defaultValue = "false")
	public static boolean QUEST_DIALOG_LOG;
	@Property(key = "gameserver.administration.trade.item.restriction", defaultValue = "false")
	public static boolean ENABLE_TRADEITEM_RESTRICTION;

	/**
	 * Custom TAG based on access level
	 */
	@Property(key = "gameserver.customtag.enable", defaultValue = "false")
	public static boolean CUSTOMTAG_ENABLE;
	@Property(key = "gameserver.customtag.access1", defaultValue = "<GM> %s")
	public static String CUSTOMTAG_ACCESS1;
	@Property(key = "gameserver.customtag.access2", defaultValue = "<HEADGM> %s")
	public static String CUSTOMTAG_ACCESS2;
	@Property(key = "gameserver.customtag.access3", defaultValue = "<ADMIN> %s")
	public static String CUSTOMTAG_ACCESS3;
	@Property(key = "gameserver.customtag.access4", defaultValue = "<TAG_HERE> %s")
	public static String CUSTOMTAG_ACCESS4;
	@Property(key = "gameserver.customtag.access5", defaultValue = "<TAG_HERE> %s")
	public static String CUSTOMTAG_ACCESS5;
	@Property(key = "gameserver.customtag.access6", defaultValue = "<TAG_HERE> %s")
	public static String CUSTOMTAG_ACCESS6;

	@Property(key = "gameserver.admin.announce.levels", defaultValue = "*")
	public static String ANNOUNCE_LEVEL_LIST;
}
