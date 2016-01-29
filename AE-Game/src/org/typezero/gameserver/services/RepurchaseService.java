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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.trade.RepurchaseList;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.audit.AuditLogger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author xTz
 */
public class RepurchaseService {

	private Multimap<Integer, Item> repurchaseItems = ArrayListMultimap.create();

	/**
	 * Save items for repurchase for this player
	 */
	public void addRepurchaseItems(Player player, List<Item> items) {
		repurchaseItems.putAll(player.getObjectId(), items);
	}

	/**
	 * Delete all repurchase items for this player
	 */
	public void removeRepurchaseItems(Player player) {
		repurchaseItems.removeAll(player.getObjectId());
	}

	public void removeRepurchaseItem(Player player, Item item) {
		repurchaseItems.get(player.getObjectId()).remove(item);
	}

	public Collection<Item> getRepurchaseItems(int playerObjectId) {
		Collection<Item> items = repurchaseItems.get(playerObjectId);
		return items != null ? items : Collections.<Item> emptyList();
	}

	public Item getRepurchaseItem(Player player, int itemObjectId) {
		Collection<Item> items = getRepurchaseItems(player.getObjectId());
		for (Item item : items) {
			if (item.getObjectId() == itemObjectId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @param player
	 * @param repurchaseList
	 */
	public void repurchaseFromShop(Player player, RepurchaseList repurchaseList) {
		if (!RestrictionsManager.canTrade(player)) {
			return;
		}
		Storage inventory = player.getInventory();
		for (Item repurchaseItem : repurchaseList.getRepurchaseItems()) {
			Collection<Item> items = repurchaseItems.get(player.getObjectId());
			if (items.contains(repurchaseItem)) {
				if (inventory.tryDecreaseKinah(repurchaseItem.getRepurchasePrice())) {
					ItemService.addItem(player, repurchaseItem);
					removeRepurchaseItem(player, repurchaseItem);
				}
				else {
					AuditLogger.info(player, "Player try repurchase item: " + repurchaseItem.getItemId() + " count: " + repurchaseItem.getItemCount()
						+ " whithout kinah");
				}
			}
			else {
				AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM try dupe item: " + repurchaseItem.getItemId() + " count: "
					+ repurchaseItem.getItemCount());
			}
		}
	}

	public static RepurchaseService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {

		protected static final RepurchaseService INSTANCE = new RepurchaseService();
	}

}
