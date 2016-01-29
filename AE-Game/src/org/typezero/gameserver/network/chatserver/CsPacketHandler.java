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

package org.typezero.gameserver.network.chatserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.network.chatserver.ChatServerConnection.State;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ATracer
 */
public class CsPacketHandler {

	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(CsPacketHandler.class);

	private Map<State, Map<Integer, CsClientPacket>> packetPrototypes = new HashMap<State, Map<Integer, CsClientPacket>>();

	/**
	 * Reads one packet from given ByteBuffer
	 *
	 * @param data
	 * @param client
	 * @return GsClientPacket object from binary data
	 */
	public CsClientPacket handle(ByteBuffer data, ChatServerConnection client) {
		State state = client.getState();
		int id = data.get() & 0xff;

		return getPacket(state, id, data, client);
	}

	/**
	 * @param packetPrototype
	 * @param states
	 */
	public void addPacketPrototype(CsClientPacket packetPrototype, State... states) {
		for (State state : states) {
			Map<Integer, CsClientPacket> pm = packetPrototypes.get(state);
			if (pm == null) {
				pm = new HashMap<Integer, CsClientPacket>();
				packetPrototypes.put(state, pm);
			}
			pm.put(packetPrototype.getOpcode(), packetPrototype);
		}
	}

	/**
	 * @param state
	 * @param id
	 * @param buf
	 * @param con
	 * @return
	 */
	private CsClientPacket getPacket(State state, int id, ByteBuffer buf, ChatServerConnection con) {
		CsClientPacket prototype = null;

		Map<Integer, CsClientPacket> pm = packetPrototypes.get(state);
		if (pm != null) {
			prototype = pm.get(id);
		}

		if (prototype == null) {
			unknownPacket(state, id);
			return null;
		}

		CsClientPacket res = prototype.clonePacket();
		res.setBuffer(buf);
		res.setConnection(con);

		return res;
	}

	/**
	 * Logs unknown packet.
	 *
	 * @param state
	 * @param id
	 */
	private void unknownPacket(State state, int id) {
		log.warn(String.format("Unknown packet recived from Chat Server: 0x%02X state=%s", id, state.toString()));
	}
}
