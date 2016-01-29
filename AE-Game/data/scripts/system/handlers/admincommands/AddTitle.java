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
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author xavier
 */
public class AddTitle extends AdminCommand {

	public AddTitle() {
		super("addtitle");
	}

	@Override
	public void execute(Player player, String... params) {
		if ((params.length < 1) || (params.length > 2)) {
			onFail(player, null);
			return;
		}

		int titleId = Integer.parseInt(params[0]);
		if ((titleId > 274) || (titleId < 1)) {
			PacketSendUtility.sendMessage(player, "title id " + titleId + " is invalid (must be between 1 and 274)");
			return;
		}

		Player target = null;
		if (params.length == 2) {
			target = World.getInstance().findPlayer(Util.convertName(params[1]));
			if (target == null) {
				PacketSendUtility.sendMessage(player, "player " + params[1] + " was not found");
				return;
			}
		}
		else {
			VisibleObject creature = player.getTarget();
			if (player.getTarget() instanceof Player) {
				target = (Player) creature;
			}

			if (target == null) {
				target = player;
			}
		}

		if (titleId < 272)
			//titleId = target.getRace().getRaceId() * 272 + titleId;

		if (!target.getTitleList().addTitle(titleId, false, 0)) {
			PacketSendUtility.sendMessage(player, "you can't add title #" + titleId + " to "
				+ (target.equals(player) ? "yourself" : target.getName()));
		}
		else {
			if (target.equals(player)) {
				PacketSendUtility.sendMessage(player, "you added to yourself title #" + titleId);
			}
			else {
				PacketSendUtility.sendMessage(player, "you added to " + target.getName() + " title #" + titleId);
				PacketSendUtility.sendMessage(target, player.getName() + " gave you title #" + titleId);
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //addtitle title_id [playerName]");
	}
}
