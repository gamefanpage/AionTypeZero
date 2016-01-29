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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.loginserver.LsClientPacket;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author Watson
 */
public class CM_BAN_RESPONSE extends LsClientPacket {

	public CM_BAN_RESPONSE(int opCode) {
		super(opCode);
	}

	private byte type;
	private int accountId;
	private String ip;
	private int time;
	private int adminObjId;
	private boolean result;

	@Override
	public void readImpl() {
		this.type = (byte) readC();
		this.accountId = readD();
		this.ip = readS();
		this.time = readD();
		this.adminObjId = readD();
		this.result = readC() == 1;
	}

	@Override
	public void runImpl() {
		Player admin = World.getInstance().findPlayer(adminObjId);

		if (admin == null) {
			return;
		}

		// Some messages stuff
		String message;
		if (type == 1 || type == 3) {
			if (result) {
				if (time < 0)
					message = "Account ID " + accountId + " was successfully unbanned";
				else if (time == 0)
					message = "Account ID " + accountId + " was successfully banned";
				else
					message = "Account ID " + accountId + " was successfully banned for " + time + " minutes";
			}
			else
				message = "Error occurred while banning player's account";
			PacketSendUtility.sendMessage(admin, message);
		}
		if (type == 2 || type == 3) {
			if (result) {
				if (time < 0)
					message = "IP mask " + ip + " was successfully removed from block list";
				else if (time == 0)
					message = "IP mask " + ip + " was successfully added to block list";
				else
					message = "IP mask " + ip + " was successfully added to block list for " + time + " minutes";
			}
			else
				message = "Error occurred while adding IP mask " + ip;
			PacketSendUtility.sendMessage(admin, message);
		}
	}
}
