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

package com.aionemu.commons.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * This class represents an <code>Acceptor</code> that will accept sockets<br>
 * connections dispatched by Accept <code>Dispatcher</code>. <code>Acceptor</code> is attachment<br>
 * of <code>ServerSocketChannel</code> <code>SelectionKey</code> registered on Accept <code>Dispatcher</code>
 * <code>Selector</code>.<br>
 * <code>Acceptor</code> will create new <code>AConnection</code> object using
 * <code>ConnectionFactory.create(SocketChannel socket)</code><br>
 * representing accepted socket, register it into one of ReadWrite <code>Dispatcher</code><br>
 * <code>Selector</code> as ready for io read operations.<br>
 *
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.Dispatcher
 * @see java.nio.channels.ServerSocketChannel
 * @see java.nio.channels.SelectionKey
 * @see java.nio.channels.SocketChannel
 * @see java.nio.channels.Selector
 * @see com.aionemu.commons.network.AConnection
 * @see com.aionemu.commons.network.ConnectionFactory
 * @see com.aionemu.commons.network.NioServer
 */
public class Acceptor {

	/**
	 * <code>ConnectionFactory</code> that will create new <code>AConnection</code>
	 *
	 * @see com.aionemu.commons.network.ConnectionFactory
	 * @see com.aionemu.commons.network.AConnection
	 */
	private final ConnectionFactory factory;

	/**
	 * <code>NioServer</code> that created this Acceptor.
	 *
	 * @see com.aionemu.commons.network.NioServer
	 */
	private final NioServer nioServer;

	/**
	 * Constructor that accept <code>ConnectionFactory</code> and <code>NioServer</code> as parameter<br>
	 *
	 * @param factory   <code>ConnectionFactory</code> that will be used to<br>
	 * @param nioServer <code>NioServer</code> that created this Acceptor object<br>
	 *                  creating new <code>AConnection</code> instances.
	 * @see com.aionemu.commons.network.ConnectionFactory
	 * @see com.aionemu.commons.network.NioServer
	 * @see com.aionemu.commons.network.AConnection
	 */
	Acceptor(ConnectionFactory factory, NioServer nioServer) {
		this.factory = factory;
		this.nioServer = nioServer;
	}

	/**
	 * Method called by Accept <code>Dispatcher</code> <code>Selector</code> when socket<br>
	 * connects to <code>ServerSocketChannel</code> listening for connections.<br>
	 * New instance of <code>AConnection</code> will be created by <code>ConnectionFactory</code>,<br>
	 * socket representing accepted connection will be register into<br>
	 * one of ReadWrite <code>Dispatchers</code> <code>Selector as ready for io read operations.<br>
	 *
	 * @param key <code>SelectionKey</code> representing <code>ServerSocketChannel</code> that is accepting<br>
	 *            new socket connection.
	 * @throws IOException
	 * @see com.aionemu.commons.network.Dispatcher
	 * @see java.nio.channels.ServerSocketChannel
	 * @see java.nio.channels.SelectionKey
	 * @see java.nio.channels.SocketChannel
	 * @see java.nio.channels.Selector
	 * @see com.aionemu.commons.network.AConnection
	 * @see com.aionemu.commons.network.ConnectionFactory
	 */
	public final void accept(SelectionKey key) throws IOException {
		/** For an accept to be pending the channel must be a server socket channel. */
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		/** Accept the connection and make it non-blocking */
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		Dispatcher dispatcher = nioServer.getReadWriteDispatcher();
		AConnection con = factory.create(socketChannel, dispatcher);

		if (con == null) {
			return;
		}

		// register
		dispatcher.register(socketChannel, SelectionKey.OP_READ, con);
		// notify initialized :)
		con.initialized();
	}
}
