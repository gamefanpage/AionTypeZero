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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import org.typezero.gameserver.network.chatserver.serverpackets.SM_CS_AUTH;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class ChatServerConnection extends AConnection {

	private static final Logger log = LoggerFactory.getLogger(ChatServerConnection.class);

	/**
	 * Possible states of CsConnection
	 */
	public static enum State {
		/**
		 * chat server just connected
		 */
		CONNECTED,
		/**
		 * chat server is authenticated
		 */
		AUTHED
	}

	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<CsServerPacket> sendMsgQueue = new ArrayDeque<CsServerPacket>();

	/**
	 * Current state of this connection
	 */
	private State state;
	private ChatServer chatServer;
	private CsPacketHandler csPacketHandler;

	/**
	 * @param sc
	 * @param d
	 * @throws IOException
	 */

	public ChatServerConnection(SocketChannel sc, Dispatcher d, CsPacketHandler csPacketHandler) throws IOException {
		super(sc, d, 8192*2, 8192*2);
		this.chatServer = ChatServer.getInstance();
		this.csPacketHandler = csPacketHandler;

		state = State.CONNECTED;
		log.info("Connected to ChatServer!");
	}

	@Override
	protected void initialized() {
		this.sendPacket(new SM_CS_AUTH());
	}


	@Override
	public boolean processData(ByteBuffer data) {
		CsClientPacket pck = csPacketHandler.handle(data, this);

		/**
		 * Execute packet only if packet exist (!= null) and read was ok.
		 */
		if (pck != null && pck.read())
			ThreadPoolManager.getInstance().executeLsPacket(pck);

		return true;
	}

	@Override
	protected final boolean writeData(ByteBuffer data) {
		synchronized (guard) {
			CsServerPacket packet = sendMsgQueue.pollFirst();
			if (packet == null)
				return false;

			packet.write(this, data);
			return true;
		}
	}

	@Override
	protected final long getDisconnectionDelay() {
		return 0;
	}

	@Override
	protected final void onDisconnect() {
		chatServer.chatServerDown();
	}

	@Override
	protected final void onServerClose() {
		// TODO send close packet to chat server
		close(/* packet, */true);
	}

	/**
	 * @param bp
	 */
	public final void sendPacket(CsServerPacket bp) {
		synchronized (guard) {
			/**
			 * Connection is already closed or waiting for last (close packet) to be sent
			 */
			if (isWriteDisabled())
				return;

			sendMsgQueue.addLast(bp);
			enableWriteInterest();
		}
	}

	/**
	 * @param closePacket
	 * @param forced
	 */
	public final void close(CsServerPacket closePacket, boolean forced) {
		synchronized (guard) {
			if (isWriteDisabled())
				return;

			log.info("sending packet: " + closePacket + " and closing connection after that.");

			pendingClose = true;
			isForcedClosing = forced;
			sendMsgQueue.clear();
			sendMsgQueue.addLast(closePacket);
			enableWriteInterest();
		}
	}

	/**
	 * @return
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ChatServer " + getIP();
	}
}
