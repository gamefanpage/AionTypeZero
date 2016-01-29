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
import org.typezero.gameserver.services.StaticDoorService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Rolandas
 */
public class DoorState extends AdminCommand {

	public DoorState() {
		super("door");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 3) {
			onFail(admin, null);
			return;
		}

		int doorId = 0;
		try {
			doorId = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "<id> must be a number!");
			return;
		}

		Boolean open = null;
		if (params[1].equalsIgnoreCase("open")) {
			open = true;
		}
		else if (params[1].equalsIgnoreCase("close")) {
			open = false;
		}
		if (open == null) {
			onFail(admin, null);
			return;
		}

		int state = 0;
		try {
			state = Integer.parseInt(params[2]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "<state> must be a number!");
			return;
		}

		StaticDoorService.getInstance().changeStaticDoorState(admin, doorId, open, state);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "<usage //door <id> <open|close> <state>");
	}
}
