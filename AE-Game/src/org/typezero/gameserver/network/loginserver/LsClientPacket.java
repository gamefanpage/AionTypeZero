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

package org.typezero.gameserver.network.loginserver;

import com.aionemu.commons.network.packet.BaseClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author -Nemesiss-
 */
public abstract class LsClientPacket extends BaseClientPacket<LoginServerConnection> implements Cloneable {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LsClientPacket.class);

	/**
	 * Constructs new client packet with specified opcode. If using this constructor, user must later manually set buffer
	 * and connection.
	 *
	 * @param opcode
	 *          packet id
	 */
	protected LsClientPacket(int opcode) {
		super(opcode);
	}

	/**
	 * run runImpl catching and logging Throwable.
	 */
	@Override
	public final void run() {
		try {
			runImpl();
		}
		catch (Throwable e) {
			log.warn("error handling ls (" + getConnection().getIP() + ") message " + this, e);
		}
	}

	/**
	 * Send new LsServerPacket to connection that is owner of this packet. This method is equivalent to:
	 * getConnection().sendPacket(msg);
	 *
	 * @param msg
	 */
	protected void sendPacket(LsServerPacket msg) {
		getConnection().sendPacket(msg);
	}

	/**
	 * Clones this packet object.
	 *
	 * @return LsClientPacket
	 */
	public LsClientPacket clonePacket() {
		try {
			return (LsClientPacket) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
