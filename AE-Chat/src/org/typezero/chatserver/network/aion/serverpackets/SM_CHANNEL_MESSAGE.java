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
package org.typezero.chatserver.network.aion.serverpackets;

import org.jboss.netty.buffer.ChannelBuffer;
import org.typezero.chatserver.model.message.Message;
import org.typezero.chatserver.network.aion.AbstractServerPacket;
import org.typezero.chatserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public class SM_CHANNEL_MESSAGE extends AbstractServerPacket {

	private Message message;

	public SM_CHANNEL_MESSAGE(Message message) {
		super(0x1A);
		this.message = message;
	}

	@Override
	protected void writeImpl(ClientChannelHandler cHandler, ChannelBuffer buf) {
		writeC(buf, getOpCode());
		writeC(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, message.getChannel().getChannelId());
		writeD(buf, message.getSender().getClientId());
		writeD(buf, 0x00);
		writeC(buf, 0x00);
		writeH(buf, message.getSender().getIdentifier().length / 2);
		writeB(buf, message.getSender().getIdentifier());
		writeH(buf, message.size() / 2);
		writeB(buf, message.getText());
	}
}
