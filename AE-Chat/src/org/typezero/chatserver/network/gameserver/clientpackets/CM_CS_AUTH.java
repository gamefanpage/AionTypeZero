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
import org.typezero.chatserver.network.gameserver.GsAuthResponse;
import org.typezero.chatserver.network.gameserver.GsClientPacket;
import org.typezero.chatserver.network.gameserver.GsConnection;
import org.typezero.chatserver.network.gameserver.serverpackets.SM_GS_AUTH_RESPONSE;
import org.typezero.chatserver.service.GameServerService;

import java.nio.ByteBuffer;

/**
 * @author ATracer
 */
public class CM_CS_AUTH extends GsClientPacket {

	private Logger log = LoggerFactory.getLogger(CM_CS_AUTH.class);
	/**
	 * Password for authentication
	 */
	private String password;
	/**
	 * Id of GameServer
	 */
	private byte gameServerId;
	/**
	 * Default address for server
	 */
	private byte[] defaultAddress;

	public CM_CS_AUTH(ByteBuffer buf, GsConnection connection) {
		super(buf, connection, 0x00);
	}

	@Override
	protected void readImpl() {
		gameServerId = (byte) readC();
		defaultAddress = readB(readC());
		password = readS();
	}

	@Override
	protected void runImpl() {
		GsAuthResponse resp = GameServerService.getInstance().registerGameServer(gameServerId, defaultAddress, password);

		switch (resp) {
			case AUTHED:
				getConnection().setState(GsConnection.State.AUTHED);
				getConnection().sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				log.info("Gameserver #" + gameServerId + " is now online.");
				break;
			case NOT_AUTHED:
				getConnection().sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;
			case ALREADY_REGISTERED:
				log.info("Gameserver #" + gameServerId + " is already registered!");
				getConnection().sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;
			//	default:
			//	getConnection().close(new SM_GS_AUTH_RESPONSE(resp), false);
		}
	}
}
