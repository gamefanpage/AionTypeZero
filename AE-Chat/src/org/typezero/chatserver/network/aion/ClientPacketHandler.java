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
package org.typezero.chatserver.network.aion;

import org.jboss.netty.buffer.ChannelBuffer;
import org.typezero.chatserver.common.netty.AbstractPacketHandler;
import org.typezero.chatserver.network.aion.clientpackets.CM_CHANNEL_MESSAGE;
import org.typezero.chatserver.network.aion.clientpackets.CM_CHANNEL_REQUEST;
import org.typezero.chatserver.network.aion.clientpackets.CM_CHAT_INI;
import org.typezero.chatserver.network.aion.clientpackets.CM_PLAYER_AUTH;
import org.typezero.chatserver.network.netty.handler.ClientChannelHandler;
import org.typezero.chatserver.network.netty.handler.ClientChannelHandler.State;
import org.typezero.chatserver.service.BroadcastService;
import org.typezero.chatserver.service.ChatService;

/**
 * @author ATracer
 */
public class ClientPacketHandler extends AbstractPacketHandler {

	private BroadcastService broadcastService = BroadcastService.getInstance();
	private ChatService chatService = ChatService.getInstance();

	/**
	 * Reads one packet from ChannelBuffer
	 *
	 * @param buf
	 * @param channelHandler
	 * @return AbstractClientPacket
	 */
	public AbstractClientPacket handle(ChannelBuffer buf, ClientChannelHandler channelHandler) {
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractClientPacket clientPacket = null;

		switch (state) {
			case CONNECTED:
				switch (opCode) {
					case 0x30:
						clientPacket = new CM_CHAT_INI(buf, channelHandler, chatService);
						break;
					case 0x05:
						clientPacket = new CM_PLAYER_AUTH(buf, channelHandler, chatService);
						break;
					default:
						// unknownPacket(opCode, state.toString());
				}
				break;
			case AUTHED:
				switch (opCode) {
					case 0x10:
						clientPacket = new CM_CHANNEL_REQUEST(buf, channelHandler, chatService);
						break;
					case 0x18:
						clientPacket = new CM_CHANNEL_MESSAGE(buf, channelHandler, broadcastService);
					default:
						// unknownPacket(opCode, state.toString());
				}
				break;
		}

		return clientPacket;
	}
}
