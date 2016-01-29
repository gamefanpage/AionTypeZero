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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.templates.WarehouseExpandTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Simple
 */
public class WarehouseService {

	private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);

	private static final int MIN_EXPAND = 0;
	private static final int MAX_EXPAND = 11;

	/**
	 * Shows Question window and expands on positive response
	 *
	 * @param player
	 * @param npc
	 */
	public static void expandWarehouse(final Player player, Npc npc) {
		final WarehouseExpandTemplate expandTemplate = DataManager.WAREHOUSEEXPANDER_DATA
			.getWarehouseExpandListTemplate(npc.getNpcId());

		if (expandTemplate == null) {
			log.error("Warehouse Expand Template could not be found for Npc ID: " + npc.getObjectTemplate().getTemplateId());
			return;
		}

		if (npcCanExpandLevel(expandTemplate, player.getWarehouseSize() + 1)
			&& validateNewSize(player.getWarehouseSize() + 1)) {
			if (validateNewSize(player.getWarehouseSize() + 1)) {
				/**
				 * Check if our player can pay the warehouse expand price
				 */
				final int price = getPriceByLevel(expandTemplate, player.getWarehouseSize() + 1);
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (player.getInventory().getKinah() < price) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300831));
							return;
						}
						expand(responder);
						player.getInventory().decreaseKinah(price);
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						// nothing to do
					}
				};

				boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, responseHandler);
				if (result) {
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, 0, 0, String.valueOf(price)));
				}
			}
		}
		else
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300432));
	}

	/**
	 * @param player
	 */
	public static void expand(Player player) {
		if (!canExpand(player))
			return;
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300433, "8")); // 8 Slots added
		player.setWarehouseSize(player.getWarehouseSize() + 1);

		sendWarehouseInfo(player, false);
	}

	/**
	 * Checks if new player cube is not max
	 *
	 * @param level
	 * @return true or false
	 */
	private static boolean validateNewSize(int level) {
		// check min and max level
		if (level < MIN_EXPAND || level > MAX_EXPAND)
			return false;
		return true;
	}

	/**
	 * @param player
	 * @return
	 */
	public static boolean canExpand(Player player) {
		return validateNewSize(player.getWarehouseSize() + 1);
	}

	/**
	 * Checks if npc can expand level
	 *
	 * @param clist
	 * @param level
	 * @return true or false
	 */
	private static boolean npcCanExpandLevel(WarehouseExpandTemplate clist, int level) {
		// check if level exists in template
		if (!clist.contains(level))
			return false;
		return true;
	}

	/**
	 * The guy who created cube template should blame himself :) One day I will rewrite them
	 *
	 * @param template
	 * @param level
	 * @return
	 */
	private static int getPriceByLevel(WarehouseExpandTemplate clist, int level) {
		return clist.get(level).getPrice();
	}

	/**
	 * Sends correctly warehouse packets
	 *
	 * @param player
	 */
	public static void sendWarehouseInfo(Player player, boolean sendAccountWh) {
		List<Item> items = player.getStorage(StorageType.REGULAR_WAREHOUSE.getId()).getItems();

		int whSize = player.getWarehouseSize();
		int itemsSize = items.size();

		/**
		 * Regular warehouse
		 */
		boolean firstPacket = true;
		if (itemsSize != 0) {
			int index = 0;

			while (index + 10 < itemsSize) {
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, index + 10),
					StorageType.REGULAR_WAREHOUSE.getId(), whSize, firstPacket, player));
				index += 10;
				firstPacket = false;
			}
			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, itemsSize),
				StorageType.REGULAR_WAREHOUSE.getId(), whSize, firstPacket, player));
		}

		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.REGULAR_WAREHOUSE.getId(), whSize,
			false, player));

		if (sendAccountWh) {
			/**
			 * Account warehouse
			 */
			PacketSendUtility.sendPacket(player,
				new SM_WAREHOUSE_INFO(player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).getItemsWithKinah(),
					StorageType.ACCOUNT_WAREHOUSE.getId(), 0, true, player));
		}

		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.ACCOUNT_WAREHOUSE.getId(), 0, false,
			player));
	}
}
