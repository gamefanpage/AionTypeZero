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

package org.typezero.gameserver.network.aion.clientpackets;


import org.slf4j.LoggerFactory;

import org.typezero.gameserver.network.BannedMacManager;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;


/**
 * In this packet client is sending Mac Address - haha.
 *
 * @author -Nemesiss-, KID
 */
public class CM_MAC_ADDRESS extends AionClientPacket {
	/**
	 * Mac Addres send by client in the same format as: ipconfig /all [ie:
	 * xx-xx-xx-xx-xx-xx]
	 */
	private String	macAddress;

	/**
	 * Constructs new instance of <tt>CM_MAC_ADDRESS </tt> packet
	 *
	 * @param opcode
	 */
	public CM_MAC_ADDRESS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readC();
		short counter = (short)readH();
		for(short i = 0; i < counter; i++)
			readD();
		macAddress = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		if(BannedMacManager.getInstance().isBanned(macAddress)) {
			//TODO some information packets
			this.getConnection().closeNow();
			LoggerFactory.getLogger(CM_MAC_ADDRESS.class).info("[MAC_AUDIT] "+macAddress+" ("+this.getConnection().getIP()+") was kicked due to mac ban");
		}
		else
			this.getConnection().setMacAddress(macAddress);
	}
}
