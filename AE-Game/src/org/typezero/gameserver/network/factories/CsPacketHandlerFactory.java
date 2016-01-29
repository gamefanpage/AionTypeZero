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

import org.typezero.gameserver.network.chatserver.CsClientPacket;
import org.typezero.gameserver.network.chatserver.CsPacketHandler;
import org.typezero.gameserver.network.chatserver.ChatServerConnection.State;
import org.typezero.gameserver.network.chatserver.clientpackets.CM_CS_AUTH_RESPONSE;
import org.typezero.gameserver.network.chatserver.clientpackets.CM_CS_PLAYER_AUTH_RESPONSE;

/**
 * @author ATracer
 */
public class CsPacketHandlerFactory {

	private CsPacketHandler handler = new CsPacketHandler();

	/**
	 * @param injector
	 */
	public CsPacketHandlerFactory() {
		addPacket(new CM_CS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_CS_PLAYER_AUTH_RESPONSE(0x01), State.AUTHED);
	}

	/**
	 * @param prototype
	 * @param states
	 */
	private void addPacket(CsClientPacket prototype, State... states) {
		handler.addPacketPrototype(prototype, states);
	}

	/**
	 * @return handler
	 */
	public CsPacketHandler getPacketHandler() {
		return handler;
	}
}
