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
package org.typezero.chatserver.network.gameserver.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.chatserver.network.gameserver.GsClientPacket;
import org.typezero.chatserver.network.gameserver.GsConnection;
import org.typezero.chatserver.service.ChatService;

import java.nio.ByteBuffer;

/**
 * @author ViAl
 *
 */
public class CM_PLAYER_GAG extends GsClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_PLAYER_LOGOUT.class);
	private int playerId;
	private long gagTime;

	public CM_PLAYER_GAG(ByteBuffer buf, GsConnection connection) {
		super(buf, connection, 0x03);
	}

	@Override
	protected void readImpl() {
		playerId = readD();
		gagTime = readQ();
	}

	@Override
	protected void runImpl() {
		ChatService.getInstance().gagPlayer(playerId, gagTime);
		log.info("Player was gagged " + playerId + " for " + (gagTime / 1000 / 60) + " minutes");
	}
}
