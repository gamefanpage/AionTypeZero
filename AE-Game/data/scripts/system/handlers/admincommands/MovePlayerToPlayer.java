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
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * Admin moveplayertoplayer command.
 *
 * @author Tanelorn
 */
public class MovePlayerToPlayer extends AdminCommand {

	public MovePlayerToPlayer() {
		super("moveplayertoplayer");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 2) {
			PacketSendUtility.sendMessage(admin,
				"syntax //moveplayertoplayer <characterNameToMove> <characterNameDestination>");
			return;
		}

		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		Player playerDestination = World.getInstance().findPlayer(Util.convertName(params[1]));
		if (playerDestination == null) {
			PacketSendUtility.sendMessage(admin, "The destination player is not online.");
			return;
		}

		if (playerToMove.getObjectId() == playerDestination.getObjectId()) {
			PacketSendUtility.sendMessage(admin, "Cannot move the specified player to their own position.");
			return;
		}

		TeleportService2.teleportTo(playerToMove, playerDestination.getWorldId(), playerDestination.getInstanceId(),
			playerDestination.getX(), playerDestination.getY(), playerDestination.getZ(), playerDestination.getHeading());

		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to the location of player "
			+ playerDestination.getName() + ".");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by an administrator.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player,
			"syntax //moveplayertoplayer <characterNameToMove> <characterNameDestination>");
	}
}
