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
 * The universal packet for account/IP bans
 *
 * @author Watson
 */
public class SM_BAN extends LsServerPacket {

	/**
	 * Ban type 1 = account 2 = IP 3 = Full ban (account and IP)
	 */
	private final byte type;

	/**
	 * Account to ban
	 */
	private final int accountId;

	/**
	 * IP or mask to ban
	 */
	private final String ip;

	/**
	 * Time in minutes. 0 = infinity; If time < 0 then it's unban command
	 */
	private final int time;

	/**
	 * Object ID of Admin, who request the ban
	 */
	private final int adminObjId;

	public SM_BAN(byte type, int accountId, String ip, int time, int adminObjId) {
		super(0x06);
		this.type = type;
		this.accountId = accountId;
		this.ip = ip;
		this.time = time;
		this.adminObjId = adminObjId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeC(type);
		writeD(accountId);
		writeS(ip);
		writeD(time);
		writeD(adminObjId);
	}
}
