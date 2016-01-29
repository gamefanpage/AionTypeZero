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

package org.typezero.gameserver.network.chatserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.NioServer;
import org.typezero.gameserver.configs.network.NetworkConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.chatserver.serverpackets.SM_CS_PLAYER_AUTH;
import org.typezero.gameserver.network.chatserver.serverpackets.SM_CS_PLAYER_GAG;
import org.typezero.gameserver.network.chatserver.serverpackets.SM_CS_PLAYER_LOGOUT;
import org.typezero.gameserver.network.factories.CsPacketHandlerFactory;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class ChatServer {

	private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

	private ChatServerConnection chatServer;
	private NioServer nioServer;

	private boolean serverShutdown = false;

	public static final ChatServer getInstance() {
		return SingletonHolder.instance;
	}

	private ChatServer() {
	}

	public void setNioServer(NioServer nioServer) {
		this.nioServer = nioServer;
	}

	/**
	 * @return
	 */
	public ChatServerConnection connect() {
		SocketChannel sc;
		for (;;) {
			chatServer = null;
			log.info("Connecting to ChatServer: " + NetworkConfig.CHAT_ADDRESS);
			try {
				sc = SocketChannel.open(NetworkConfig.CHAT_ADDRESS);
				sc.configureBlocking(false);
				Dispatcher d = nioServer.getReadWriteDispatcher();
				CsPacketHandlerFactory csPacketHandlerFactory = new CsPacketHandlerFactory();
				chatServer = new ChatServerConnection(sc, d, csPacketHandlerFactory.getPacketHandler());

				// register
				d.register(sc, SelectionKey.OP_READ, chatServer);

				// initialized
				chatServer.initialized();

				return chatServer;
			}
			catch (Exception e) {
				log.info("Cant connect to ChatServer: " + e.getMessage());
			}
			try {
				/**
				 * 10s sleep
				 */
				Thread.sleep(10 * 1000);
			}
			catch (Exception e) {
			}
		}
	}

	/**
	 * This method is called when we lost connection to ChatServer.
	 */
	public void chatServerDown() {
		log.warn("Connection with ChatServer lost...");

		chatServer = null;

		if (!serverShutdown) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					connect();
				}
			}, 5000);
		}
	}

	/**
	 * @param player
	 * @param token
	 */
	public void sendPlayerLoginRequst(Player player) {
		if (chatServer != null)
			chatServer.sendPacket(new SM_CS_PLAYER_AUTH(player.getObjectId(), player.getAcountName(), player.getName()));
	}

	/**
	 * @param player
	 */
	public void sendPlayerLogout(Player player) {
		if (chatServer != null)
			chatServer.sendPacket(new SM_CS_PLAYER_LOGOUT(player.getObjectId()));
	}

	public void sendPlayerGagPacket(int playerObjId, long gagTime) {
		if (chatServer != null)
			chatServer.sendPacket(new SM_CS_PLAYER_GAG(playerObjId, gagTime));
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ChatServer instance = new ChatServer();
	}
}
