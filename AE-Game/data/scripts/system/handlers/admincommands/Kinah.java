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
import org.typezero.gameserver.model.items.ItemId;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author Sarynth Simple admin assistance command for adding kinah to self, named player or target player. Based on
 *         //add command. Kinah Item Id - 182400001 (Using ItemId.KINAH.value())
 */
public class Kinah extends AdminCommand {

	public Kinah() {
		super("kinah");
	}

	@Override
	public void execute(Player admin, String... params) {
		long kinahCount;
		Player receiver;

		if (params.length == 1) {
			receiver = admin;
			try {
				kinahCount = Long.parseLong(params[0]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		}
		else {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));

			if (receiver == null) {
				PacketSendUtility.sendMessage(admin, "Could not find a player by that name.");
				return;
			}

			try {
				kinahCount = Long.parseLong(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		}

		long count = ItemService.addItem(receiver, ItemId.KINAH.value(), kinahCount);

		if (count == 0) {
			PacketSendUtility.sendMessage(admin, "Kinah given successfully.");
			PacketSendUtility.sendMessage(receiver, "An admin gives you some kinah.");
		}
		else {
			PacketSendUtility.sendMessage(admin, "Kinah couldn't be given.");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //kinah [player] <quantity>");
	}
}
