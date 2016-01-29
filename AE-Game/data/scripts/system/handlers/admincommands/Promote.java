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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * Admin promote command.
 *
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Promote extends AdminCommand {

	public Promote() {
		super("promote");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 3) {
			PacketSendUtility.sendMessage(admin, "syntax //promote <characterName> <accesslevel | membership> <mask> ");
			return;
		}

		int mask = 0;
		try {
			mask = Integer.parseInt(params[2]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Only number!");
			return;
		}

		int type = 0;
		if (params[1].toLowerCase().equals("accesslevel")) {
			type = 1;
			if (mask > 3 || mask < 0) {
				PacketSendUtility.sendMessage(admin, "accesslevel can be 0 - 3");
				return;
			}
		}
		else if (params[1].toLowerCase().equals("membership")) {
			type = 2;
			if (mask > 10 || mask < 0) {
				PacketSendUtility.sendMessage(admin, "membership can be 0 - 10");
				return;
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "syntax //promote <characterName> <accesslevel | membership> <mask>");
			return;
		}

		Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		LoginServer.getInstance()
			.sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), mask, type);

	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //promote <characterName> <accesslevel | membership> <mask> ");
	}
}
