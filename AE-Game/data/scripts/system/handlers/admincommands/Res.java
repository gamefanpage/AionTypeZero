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

import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_RESURRECT;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Sarynth
 */
public class Res extends AdminCommand {

	public Res() {
		super("res");
	}

	@Override
	public void execute(Player admin, String... params) {
		final VisibleObject target = admin.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "No target selected.");
			return;
		}

		if (!(target instanceof Player)) {
			PacketSendUtility.sendMessage(admin, "You can only resurrect other players.");
			return;
		}

		final Player player = (Player) target;
		if (!player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendMessage(admin, "That player is already alive.");
			return;
		}

		// Default action is to prompt for resurrect.
		if (params == null || params.length == 0 || ("prompt").startsWith(params[0])) {
			player.setPlayerResActivate(true);
			PacketSendUtility.sendPacket(player, new SM_RESURRECT(admin));
			return;
		}

		if (("instant").startsWith(params[0])) {
			PlayerReviveService.skillRevive(player);
			return;
		}

		PacketSendUtility.sendMessage(admin, "[Resurrect] Usage: target player and use //res <instant|prompt>");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
