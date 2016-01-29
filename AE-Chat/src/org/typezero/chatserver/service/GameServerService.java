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
package org.typezero.chatserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.chatserver.configs.Config;
import org.typezero.chatserver.network.gameserver.GsAuthResponse;

/**
 * @author ATracer, KID
 */
public class GameServerService {

	public static byte GAMESERVER_ID;
	private static GameServerService instance = new GameServerService();
	private Logger log = LoggerFactory.getLogger(GameServerService.class);
	private boolean isOnline = false;

	public static GameServerService getInstance() {
		return instance;
	}

	/**
	 * @param gameChannelHandler
	 * @param gameServerId
	 * @param defaultAddress
	 * @param password
	 * @return
	 */
	public GsAuthResponse registerGameServer(byte gameServerId, byte[] defaultAddress, String password) {
		GAMESERVER_ID = gameServerId;
		if (isOnline) {
			return GsAuthResponse.ALREADY_REGISTERED;
		}

		return passwordConfigAuth(password);
	}

	/**
	 * @return
	 */
	private GsAuthResponse passwordConfigAuth(String password) {
		if (password.equals(Config.GAME_SERVER_PASSWORD)) {
			isOnline = true;
			return GsAuthResponse.AUTHED;
		}

		log.warn("Gameserver #" + GAMESERVER_ID + " has invalid password.");
		return GsAuthResponse.NOT_AUTHED;
	}

	public void setOffline() {
		log.info("Gameserver #" + GAMESERVER_ID + " is disconnected");
		isOnline = false;
	}
}
