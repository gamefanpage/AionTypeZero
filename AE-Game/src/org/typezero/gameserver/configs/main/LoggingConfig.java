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

public class LoggingConfig {

	@Property(key = "gameserver.enable.advanced.logging", defaultValue = "false")
	public static boolean ENABLE_ADVANCED_LOGGING;

	/**
	 * Logging
	 */
	@Property(key = "gameserver.log.audit", defaultValue = "true")
	public static boolean LOG_AUDIT;

	@Property(key = "gameserver.log.autogroup", defaultValue = "true")
	public static boolean LOG_AUTOGROUP;

	@Property(key = "gameserver.log.chat", defaultValue = "true")
	public static boolean LOG_CHAT;

	@Property(key = "gameserver.log.craft", defaultValue = "true")
	public static boolean LOG_CRAFT;

	@Property(key = "gameserver.log.faction", defaultValue = "false")
	public static boolean LOG_FACTION;

	@Property(key = "gameserver.log.gmaudit", defaultValue = "true")
	public static boolean LOG_GMAUDIT;

	@Property(key = "gameserver.log.ingameshop", defaultValue = "false")
	public static boolean LOG_INGAMESHOP;

	@Property(key = "gameserver.log.ingameshop.sql", defaultValue = "false")
	public static boolean LOG_INGAMESHOP_SQL;

	@Property(key = "gameserver.log.item", defaultValue = "true")
	public static boolean LOG_ITEM;

	@Property(key = "gameserver.log.kill", defaultValue = "false")
	public static boolean LOG_KILL;

	@Property(key = "gameserver.log.pl", defaultValue = "false")
	public static boolean LOG_PL;

	@Property(key = "gameserver.log.mail", defaultValue = "false")
	public static boolean LOG_MAIL;

	@Property(key = "gameserver.log.player.exchange", defaultValue = "false")
	public static boolean LOG_PLAYER_EXCHANGE;

	@Property(key = "gameserver.log.broker.exchange", defaultValue = "false")
	public static boolean LOG_BROKER_EXCHANGE;

	@Property(key = "gameserver.log.siege", defaultValue = "false")
	public static boolean LOG_SIEGE;

	@Property(key = "gameserver.log.sysmail", defaultValue = "false")
	public static boolean LOG_SYSMAIL;

	@Property(key = "gameserver.log.auction", defaultValue = "true")
	public static boolean LOG_HOUSE_AUCTION;
}
