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

package org.typezero.gameserver.network.aion;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.ConnectionFactory;
import com.aionemu.commons.network.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.network.NetworkConfig;
import org.typezero.gameserver.network.sequrity.FloodManager;
import org.typezero.gameserver.network.sequrity.FloodManager.Result;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * ConnectionFactory implementation that will be creating AionConnections
 *
 * @author -Nemesiss-
 */
public class GameConnectionFactoryImpl implements ConnectionFactory {

	private final Logger log = LoggerFactory.getLogger(GameConnectionFactoryImpl.class);
	private FloodManager floodAcceptor;

	/**
	 * Create a new {@link com.aionemu.commons.network.AConnection AConnection} instance.<br>
	 *
	 * @param socket
	 *          that new {@link com.aionemu.commons.network.AConnection AConnection} instance will represent.<br>
	 * @param dispatcher
	 *          to witch new connection will be registered.<br>
	 * @return a new instance of {@link com.aionemu.commons.network.AConnection AConnection}<br>
	 * @throws IOException
	 * @see com.aionemu.commons.network.AConnection
	 * @see com.aionemu.commons.network.Dispatcher
	 */

	public GameConnectionFactoryImpl()
	{
		if(NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			floodAcceptor = new FloodManager(NetworkConfig.Flood_Tick,
					new FloodManager.FloodFilter(NetworkConfig.Flood_SWARN, NetworkConfig.Flood_SReject, NetworkConfig.Flood_STick), // short period
					new FloodManager.FloodFilter(NetworkConfig.Flood_LWARN, NetworkConfig.Flood_LReject, NetworkConfig.Flood_LTick)); // long period
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.commons.network.ConnectionFactory#create(java.nio.channels.SocketChannel,
	 * com.aionemu.commons.network.Dispatcher)
	 */
	@Override
	public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
		if(NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			String host = socket.socket().getInetAddress().getHostAddress();
			final Result isFlooding = floodAcceptor.isFlooding(host, true);
			switch (isFlooding)
			{
				case REJECTED:
				{
					log.warn("Rejected connection from " + host);
					socket.close();
					return null;
				}
				case WARNED:
				{
					log.warn("Connection over warn limit from " + host);
					break;
				}
			}
		}

		return new AionConnection(socket, dispatcher);
	}
}
