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
package org.typezero.chatserver.network.gameserver.serverpackets;

import org.typezero.chatserver.model.ChatClient;
import org.typezero.chatserver.network.gameserver.GsConnection;
import org.typezero.chatserver.network.gameserver.GsServerPacket;

/**
 * @author ATracer
 */
public class SM_PLAYER_AUTH_RESPONSE extends GsServerPacket {

	private int playerId;
	private byte[] token;

	public SM_PLAYER_AUTH_RESPONSE(ChatClient chatClient) {
		this.playerId = chatClient.getClientId();
		token = chatClient.getToken();
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(1);
		writeD(playerId);
		writeC(token.length);
		writeB(token);
	}
}
