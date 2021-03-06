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
 */

package admincommands;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.player.PlayerSecurityTokenService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * Created by Magenik on 9/08/2015.
 */
public class SecurityToken extends AdminCommand {

	public SecurityToken() {
		super("token");
	}

	/* (non-Javadoc)
	 * @see org.typezero.gameserver.utils.chathandlers.ChatCommand#execute(org.typezero.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void execute(Player player, String... params) {
		if (params.length < 1) {
			PacketSendUtility.sendMessage(player, "Syntax: //token <playername> || //token show <playername>");
			return;
		}
		Player receiver = null;

		if (params[0].equals("show")) {
			receiver = World.getInstance().findPlayer(Util.convertName(params[1]));
			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Can't find this player, maybe he's not online");
				return;
			}

			if (!"".equals(receiver.getPlayerAccount().getSecurityToken())) {
				PacketSendUtility.sendMessage(player, "The Security Token of this player is: " + receiver.getPlayerAccount().getSecurityToken());
			} else {
				PacketSendUtility.sendMessage(player, "This player haven't an Security Token!");
			}

		} else {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));

			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Can't find this player, maybe he's not online");
				return;
			}

			PlayerSecurityTokenService.getInstance().generateToken(receiver);
		}

	}

	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "Syntax: //token <playername> || //token show <playername>");
	}

}
