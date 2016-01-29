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


package org.typezero.gameserver.configs.network;

import java.net.InetSocketAddress;

import com.aionemu.commons.configuration.Property;

public class NetworkConfig {

	/**
	 * Game Server port
	 */
	@Property(key = "gameserver.network.client.port", defaultValue = "7777")
	public static int GAME_PORT;

	/**
	 * Game Server bind ip
	 */
	@Property(key = "gameserver.network.client.host", defaultValue = "*")
	public static String GAME_BIND_ADDRESS;

	/**
	 * Max allowed online players
	 */
	@Property(key = "gameserver.network.client.maxplayers", defaultValue = "100")
	public static int MAX_ONLINE_PLAYERS;

	/**
	 * LoginServer address
	 */
	@Property(key = "gameserver.network.login.address", defaultValue = "localhost:9014")
	public static InetSocketAddress LOGIN_ADDRESS;

	/**
	 * ChatServer address
	 */
	@Property(key = "gameserver.network.chat.address", defaultValue = "localhost:9021")
	public static InetSocketAddress CHAT_ADDRESS;

	/**
	 * Password for this GameServer ID for authentication at ChatServer.
	 */
	@Property(key = "gameserver.network.chat.password", defaultValue = "")
	public static String CHAT_PASSWORD;

	/**
	 * GameServer id that this GameServer will request at LoginServer.
	 */
	@Property(key = "gameserver.network.login.gsid", defaultValue = "0")
	public static int GAMESERVER_ID;

	/**
	 * Password for this GameServer ID for authentication at LoginServer.
	 */
	@Property(key = "gameserver.network.login.password", defaultValue = "")
	public static String LOGIN_PASSWORD;

	/**
	 * Number of Threads dedicated to be doing io read & write.
	 * There is always 1 acceptor thread.
	 * If value is < 1 - acceptor thread will also handle read & write.
	 * If value is > 0 - there will be given amount of read & write threads
	 * + 1 acceptor thread.
	 */
	@Property(key = "gameserver.network.nio.threads", defaultValue = "1")
	public static int NIO_READ_WRITE_THREADS;

	/**
	 * Number of minimum threads that will be used to execute aion client packets.
	 */
	@Property(key = "gameserver.network.packet.processor.threads.min", defaultValue = "4")
	public static int PACKET_PROCESSOR_MIN_THREADS;

	/**
	 * Number of maximum threads that will be used to execute aion client packets.
	 */
	@Property(key = "gameserver.network.packet.processor.threads.max", defaultValue = "4")
	public static int PACKET_PROCESSOR_MAX_THREADS;

	/**
	 * Threshold that will be used to decide when extra threads are not needed.
	 * (it doesn't have any effect if min threads == max threads)
	 */
	@Property(key = "gameserver.network.packet.processor.threshold.kill", defaultValue = "3")
	public static int PACKET_PROCESSOR_THREAD_KILL_THRESHOLD;

	/**
	 * Threshold that will be used to decide when extra threads should be spawned.
	 * (it doesn't have any effect if min threads == max threads)
	 */
	@Property(key = "gameserver.network.packet.processor.threshold.spawn", defaultValue = "50")
	public static int PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD;

	/**
	 * If unknown packets should be logged.
	 */
	@Property(key = "gameserver.network.display.unknownpackets", defaultValue = "false")
	public static boolean DISPLAY_UNKNOWNPACKETS;

	@Property(key = "gameserver.network.flood.connections", defaultValue = "false")
	public static boolean ENABLE_FLOOD_CONNECTIONS;

	@Property(key = "gameserver.network.flood.tick", defaultValue = "1000")
	public static int Flood_Tick;

	@Property(key = "gameserver.network.flood.short.warn", defaultValue = "10")
	public static int Flood_SWARN;

	@Property(key = "gameserver.network.flood.short.reject", defaultValue = "20")
	public static int Flood_SReject;

	@Property(key = "gameserver.network.flood.short.tick", defaultValue = "10")
	public static int Flood_STick;

	@Property(key = "gameserver.network.flood.long.warn", defaultValue = "30")
	public static int Flood_LWARN;

	@Property(key = "gameserver.network.flood.long.reject", defaultValue = "60")
	public static int Flood_LReject;

	@Property(key = "gameserver.network.flood.long.tick", defaultValue = "60")
	public static int Flood_LTick;
}
