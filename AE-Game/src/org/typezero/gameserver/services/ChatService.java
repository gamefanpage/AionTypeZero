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

package org.typezero.gameserver.services;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_CHAT_INIT;
import org.typezero.gameserver.network.chatserver.ChatServer;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/** 
 * @author ATracer
 */
public class ChatService {
	
	private static byte[] ip = { 127, 0, 0, 1 };
	private static int port = 10241;

	/**
	 * Disonnect from chat server
	 * 
	 * @param player
	 */
	public static void onPlayerLogout(Player player) {
		ChatServer.getInstance().sendPlayerLogout(player);
	}

	/**
	 * @param playerId
	 * @param token
	 * @param account 
	 * @param nick 
	 */
	public static void playerAuthed(int playerId, byte[] token) {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_CHAT_INIT(token));
		}
	}

	/**
	 * @return the ip
	 */
	public static byte[] getIp() {
		return ip;
	}

	/**
	 * @return the port
	 */
	public static int getPort() {
		return port;
	}

	/**
	 * @param ip
	 *          the ip to set
	 */
	public static void setIp(byte[] _ip) {
		ip = _ip;
	}

	/**
	 * @param port
	 *          the port to set
	 */
	public static void setPort(int _port) {
		port = _port;
	}
}
