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

package org.typezero.gameserver.services.item;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.ArmorType;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.actions.ItemActions;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Sarynth modified by Wakizashi
 */
public class ItemRemodelService {

	/**
	 * @param player
	 * @param keepItemObjId
	 * @param extractItemObjId
	 */
	public static void remodelItem(Player player, int keepItemObjId, int extractItemObjId) {
		Storage inventory = player.getInventory();
		Item keepItem = inventory.getItemByObjId(keepItemObjId);
		Item extractItem = inventory.getItemByObjId(extractItemObjId);

		long remodelCost = PricesService.getPriceForService(1000, player.getRace());

		if (keepItem == null || extractItem == null) { // NPE check.
			return;
		}

		// Check Player Level
		if (player.getLevel() < 10) {

			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_PC_LEVEL_LIMIT);
			return;
		}

		// Check Kinah
		if (player.getInventory().getKinah() < remodelCost) {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_ENOUGH_GOLD(new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}

		// Check for using "Pattern Reshaper" (168100000)
		if (extractItem.getItemTemplate().getTemplateId() == 168100000) {
			if (!keepItem.isSkinnedItem()) {
				PacketSendUtility.sendMessage(player, "That item does not have a remodeled skin to remove.");
				return;
			}
			// Remove Money
			if(!player.getInventory().tryDecreaseKinah(remodelCost))
				return;
			// Remove Pattern Reshaper
			player.getInventory().decreaseItemCount(extractItem, 1);

			// Revert item to ORIGINAL SKIN
			keepItem.setItemSkinTemplate(keepItem.getItemTemplate());

			// Remove dye color if item can not be dyed.
			if (!keepItem.getItemTemplate().isItemDyePermitted())
				keepItem.setItemColor(0);

			// Notify Player
			ItemPacketService.updateItemAfterInfoChange(player, keepItem);
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}
		// Check that types match.
		/*if (keepItem.getItemTemplate().getWeaponType() != extractItem.getItemSkinTemplate().getWeaponType()
			|| (extractItem.getItemSkinTemplate().getArmorType() != ArmorType.CLOTHES && keepItem.getItemTemplate().getArmorType() != extractItem
				.getItemSkinTemplate().getArmorType()) || keepItem.getItemTemplate().getArmorType() == ArmorType.CLOTHES
			|| keepItem.getItemTemplate().getItemSlot() != extractItem.getItemSkinTemplate().getItemSlot()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_COMPATIBLE(new DescriptionId(keepItem
				.getItemTemplate().getNameId()), new DescriptionId(extractItem.getItemSkinTemplate().getNameId())));
			return;
		}*/
                if (keepItem.getItemTemplate().getItemSlot() != extractItem.getItemSkinTemplate().getItemSlot()) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_COMPATIBLE(new DescriptionId(keepItem.getItemTemplate().getNameId()), new DescriptionId(extractItem.getItemSkinTemplate().getNameId())));
                    return;
                }

		if (!keepItem.isRemodelable(player)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300478, new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}

		if (!extractItem.isRemodelable(player)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300482, new DescriptionId(extractItem.getItemTemplate().getNameId())));
			return;
		}

		ItemTemplate skin = extractItem.getItemSkinTemplate();
		ItemActions actions = skin.getActions();
		if (extractItem.isSkinnedItem() && actions != null && actions.getRemodelAction() != null
			&& actions.getRemodelAction().getExtractType() == 2) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300482, new DescriptionId(extractItem.getItemTemplate().getNameId())));
			return;
		}
		// -- SUCCESS --

		// Remove Money
		player.getInventory().decreaseKinah(remodelCost);

		// Remove Item
		player.getInventory().decreaseItemCount(extractItem, 1);

		// REMODEL ITEM
		keepItem.setItemSkinTemplate(skin);

		// Transfer Dye
		keepItem.setItemColor(extractItem.getItemColor());

		// Notify Player
		ItemPacketService.updateItemAfterInfoChange(player, keepItem);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300483, new DescriptionId(keepItem.getItemTemplate().getNameId())));
	}
}
