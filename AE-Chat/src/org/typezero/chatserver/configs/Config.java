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
 */

package org.typezero.chatserver.configs;

import com.aionemu.commons.configs.CommonsConfig;
import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author ATracer
 */
public class Config {

	protected static final Logger log = LoggerFactory.getLogger(Config.class);
	/**
	 * Chat Server address
	 */
	@Property(key = "chatserver.network.client.address", defaultValue = "localhost:10241")
	public static InetSocketAddress CHAT_ADDRESS;
	/**
	 * Game Server address
	 */
	@Property(key = "chatserver.network.gameserver.address", defaultValue = "localhost:9021")
	public static InetSocketAddress GAME_ADDRESS;
	/**
	 * Password for GS authentication
	 */
	@Property(key = "chatserver.network.gameserver.password", defaultValue = "*")
	public static String GAME_SERVER_PASSWORD;
	/**
	 * Log requests to new channels
	 */
	@Property(key = "chatserver.log.channel.request", defaultValue = "false")
	public static boolean LOG_CHANNEL_REQUEST;
	/**
	 * Log requests to invalid channels
	 */
	@Property(key = "chatserver.log.channel.invalid", defaultValue = "false")
	public static boolean LOG_CHANNEL_INVALID;
	/**
	 * Log Chat
	 */
	@Property(key = "chatserver.log.chat", defaultValue = "false")
	public static boolean LOG_CHAT;
	/**
	 * Lang Chat
	 */
	@Property(key = "chatserver.chat.lang", defaultValue = "1")
	public static int LANG_CHAT;
	@Property(key = "chatserver.chat.message.delay", defaultValue = "30")
	public static int MESSAGE_DELAY;
	/**
	 * Specifies the frequency the chat server will be restarted
	 */
	@Property(key = "chatserver.restart.frequency", defaultValue = "NEVER")
	public static String CHATSERVER_RESTART_FREQUENCY;
	/**
	 * Specifies the exact time of day the server should be restarted (of course
	 * respecting the frequency)
	 */
	@Property(key = "chatserver.restart.time", defaultValue = "5:00")
	public static String CHATSERVER_RESTART_TIME;

	/**
	 * Load configs from files.
	 */
	public static void load() {
		try {

			Properties myProps = null;
			try {
				log.info("Loading: mycs.properties");
				myProps = PropertiesUtils.load("./config/mycs.properties");
			} catch (Exception e) {
				log.info("No override properties found");
			}

			Properties[] props = PropertiesUtils.loadAllFromDirectory("./config");
			PropertiesUtils.overrideProperties(props, myProps);

			log.info("Loading: commons.properties");
			ConfigurableProcessor.process(CommonsConfig.class, props);
			log.info("Loading: chatserver.properties");
			ConfigurableProcessor.process(Config.class, props);
		} catch (Exception e) {
			log.error("Can't load chatserver configuration", e);
			throw new Error("Can't load chatserver configuration", e);
		}
	}
}
