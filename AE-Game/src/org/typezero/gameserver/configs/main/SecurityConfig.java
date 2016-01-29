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

public class SecurityConfig {

	@Property(key = "gameserver.security.gmaudit.message.broadcast", defaultValue = "false")
	public static boolean GM_AUDIT_MESSAGE_BROADCAST;

	@Property(key = "gameserver.security.antihack.invis", defaultValue = "false")
	public static boolean INVIS;

	@Property(key = "gameserver.security.antihack.teleportation", defaultValue = "false")
	public static boolean TELEPORTATION;

	@Property(key = "gameserver.security.antihack.speedhack", defaultValue = "false")
	public static boolean SPEEDHACK;

	@Property(key = "gameserver.security.antihack.speedhack.counter", defaultValue = "1")
	public static int SPEEDHACK_COUNTER;

	@Property(key = "gameserver.security.antihack.abnormal", defaultValue = "false")
	public static boolean ABNORMAL;

	@Property(key = "gameserver.security.antihack.abnormal.counter", defaultValue = "1")
	public static int ABNORMAL_COUNTER;

	@Property(key = "gameserver.security.antihack.punish", defaultValue = "0")
	public static int PUNISH;

	@Property(key = "gameserver.security.noanimation", defaultValue = "false")
	public static boolean NO_ANIMATION;

	@Property(key = "gameserver.security.noanimation.kick", defaultValue = "false")
	public static boolean NO_ANIMATION_KICK;

	@Property(key = "gameserver.security.noanimation.value", defaultValue = "0.1")
	public static float NO_ANIMATION_VALUE;

        @Property(key = "gameserver.security.motion.time.enable", defaultValue = "true")
	public static boolean MOTION_TIME;

	@Property(key = "gameserver.security.captcha.enable", defaultValue = "false")
	public static boolean CAPTCHA_ENABLE;

	@Property(key = "gameserver.security.captcha.appear", defaultValue = "OD")
	public static String CAPTCHA_APPEAR;

	@Property(key = "gameserver.security.captcha.appear.rate", defaultValue = "5")
	public static int CAPTCHA_APPEAR_RATE;

	@Property(key = "gameserver.security.captcha.extraction.ban.time", defaultValue = "3000")
	public static int CAPTCHA_EXTRACTION_BAN_TIME;

	@Property(key = "gameserver.security.captcha.extraction.ban.add.time", defaultValue = "600")
	public static int CAPTCHA_EXTRACTION_BAN_ADD_TIME;

	@Property(key = "gameserver.security.captcha.bonus.fp.time", defaultValue = "5")
	public static int CAPTCHA_BONUS_FP_TIME;

	@Property(key = "gameserver.security.passkey.enable", defaultValue = "false")
	public static boolean PASSKEY_ENABLE;

	@Property(key = "gameserver.security.passkey.wrong.maxcount", defaultValue = "5")
	public static int PASSKEY_WRONG_MAXCOUNT;

	@Property(key = "gameserver.security.pingcheck.enable", defaultValue = "true")
	public static boolean SECURITY_ENABLE;

	@Property(key = "gameserver.security.pingcheck.interval", defaultValue = "80")
	public static int PING_INTERVAL;

	@Property(key = "gameserver.security.flood.delay", defaultValue = "1")
	public static int FLOOD_DELAY;

	@Property(key = "gameserver.security.flood.msg", defaultValue = "6")
	public static int FLOOD_MSG;

        @Property(key = "gameserver.security.pff.enable", defaultValue = "false")
	public static boolean PFF_ENABLE;

	@Property(key = "gameserver.security.pff.level", defaultValue = "1")
	public static int PFF_LEVEL;

	@Property(key = "gameserver.security.broker.prebuy", defaultValue = "true")
	public static boolean BROKER_PREBUY_CHECK;

        @Property(key = "gameserver.security.validation.flypath", defaultValue = "false")
	public static boolean ENABLE_FLYPATH_VALIDATOR;

        @Property(key = "gameserver.security.survey.delay.minute", defaultValue = "20")
	public static int SURVEY_DELAY;

        @Property(key = "gameserver.security.instance.keycheck", defaultValue = "false")
	public static boolean INSTANCE_KEYCHECK;

	@Property(key = "gameserver.security.delete.minute", defaultValue = "5")
	public static int DELETE_MINUTE;
}
