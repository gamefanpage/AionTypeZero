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

public class GroupConfig {

	/**
	 * Group remove time
	 */
	@Property(key = "gameserver.playergroup.removetime", defaultValue = "600")
	public static int GROUP_REMOVE_TIME;

	/**
	 * Group max distance
	 */
	@Property(key = "gameserver.playergroup.maxdistance", defaultValue = "100")
	public static int GROUP_MAX_DISTANCE;

	/**
	 * Enable Group Invite Other Faction
	 */
	@Property(key = "gameserver.group.inviteotherfaction", defaultValue = "false")
	public static boolean GROUP_INVITEOTHERFACTION;

	/**
	 * Alliance remove time
	 */
	@Property(key = "gameserver.playeralliance.removetime", defaultValue = "600")
	public static int ALLIANCE_REMOVE_TIME;

	/**
	 * Enable Alliance Invite Other Faction
	 */
	@Property(key = "gameserver.playeralliance.inviteotherfaction", defaultValue = "false")
	public static boolean ALLIANCE_INVITEOTHERFACTION;

	@Property(key = "gameserver.team2.enable", defaultValue = "false")
	public static boolean TEAM2_ENABLE;
}
