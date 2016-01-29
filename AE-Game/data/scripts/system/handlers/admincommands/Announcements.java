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

import java.util.Set;

import org.typezero.gameserver.model.Announcement;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.AnnouncementService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Divinity
 */
public class Announcements extends AdminCommand {

	private AnnouncementService announceService;

	public Announcements() {
		super("announcements");
		announceService = AnnouncementService.getInstance();
	}

	@Override
	public void execute(Player player, String... params) {
		if (params[0].equals("list")) {
			Set<Announcement> announces = announceService.getAnnouncements();
			PacketSendUtility.sendMessage(player, "ID  |  FACTION  |  CHAT TYPE  |  DELAY  |  MESSAGE");
			PacketSendUtility.sendMessage(player, "-------------------------------------------------------------------");

			for (Announcement announce : announces)
				PacketSendUtility.sendMessage(
					player,
					announce.getId() + "  |  " + announce.getFaction() + "  |  " + announce.getType() + "  |  "
						+ announce.getDelay() + "  |  " + announce.getAnnounce());
		}
		else if (params[0].equals("add")) {
			if ((params.length < 5)) {
				onFail(player, null);
				return;
			}

			int delay;

			try {
				delay = Integer.parseInt(params[3]);
			}
			catch (NumberFormatException e) {
				// 15 minutes, default
				delay = 900;
			}

			String message = "";

			// Add with space
			for (int i = 4; i < params.length - 1; i++)
				message += params[i] + " ";

			// Add the last without the end space
			message += params[params.length - 1];

			// Create the announce
			Announcement announce = new Announcement(message, params[1], params[2], delay);

			// Add the announce in the database
			announceService.addAnnouncement(announce);

			// Reload all announcements
			announceService.reload();

			PacketSendUtility.sendMessage(player, "The announcement has been created with successful !");
		}
		else if (params[0].equals("delete")) {
			if ((params.length < 2)) {
				onFail(player, null);
				return;
			}

			int id;

			try {
				id = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(player, "The announcement's ID is wrong !");
				onFail(player, e.getMessage());
				return;
			}

			// Delete the announcement from the database
			announceService.delAnnouncement(id);

			// Reload all announcements
			announceService.reload();

			PacketSendUtility.sendMessage(player, "The announcement has been deleted with successful !");
		}
		else {
			onFail(player, null);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		String syntaxCommand = "Syntax: //announcements list - Obtain all announcements in the database.\n";
		syntaxCommand += "Syntax: //announcements add <faction: ELYOS | ASMODIANS | ALL> <type: SYSTEM | WHITE | ORANGE | SHOUT | YELLOW> <delay in seconds> <message> - Add an announcements in the database.\n";
		syntaxCommand += "Syntax: //announcements delete <id (see //announcements list to find all id> - Delete an announcements from the database.";
		PacketSendUtility.sendMessage(player, syntaxCommand);
	}
}
