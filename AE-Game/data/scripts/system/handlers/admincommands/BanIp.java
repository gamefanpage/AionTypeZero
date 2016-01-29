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
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Watson
 */
public class BanIp extends AdminCommand {

	public BanIp() {
		super("banip");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
			return;
		}

		String mask = params[0];

		int time = 0; // Default: infinity
		if (params.length > 1) {
			try {
				time = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				onFail(player, e.getMessage());
				return;
			}
		}
		if (time == 0) {
		   time = 60 * 24 * 365 * 10;//pseudo infinity
		}

		LoginServer.getInstance().sendBanPacket((byte) 2, 0, mask, time, player.getObjectId());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
	}
}
