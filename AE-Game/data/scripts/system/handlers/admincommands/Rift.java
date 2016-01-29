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
import org.typezero.gameserver.services.RiftService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.apache.commons.lang.math.NumberUtils;

public class Rift extends AdminCommand {

	private static final String COMMAND_OPEN = "open";
	private static final String COMMAND_CLOSE = "close";

	public Rift() {
		super("rift");
	}

	@Override
	public void execute(Player player, String... params) {

		if (params.length == 0) {
			showHelp(player);
			return;
		}

		if (COMMAND_CLOSE.equalsIgnoreCase(params[0]) || COMMAND_OPEN.equalsIgnoreCase(params[0])) {
			handleRift(player, params);
		}
	}

	protected void handleRift(Player player, String... params) {
		if (params.length < 2 || !NumberUtils.isDigits(params[1])) {
			showHelp(player);
			return;
		}

		int id = NumberUtils.toInt(params[1]);
		boolean result;
		if (!isValidId(player, id)) {
			showHelp(player);
			return;
		}

		if (COMMAND_OPEN.equalsIgnoreCase(params[0])) {
			boolean guards = Boolean.parseBoolean(params[2]);
			result = RiftService.getInstance().openRifts(id, guards);
			PacketSendUtility.sendMessage(player, result ? "Rifts is opened!" : "Rifts was already opened");
		}
		else if (COMMAND_CLOSE.equalsIgnoreCase(params[0])) {
			result = RiftService.getInstance().closeRifts(id);
			PacketSendUtility.sendMessage(player, result ? "Rifts is closed!" : "Rifts was already closed");
		}
	}

	protected boolean isValidId(Player player, int id) {
		if (!RiftService.getInstance().isValidId(id)) {
			PacketSendUtility.sendMessage(player, "Id " + id + " is invalid");
			return false;
		}

		return true;
	}

	protected void showHelp(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //rift open|close <Id|worldId> (open with boolean for guards)");
	}

}
