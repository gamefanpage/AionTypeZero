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

package com.aionengine.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;

import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.network.aion.AionAuthResponse;
import com.aionengine.loginserver.network.aion.AionClientPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;
import com.aionengine.loginserver.network.aion.SessionKey;
import com.aionengine.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;
import com.aionengine.loginserver.network.aion.serverpackets.SM_PLAY_FAIL;
import com.aionengine.loginserver.network.aion.serverpackets.SM_PLAY_OK;

/**
 * @author -Nemesiss-
 */
public class CM_PLAY extends AionClientPacket {

	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private int accountId;

	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private int loginOk;

	/**
	 * Id of game server that this client is trying to play on.
	 */
	private byte servId;

	public CM_PLAY(ByteBuffer buf, LoginConnection client) {
		super(buf, client, 0x02);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		accountId = readD();
		loginOk = readD();
		servId = (byte) readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		LoginConnection con = getConnection();
		SessionKey key = con.getSessionKey();
		if (key.checkLogin(accountId, loginOk)) {
			GameServerInfo gsi = GameServerTable.getGameServerInfo(servId);
			if (gsi == null || !gsi.isOnline()) {
				con.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.SERVER_DOWN));
				return;
			}
			if (gsi.isGmOnly() && (getConnection().getAccount().getAccessLevel() < 1)) {
				con.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.GM_ONLY));
				return;
			}
			if (gsi.isFull()) {
				con.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.SERVER_FULL));
				return;
			}
			con.setJoinedGs();
			sendPacket(new SM_PLAY_OK(key, servId));
			return;
		}
		con.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), false);
	}

}
