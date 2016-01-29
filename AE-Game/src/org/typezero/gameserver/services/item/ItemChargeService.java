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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.items.ChargeInfo;
import org.typezero.gameserver.model.templates.item.Improvement;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ATracer
 */
public class ItemChargeService {

	/**
	 * @return collection of items for conditioning
	 */
	public static Collection<Item> filterItemsToCondition(Player player, Item selectedItem, final int chargeWay) {
		if (selectedItem != null) {
			return Collections.singletonList(selectedItem);
		}
		return Collections2.filter(player.getEquipment().getEquippedItems(), new Predicate<Item>() {
			@Override
			public boolean apply(Item item) {
				return item.getChargeLevelMax() != 0 && item.getImprovement() != null
						&& item.getImprovement().getChargeWay() == chargeWay
						&& item.getChargePoints() < ChargeInfo.LEVEL2;
			}

		});
	}

	public static void startChargingEquippedItems(final Player player, int senderObj, final int chargeWay) {
		// TODO: Check this : SM_QUESTION_WINDOW.STR_ITEM_CHARGE_CONFIRM_SOME_ALREADY_CHARGED !!!
		final Collection<Item> filteredItems = filterItemsToCondition(player, null, chargeWay);
		if (filteredItems.isEmpty()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(chargeWay == 1 ? 1400895 : 1401343));
			return;
		}

		final long payAmount = calculatePrice(filteredItems);

		RequestResponseHandler request = new RequestResponseHandler(player) {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (processPayment(player, chargeWay, payAmount)) {
					for (Item item : filteredItems) {
						chargeItem(player, item, item.getChargeLevelMax());
					}
				}
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Nothing Happens
			}

		};
		int msg = chargeWay == 1 ? SM_QUESTION_WINDOW.STR_ITEM_CHARGE_ALL_CONFIRM
				: SM_QUESTION_WINDOW.STR_ITEM_CHARGE2_ALL_CONFIRM;
		if (player.getResponseRequester().putRequest(msg, request))
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(msg,
					senderObj, 0, String.valueOf(payAmount)));
	}

	private static long calculatePrice(Collection<Item> items) {
		long result = 0;
		for (Item item : items) {
			result += getPayAmountForService(item, item.getChargeLevelMax());
		}
		return result;
	}

	public static void chargeItems(Player player, Collection<Item> items, int level) {
		for (Item item : items) {
			chargeItem(player, item, level);
		}
	}

	public static void chargeItem(Player player, Item item, int level) {
		Improvement improvement = item.getImprovement();
		if (improvement == null) {
			return;
		}
		int chargeWay = improvement.getChargeWay();
		int currentCharge = item.getChargePoints();
		switch (level) {
			case 1:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL1 - currentCharge);
				break;
			case 2:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL2 - currentCharge);
				break;
		}
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.CHARGE));
		player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		if (chargeWay == 1) {
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE_SUCCESS(new DescriptionId(item.getNameId()), level));
		}
		else {
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE2_SUCCESS(new DescriptionId(item.getNameId()), level));
		}
		player.getGameStats().updateStatsVisually();
	}

	/**
	 * Pay for conditioning of item
	 */
	public static boolean processPayment(Player player, Item item, int level) {
		return processPayment(player, item.getImprovement().getChargeWay(), getPayAmountForService(item, level));
	}

	public static boolean processPayment(Player player, int chargeWay, long amount) {
		switch (chargeWay) {
			case 1:
				return processKinahPayment(player, amount);
			case 2:
				return processAPPayment(player, amount);
		}
		return false;
	}

	public static boolean processKinahPayment(Player player, long requiredKinah) {
		return player.getInventory().tryDecreaseKinah(requiredKinah);
	}

	public static boolean processAPPayment(Player player, long requiredAP) {
		if (player.getAbyssRank().getAp() < requiredAP) {
			return false;
		}
		AbyssPointsService.addAGp(player, (int) -requiredAP, 0);
		return true;
	}

	public static long getPayAmountForService(Item item, int chargeLevel) {
		Improvement improvement = item.getImprovement();
		if (improvement == null)
			return 0;
		int price1 = improvement.getPrice1();
		int price2 = improvement.getPrice2();
		double firstLevel = price1 / 2;
		double updateLevel = Math.round(firstLevel + (price2 - price1) / 2d);

		double money = 0;
		switch (chargeLevel) {
			case 1:
                money = Math.round(firstLevel - (firstLevel / 500000 * item.getChargePoints()));
				break;
			case 2:
				switch (getNextChargeLevel(item)) {
					case 1: // full
                        money = Math.round( updateLevel + (firstLevel - (firstLevel / 500000 * item.getChargePoints())));
						break;
					case 2: // update
						money = Math.round( price1 + (updateLevel - (updateLevel / 500000 * item.getChargePoints())));
						break;
				}
		};
		return (long) money;
	}

	private static int getNextChargeLevel(Item item) {
		int charge = item.getChargePoints();
		if (charge < ChargeInfo.LEVEL1) {
			return 1;
		}
		if (charge < ChargeInfo.LEVEL2) {
			return 2;
		}
		throw new IllegalArgumentException("Invalid charge level " + charge);
	}

}
