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
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.LsClientPacket;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.rates.Rates;
import org.typezero.gameserver.world.World;

/**
 * @author Aionchs-Wylovech
 */
public class CM_LS_CONTROL_RESPONSE extends LsClientPacket {

	public CM_LS_CONTROL_RESPONSE(int opCode) {
		super(opCode);
	}

	private int type;
	private boolean result;
	private String playerName;
	private byte param;
	private String adminName;
	private int accountId;

	@Override
	public void readImpl() {
		type = readC();
		result = readC() == 1;
		adminName = readS();
		playerName = readS();
		param = (byte) readC();
		accountId = readD();
	}

	@Override
	public void runImpl() {
		World world = World.getInstance();
		Player admin = world.findPlayer(Util.convertName(adminName));
		Player player = world.findPlayer(Util.convertName(playerName));
		LoginServer.getInstance().accountUpdate(accountId, param, type);
		switch (type) {
			case 1:
				if (!result) {
					if (admin != null)
						PacketSendUtility.sendMessage(admin, playerName + " has been promoted Administrator with role " + param);
					if (player != null) {
						PacketSendUtility.sendMessage(player, "You have been promoted Administrator with role " + param + " by "
							+ adminName);
					}
				}
				else {
					if (admin != null)
						PacketSendUtility.sendMessage(admin, " Abnormal, the operation failed! ");
				}
				break;
			case 2:
				if (!result) {
					if (admin != null)
						PacketSendUtility.sendMessage(admin, playerName + " has been promoted membership with level " + param);
					if (player != null) {
						player.setRates(Rates.getRatesFor(param));
						PacketSendUtility.sendMessage(player, "You have been promoted membership with level " + param + " by "
							+ adminName);
					}
				}
				else {
					if (admin != null)
						PacketSendUtility.sendMessage(admin, " Abnormal, the operation failed! ");
				}
				break;
		}
	}
}
