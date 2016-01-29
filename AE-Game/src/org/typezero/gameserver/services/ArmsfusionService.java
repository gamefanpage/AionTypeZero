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

package org.typezero.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemQuality;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.services.item.ItemSocketService;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * This class is responsible of Armsfusion-related tasks (fusion,breaking)
 *
 * @author Wakizashi modified by Source & xTz
 */
public class ArmsfusionService {

	private static final Logger log = LoggerFactory.getLogger(ArmsfusionService.class);

	public static void fusionWeapons(Player player, int firstItemUniqueId, int secondItemUniqueId) {
		Item firstItem = player.getInventory().getItemByObjId(firstItemUniqueId);
		if (firstItem == null)
			firstItem = player.getEquipment().getEquippedItemByObjId(firstItemUniqueId);

		Item secondItem = player.getInventory().getItemByObjId(secondItemUniqueId);
		if (secondItem == null)
			secondItem = player.getEquipment().getEquippedItemByObjId(secondItemUniqueId);

		/*
		 * Check if item is in bag
		 */
		if (firstItem == null || secondItem == null || !(player.getTarget() instanceof Npc))
			return;

		double priceRate = PricesService.getGlobalPrices(player.getRace()) * .01;
		double taxRate = PricesService.getTaxes(player.getRace()) * .01;
		double rarity = rarityRate(firstItem.getItemTemplate().getItemQuality());
		int priceMod = PricesService.getGlobalPricesModifier() * 2;
		int level = firstItem.getItemTemplate().getLevel();

		int price = (int) (priceMod * priceRate * taxRate * rarity * level * level);
		log.debug("Rarete: " + rarity + " Prix Ratio: " + priceRate + " Tax: " + taxRate + " Mod: " + priceMod
			+ " NiveauDeLArme: " + level);
		log.debug("Prix: " + price);

		if (player.getInventory().getKinah() < price) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_ENOUGH_MONEY(firstItem.getNameId(), secondItem.getNameId()));
			return;
		}

		/*
		 * Fusioned weapons must be not fusioned
		 */
		if (firstItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameId()));
			return;
		}
		if (secondItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(secondItem.getNameId()));
			return;
		}

		if (!firstItem.getItemTemplate().isCanFuse() || !secondItem.getItemTemplate().isCanFuse()) {
			PacketSendUtility.sendMessage(player, "You performed illegal operation, admin will catch you");
			log.info("[AUDIT] Client hack with item fusion, player: " + player.getName());
			return;
		}

		if (!firstItem.getItemTemplate().isTwoHandWeapon()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameId()));
			return;
		}

		// Fusioned weapons must have same type
		if (firstItem.getItemTemplate().getWeaponType() != secondItem.getItemTemplate().getWeaponType()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_DIFFERENT_TYPE);
			return;
		}

		/*
		 * Second weapon must have inferior or equal lvl. in relation to first weapon
		 */
		if (secondItem.getItemTemplate().getLevel() > firstItem.getItemTemplate().getLevel()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_MAIN_REQUIRE_HIGHER_LEVEL);
			return;
		}

		//You can not combine Conditioning and Augmenting
		if (firstItem.getImprovement() != null && secondItem.getImprovement() != null) {
			if (firstItem.getImprovement().getChargeWay() != secondItem.getImprovement().getChargeWay()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_COMPARABLE_ITEM);
				return;
			}
		}

		firstItem.setFusionedItem(secondItem.getItemTemplate());

		ItemSocketService.removeAllFusionStone(player, firstItem);

		if (secondItem.hasOptionalSocket())
			firstItem.setOptionalFusionSocket(secondItem.getOptionalSocket());
		else
			firstItem.setOptionalFusionSocket(0);

		ItemSocketService.copyFusionStones(secondItem, firstItem);
		firstItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
		DAOManager.getDAO(InventoryDAO.class).store(firstItem, player);

		if (!player.getInventory().decreaseByObjectId(secondItemUniqueId, 1))
			return;

		ItemPacketService.updateItemAfterInfoChange(player, firstItem);
		player.getInventory().decreaseKinah(price);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_SUCCESS(firstItem.getNameId(), secondItem.getNameId()));
	}

	private static double rarityRate(ItemQuality rarity) {
		switch (rarity) {
			case COMMON:
				return 1.0;
			case RARE:
				return 1.25;
			case LEGEND:
				return 1.5;
			case UNIQUE:
				return 2.0;
			case EPIC:
				return 2.5;
			case MYTHIC:
			    return 3.0;
			default:
				return 1.0;
		}
	}

	public static void breakWeapons(Player player, int weaponToBreakUniqueId) {
		Item weaponToBreak = player.getInventory().getItemByObjId(weaponToBreakUniqueId);
		if (weaponToBreak == null)
			weaponToBreak = player.getEquipment().getEquippedItemByObjId(weaponToBreakUniqueId);

		if (weaponToBreak == null || !(player.getTarget() instanceof Npc))
			return;

		if (!weaponToBreak.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOUND_ERROR_NOT_AVAILABLE(weaponToBreak.getNameId()));
			return;
		}

		weaponToBreak.setFusionedItem(null);
		ItemSocketService.removeAllFusionStone(player, weaponToBreak);
		DAOManager.getDAO(InventoryDAO.class).store(weaponToBreak, player);

		ItemPacketService.updateItemAfterInfoChange(player, weaponToBreak);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUNDED_ITEM_DECOMPOUND_SUCCESS(weaponToBreak.getNameId()));
	}
}
