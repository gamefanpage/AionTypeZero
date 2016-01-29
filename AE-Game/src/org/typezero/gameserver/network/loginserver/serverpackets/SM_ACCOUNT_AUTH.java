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

package org.typezero.gameserver.network.loginserver.serverpackets;

import org.typezero.gameserver.network.loginserver.LoginServerConnection;
import org.typezero.gameserver.network.loginserver.LsServerPacket;

/**
 * In this packet Gameserver is asking if given account sessionKey is valid at Loginserver side. [if user that is
 * authenticating on Gameserver is already authenticated on Loginserver]
 * 
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_AUTH extends LsServerPacket {

	/**
	 * accountId [part of session key]
	 */
	private final int accountId;
	/**
	 * loginOk [part of session key]
	 */
	private final int loginOk;
	/**
	 * playOk1 [part of session key]
	 */
	private final int playOk1;
	/**
	 * playOk2 [part of session key]
	 */
	private final int playOk2;

	/**
	 * Constructs new instance of <tt>SM_ACCOUNT_AUTH </tt> packet.
	 * 
	 * @param accountId
	 *          account identifier.
	 * @param loginOk
	 * @param playOk1
	 * @param playOk2
	 */
	public SM_ACCOUNT_AUTH(int accountId, int loginOk, int playOk1, int playOk2) {
		super(0x01);
		this.accountId = accountId;
		this.loginOk = loginOk;
		this.playOk1 = playOk1;
		this.playOk2 = playOk2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeD(accountId);
		writeD(loginOk);
		writeD(playOk1);
		writeD(playOk2);
	}
}
