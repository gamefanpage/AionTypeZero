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

package org.typezero.gameserver.network.aion;

import com.aionemu.commons.network.packet.BaseClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.network.aion.AionConnection.State;

import java.util.EnumSet;
import java.util.Set;

/**
 * Base class for every Aion -> LS Client Packet
 *
 * @author -Nemesiss-
 */
public abstract class AionClientPacket extends BaseClientPacket<AionConnection> implements Cloneable {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AionClientPacket.class);

	private final Set<State> validStates;

	/**
	 * Constructs new client packet instance. ByBuffer and ClientConnection should be later set manually, after using this
	 * constructor.
	 *
	 * @param opcode
	 *          packet id
	 * @param state
	 *          connection valid state
	 * @param restStates
	 *          rest of connection valid state (optional - if there are more than one)
	 */
	protected AionClientPacket(int opcode, State state, State... restStates) {
		super(opcode);
		validStates = EnumSet.of(state, restStates);
	}

	/**
	 * run runImpl catching and logging Throwable.
	 */
	@Override
	public final void run() {

		try {
			// run only if packet is still valid (connection state didn't changed)
			if(isValid())
				runImpl();
		}
		catch (Throwable e) {
			String name = getConnection().getAccount().getName();
			if (name == null)
				name = getConnection().getIP();

			log.error("Error handling client (" + name + ") message :" + this, e);
		}
	}

	/**
	 * Send new AionServerPacket to connection that is owner of this packet. This method is equvalent to:
	 * getConnection().sendPacket(msg);
	 *
	 * @param msg
	 */
	protected void sendPacket(AionServerPacket msg) {
		getConnection().sendPacket(msg);
	}

	/**
	 * Clones this packet object.
	 *
	 * @return AionClientPacket
	 */
	public AionClientPacket clonePacket() {
		try {
			return (AionClientPacket) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	protected final String readS(int size) {
		String string = readS();
		if (string != null)
			readB(size - (string.length() * 2 + 2));
		else
			readB(size);
		return string;
	}

	/**
	 * Check if packet is still valid for its connection.
	 *
	 * @return true if packet is still valid and should be processed.
	 */
	public final boolean isValid()
	{
		State state = getConnection().getState();
		boolean valid = validStates.contains(state);

		if(!valid)
			log.info(this + " wont be processed cuz its valid state don't match current connection state: " + state);
		return valid;
	}
}
