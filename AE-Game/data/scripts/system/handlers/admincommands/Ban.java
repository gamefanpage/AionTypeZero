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

package admincommands;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author Watson
 */
public class Ban extends AdminCommand {

	public Ban() {
		super("ban");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
			return;
		}

		// We need to get player's account ID
		String name = Util.convertName(params[0]);
		int accountId = 0;
		String accountIp = "";

		// First, try to find player in the World
		Player player = World.getInstance().findPlayer(name);
		if (player != null) {
			accountId = player.getClientConnection().getAccount().getId();
			accountIp = player.getClientConnection().getIP();
		}

		// Second, try to get account ID of offline player from database
		if (accountId == 0)
			accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

		// Third, fail
		if (accountId == 0) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
			return;
		}

		byte type = 3; // Default: full
		if (params.length > 1) {
			// Smart Matching
			String stype = params[1].toLowerCase();
			if (("account").startsWith(stype))
				type = 1;
			else if (("ip").startsWith(stype))
				type = 2;
			else if (("full").startsWith(stype))
				type = 3;
			else {
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
				return;
			}
		}

		int time = 0; // Default: infinity
		if (params.length > 2) {
			try {
				time = Integer.parseInt(params[2]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
				return;
			}
		}
		if (time == 0) {
		   time = 60 * 24 * 365 * 10; //pseudo infinity. TODO: rework
		}

		LoginServer.getInstance().sendBanPacket(type, accountId, accountIp, time, admin.getObjectId());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
	}
}
