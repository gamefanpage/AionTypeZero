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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.AdminService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Phantom, ATracer, Source
 */
public class Add extends AdminCommand {

	public Add() {
		super("add");
	}

	@Override
	public void execute(Player player, String... params) {
		if ((params.length < 0) || (params.length < 1)) {
			onFail(player, null);
			return;
		}
		int itemId = 0;
		long itemCount = 1;
		Player receiver;

		try {
			String item = params[0];
			// Some item links have space before Id
			if (item.equals("[item:")) {
				item = params[1];
				Pattern id = Pattern.compile("(\\d{9})");
				Matcher result = id.matcher(item);
				if (result.find())
					itemId = Integer.parseInt(result.group(1));

				if (params.length == 3)
					itemCount = Long.parseLong(params[2]);
			}
			else {
				Pattern id = Pattern.compile("\\[item:(\\d{9})");
				Matcher result = id.matcher(item);

				if (result.find())
					itemId = Integer.parseInt(result.group(1));
				else
					itemId = Integer.parseInt(params[0]);

				if (params.length == 2)
					itemCount = Long.parseLong(params[1]);
			}
			receiver = player;
		}
		catch (NumberFormatException e) {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Could not find a player by that name.");
				return;
			}

			try {
				String item = params[1];
				// Some item links have space before Id
				if (item.equals("[item:")) {
					item = params[2];
					Pattern id = Pattern.compile("(\\d{9})");
					Matcher result = id.matcher(item);
					if (result.find())
						itemId = Integer.parseInt(result.group(1));

					if (params.length == 4)
						itemCount = Long.parseLong(params[3]);
				}
				else {
					Pattern id = Pattern.compile("\\[item:(\\d{9})");
					Matcher result = id.matcher(item);

					if (result.find())
						itemId = Integer.parseInt(result.group(1));
					else
						itemId = Integer.parseInt(params[1]);

					if (params.length == 3)
						itemCount = Long.parseLong(params[2]);
				}
			}
			catch (NumberFormatException ex) {
				PacketSendUtility.sendMessage(player, "You must give number to itemid.");
				return;
			}
			catch (Exception ex2) {
				PacketSendUtility.sendMessage(player, "Occurs an error.");
				return;
			}
		}

		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			PacketSendUtility.sendMessage(player, "Item id is incorrect: " + itemId);
			return;
		}

		if (!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add"))
			return;

		long count = ItemService.addItem(receiver, itemId, itemCount);

		if (count == 0) {
			if (player != receiver) {
				PacketSendUtility.sendMessage(player, "You successfully gave " + itemCount + " x [item:"
						+ itemId + "] to " + receiver.getName() + ".");
				PacketSendUtility.sendMessage(receiver, "You successfully received " + itemCount + " x [item:"
						+ itemId + "] from " + player.getName() + ".");
			}
			else
				PacketSendUtility.sendMessage(player, "You successfully received " + itemCount + " x [item:"
						+ itemId + "]");
		}
		else {
			PacketSendUtility.sendMessage(player, "Item couldn't be added");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //add <player> <item Id | link> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link>");
	}

}
