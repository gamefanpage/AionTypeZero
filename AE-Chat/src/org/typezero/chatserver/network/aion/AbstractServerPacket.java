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
import org.typezero.chatserver.common.netty.BaseServerPacket;
import org.typezero.chatserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractServerPacket extends BaseServerPacket {

	/**
	 * @param opCode
	 */
	public AbstractServerPacket(int opCode) {
		super(opCode);
	}

	/**
	 * @param clientChannelHandler
	 * @param buf
	 */
	public void write(ClientChannelHandler clientChannelHandler, ChannelBuffer buf) {
		buf.writeShort((short) 0);
		writeImpl(clientChannelHandler, buf);
	}

	/**
	 * @param cHandler
	 * @param buf
	 */
	protected abstract void writeImpl(ClientChannelHandler cHandler, ChannelBuffer buf);
}
