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


package org.typezero.gameserver.network.loginserver.clientpackets;

import org.typezero.gameserver.model.account.AccountTime;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.LsClientPacket;

/**
 * In this packet LoginServer is answering on GameServer request about valid authentication data and also sends account
 * name of user that is authenticating on GameServer.
 * 
 * @author -Nemesiss-
 */
public class CM_ACOUNT_AUTH_RESPONSE extends LsClientPacket {

	public CM_ACOUNT_AUTH_RESPONSE(int opCode) {
		super(opCode);
	}

	/**
	 * accountId
	 */
	private int accountId;

	/**
	 * result - true = authed
	 */
	private boolean result;

	/**
	 * accountName [if response is ok]
	 */
	private String accountName;
	/**
	 * accountTime
	 */
	private AccountTime accountTime;
	/**
	 * access level - regular/gm/admin
	 */
	private byte accessLevel;
	/**
	 * Membership - regular/premium
	 */
	private byte membership;

	/**
	 * Toll
	 */
	private long toll;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readImpl() {
		accountId = readD();
		result = readC() == 1;

		if (result) {
			accountName = readS();
			accountTime = new AccountTime();

			accountTime.setAccumulatedOnlineTime(readQ());
			accountTime.setAccumulatedRestTime(readQ());

			accessLevel = (byte) readC();
			membership = (byte) readC();
			toll = readQ();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void runImpl() {
		LoginServer.getInstance().accountAuthenticationResponse(accountId, accountName, result, accountTime, accessLevel, membership, toll);
	}
}
