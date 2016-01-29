/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p/>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * Aion-Lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. *
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * <p/>
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning
 *
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package org.typezero.chatserver.network.netty.handler;

import com.google.inject.Inject;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.chatserver.model.ChatClient;
import org.typezero.chatserver.network.aion.AbstractClientPacket;
import org.typezero.chatserver.network.aion.AbstractServerPacket;
import org.typezero.chatserver.network.aion.ClientPacketHandler;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

/**
 * @author ATracer
 */
public class ClientChannelHandler extends AbstractChannelHandler {

	private static final Logger log = LoggerFactory.getLogger(ClientChannelHandler.class);
	private final ClientPacketHandler clientPacketHandler;
	private State state;
	private ChatClient chatClient;

	@Inject
	public ClientChannelHandler(ClientPacketHandler clientPacketHandler) {
		this.clientPacketHandler = clientPacketHandler;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelConnected(ctx, e);

		state = State.CONNECTED;
		inetAddress = ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress();
		channel = ctx.getChannel();
		log.info("Channel connected Ip:" + inetAddress.getHostAddress());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		super.messageReceived(ctx, e);

		AbstractClientPacket clientPacket = clientPacketHandler.handle((ChannelBuffer) e.getMessage(), this);
		if (clientPacket != null && clientPacket.read()) {
			clientPacket.run();
		}
		if (clientPacket != null) {
			if (log.isDebugEnabled()) {
				log.debug("Received packet: " + clientPacket);
			}
		}
	}

	/**
	 * @param packet
	 */
	public void sendPacket(AbstractServerPacket packet) {
		ChannelBuffer cb = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, 2 * 8192);
		packet.write(this, cb);
		channel.write(cb);
		if (log.isDebugEnabled()) {
			log.debug("Sent packet: " + packet);
		}
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the chatClient
	 */
	public ChatClient getChatClient() {
		return chatClient;
	}

	/**
	 * @param chatClient the chatClient to set
	 */
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	/**
	 * Possible states of channel handler
	 */
	public static enum State {

		/**
		 * client just connected
		 */
		CONNECTED,
		/**
		 * client is authenticated
		 */
		AUTHED,
	}
}
