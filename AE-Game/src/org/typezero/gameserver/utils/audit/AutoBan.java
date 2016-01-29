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

package org.typezero.gameserver.utils.audit;

import org.typezero.gameserver.configs.main.PunishmentConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.BannedMacManager;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.services.PunishmentService;

/**
 * @author synchro2
 */
public class AutoBan {

	protected static void punishment(Player player, String message) {

		String reason = "AUTO " +message;
		String address = player.getClientConnection().getMacAddress();
		String accountIp = player.getClientConnection().getIP();
		int accountId = player.getClientConnection().getAccount().getId();
		int playerId = player.getObjectId();
		int time = PunishmentConfig.PUNISHMENT_TIME;
		int minInDay = 1440;
		int dayCount = (int)(Math.floor((double)(time/minInDay)));

		switch (PunishmentConfig.PUNISHMENT_TYPE) {
			case 1:
				player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
				break;
			case 2:
				PunishmentService.banChar(playerId, dayCount, reason);
				break;
			case 3:
				LoginServer.getInstance().sendBanPacket((byte)1, accountId, accountIp, time, 0);
				break;
			case 4:
				LoginServer.getInstance().sendBanPacket((byte)2, accountId, accountIp, time, 0);
				break;
			case 5:
				player.getClientConnection().closeNow();
				BannedMacManager.getInstance().banAddress(address, System.currentTimeMillis() + time * 60000, reason);
				break;
		}
	}
}
