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

package org.typezero.gameserver.network.factories;

import org.typezero.gameserver.network.loginserver.LoginServerConnection.State;
import org.typezero.gameserver.network.loginserver.LsClientPacket;
import org.typezero.gameserver.network.loginserver.LsPacketHandler;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_ACOUNT_AUTH_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_BAN_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_GS_AUTH_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_GS_CHARACTER_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_LS_CONTROL_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_LS_PING;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_MACBAN_LIST;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_PREMIUM_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_PTRANSFER_RESPONSE;
import org.typezero.gameserver.network.loginserver.clientpackets.CM_REQUEST_KICK_ACCOUNT;

/**
 * @author Luno
 */
public class LsPacketHandlerFactory {

	private LsPacketHandler handler = new LsPacketHandler();

	public static final LsPacketHandlerFactory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param loginServer
	 */
	private LsPacketHandlerFactory() {
		addPacket(new CM_ACCOUNT_RECONNECT_KEY(0x03), State.AUTHED);
		addPacket(new CM_ACOUNT_AUTH_RESPONSE(0x01), State.AUTHED);
		addPacket(new CM_GS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_REQUEST_KICK_ACCOUNT(0x02), State.AUTHED);
		addPacket(new CM_LS_CONTROL_RESPONSE(0x04), State.AUTHED);
		addPacket(new CM_BAN_RESPONSE(0x05), State.AUTHED);
		addPacket(new CM_GS_CHARACTER_RESPONSE(0x08), State.AUTHED);
		addPacket(new CM_MACBAN_LIST(9), State.AUTHED);
		addPacket(new CM_PREMIUM_RESPONSE(10), State.AUTHED);
		addPacket(new CM_LS_PING(11), State.AUTHED);
		addPacket(new CM_PTRANSFER_RESPONSE(12), State.AUTHED);
	}

	private void addPacket(LsClientPacket prototype, State... states) {
		handler.addPacketPrototype(prototype, states);
	}

	public LsPacketHandler getPacketHandler() {
		return handler;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final LsPacketHandlerFactory instance = new LsPacketHandlerFactory();
	}
}
