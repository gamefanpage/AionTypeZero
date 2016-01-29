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

import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.loginserver.LoginServer;

/**
 * In this packets aion client is authenticating himself by providing accountId and rest of sessionKey - we will check
 * if its valid at login server side.
 *
 * @author -Nemesiss-
 */
// TODO: L2AUTH? Really? :O
public class CM_L2AUTH_LOGIN_CHECK extends AionClientPacket {

	/**
	 * playOk2 is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int playOk2;
	/**
	 * playOk1 is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int playOk1;
	/**
	 * accountId is part of session key - its used for authentication we will check if this accountId is matching any
	 * waiting account login server side and check if rest of session key is ok.
	 */
	private int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int loginOk;

	@SuppressWarnings("unused")
	private int unk1;
	@SuppressWarnings("unused")
	private int unk2;
	/**
	 * Constructs new instance of <tt>CM_L2AUTH_LOGIN_CHECK </tt> packet
	 *
	 * @param opcode
	 */
	public CM_L2AUTH_LOGIN_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		playOk2 = readD();
		playOk1 = readD();
		accountId = readD();
		loginOk = readD();
		unk1 = readD();
		unk2 = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		LoginServer.getInstance().requestAuthenticationOfClient(accountId, getConnection(), loginOk, playOk1, playOk2);
	}
}
