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
 * @author synchro2
 */
public class WeddingsConfig {

	@Property(key = "gameserver.weddings.enable", defaultValue = "false")
	public static boolean WEDDINGS_ENABLE;

	@Property(key = "gameserver.weddings.gift.enable", defaultValue = "false")
	public static boolean WEDDINGS_GIFT_ENABLE;

	@Property(key = "gameserver.weddings.gift", defaultValue = "0")
	public static int WEDDINGS_GIFT;

	@Property(key = "gameserver.weddings.suit.enable", defaultValue = "false")
	public static boolean WEDDINGS_SUIT_ENABLE;

	@Property(key = "gameserver.weddings.suit", defaultValue = "")
	public static String WEDDINGS_SUITS;

	@Property(key = "gameserver.weddings.membership", defaultValue = "0")
	public static byte WEDDINGS_MEMBERSHIP;

	@Property(key = "gameserver.weddings.same_sex", defaultValue = "false")
	public static boolean WEDDINGS_SAME_SEX;

	@Property(key = "gameserver.weddings.races", defaultValue = "false")
	public static boolean WEDDINGS_DIFF_RACES;

	@Property(key = "gameserver.weddings.kinah", defaultValue = "0")
	public static int WEDDINGS_KINAH;

	@Property(key = "gameserver.weddings.toll", defaultValue = "0")
	public static int WEDDINGS_TOLL;

	@Property(key = "gameserver.weddings.dissmistoll", defaultValue = "0")
	public static int WEDDINGS_DISSMIS_TOLL;

	@Property(key = "gameserver.weddings.dissmiskinah", defaultValue = "0")
	public static int WEDDINGS_DISSMIS_KINAH;

	@Property(key = "gameserver.weddings.announce", defaultValue = "true")
	public static boolean WEDDINGS_ANNOUNCE;
}