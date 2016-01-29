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
import org.typezero.chatserver.model.ChatClient;
import org.typezero.chatserver.network.gameserver.GsClientPacket;
import org.typezero.chatserver.network.gameserver.GsConnection;
import org.typezero.chatserver.network.gameserver.serverpackets.SM_PLAYER_AUTH_RESPONSE;
import org.typezero.chatserver.service.ChatService;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * @author ATracer
 */
public class CM_PLAYER_AUTH extends GsClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_PLAYER_AUTH.class);
	private int playerId;
	private String playerLogin;
	private String nick;

	public CM_PLAYER_AUTH(ByteBuffer buf, GsConnection connection) {
		super(buf, connection, 0x01);
	}

	@Override
	protected void readImpl() {
		playerId = readD();
		playerLogin = readS();
		nick = readS();
	}

	@Override
	protected void runImpl() {
		ChatClient chatClient = null;
		try {
			chatClient = ChatService.getInstance().registerPlayer(playerId, playerLogin, nick);
		} catch (NoSuchAlgorithmException e) {
			log.error("Error registering player on ChatServer: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.error("Error registering player on ChatServer: " + e.getMessage());
		}

		if (chatClient != null) {
			getConnection().sendPacket(new SM_PLAYER_AUTH_RESPONSE(chatClient));
		} else {
			log.info("Player was not authed " + playerId);
		}
	}
}
