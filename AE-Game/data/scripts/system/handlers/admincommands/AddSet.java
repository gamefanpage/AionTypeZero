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
import org.typezero.gameserver.model.templates.itemset.ItemPart;
import org.typezero.gameserver.model.templates.itemset.ItemSetTemplate;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author Antivirus
 */
public class AddSet extends AdminCommand {

	public AddSet() {
		super("addset");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0 || params.length > 2) {
			onFail(player, null);
			return;
		}

		int itemSetId = 0;
		Player receiver = null;

		try {
			itemSetId = Integer.parseInt(params[0]);
			receiver = player;
		}
		catch (NumberFormatException e) {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));

			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Could not find a player by that name.");
				return;
			}

			try {
				itemSetId = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException ex) {

				PacketSendUtility.sendMessage(player, "You must give number to itemset ID.");
				return;
			}
			catch (Exception ex2) {
				PacketSendUtility.sendMessage(player, "Occurs an error.");
				return;
			}
		}

		ItemSetTemplate itemSet = DataManager.ITEM_SET_DATA.getItemSetTemplate(itemSetId);
		if (itemSet == null) {
			PacketSendUtility.sendMessage(player, "ItemSet does not exist with id " + itemSetId);
			return;
		}

		if (receiver.getInventory().getFreeSlots() < itemSet.getItempart().size()) {
			PacketSendUtility
				.sendMessage(player, "Inventory needs at least " + itemSet.getItempart().size() + " free slots.");
			return;
		}

		for (ItemPart setPart : itemSet.getItempart()) {
			long count = ItemService.addItem(receiver, setPart.getItemid(), 1);

			if (count != 0) {
				PacketSendUtility.sendMessage(player, "Item " + setPart.getItemid() + " couldn't be added");
				return;
			}
		}

		PacketSendUtility.sendMessage(player, "Item Set added successfully");
		PacketSendUtility.sendMessage(receiver, "admin gives you an item set");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //addset <player> <itemset ID>");
		PacketSendUtility.sendMessage(player, "syntax //addset <itemset ID>");
	}

}
