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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.IStorage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.services.ExchangeService;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import static org.typezero.gameserver.services.item.ItemPacketService.sendItemDeletePacket;
import static org.typezero.gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;
import java.util.List;

/**
 * @author ATracer
 */
public class ItemMoveService {

	public static void moveItem(Player player, int itemObjId, byte sourceStorageType, byte destinationStorageType,
		short slot) {
		if (ExchangeService.getInstance().isPlayerInExchange(player))
			return;

		IStorage sourceStorage = player.getStorage(sourceStorageType);
		Item item = player.getStorage(sourceStorageType).getItemByObjId(itemObjId);

		if (item == null)
			return;

		if (sourceStorageType == destinationStorageType) {
			if (item.getEquipmentSlot() != slot)
				moveInSameStorage(sourceStorage, item, slot);
			return;
		}

		if (sourceStorageType != destinationStorageType
			&& (ItemRestrictionService.isItemRestrictedTo(player, item, destinationStorageType) || ItemRestrictionService
				.isItemRestrictedFrom(player, item, sourceStorageType))) {
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), item);
			return;
		}
		IStorage targetStorage = player.getStorage(destinationStorageType);
		LegionService.getInstance().addWHItemHistory(player, item.getItemId(), item.getItemCount(), sourceStorage, targetStorage);
		if (slot == -1) {
			if (item.getItemTemplate().isStackable()) {
				List<Item> sameItems = targetStorage.getItemsByItemId(item.getItemId());
				for (Item sameItem : sameItems) {
					long itemCount = item.getItemCount();
					if (itemCount == 0) {
						break;
					}
					// we can merge same stackable items
					ItemSplitService.mergeStacks(sourceStorage, targetStorage, item, sameItem, itemCount);
				}
			}
		}
		if (!targetStorage.isFull() && item.getItemCount() > 0) {
			sourceStorage.remove(item);
			sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), item, ItemDeleteType.MOVE);
			item.setEquipmentSlot(slot);
			targetStorage.add(item);
		}
	}

	/**
	 * @param storage
	 * @param item
	 * @param slot
	 */
	private static void moveInSameStorage(IStorage storage, Item item, short slot) {
		storage.setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setEquipmentSlot(slot);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public static void switchItemsInStorages(Player player, byte sourceStorageType, int sourceItemObjId,
		byte replaceStorageType, int replaceItemObjId) {
		IStorage sourceStorage = player.getStorage(sourceStorageType);
		IStorage replaceStorage = player.getStorage(replaceStorageType);

		Item sourceItem = sourceStorage.getItemByObjId(sourceItemObjId);
		if (sourceItem == null)
			return;

		Item replaceItem = replaceStorage.getItemByObjId(replaceItemObjId);
		if (replaceItem == null)
			return;

		// restrictions checks
		if (ItemRestrictionService.isItemRestrictedFrom(player, sourceItem, sourceStorageType)
			|| ItemRestrictionService.isItemRestrictedFrom(player, replaceItem, replaceStorageType)
			|| ItemRestrictionService.isItemRestrictedTo(player, sourceItem, replaceStorageType)
			|| ItemRestrictionService.isItemRestrictedTo(player, replaceItem, sourceStorageType))
			return;

		long sourceSlot = sourceItem.getEquipmentSlot();
		long replaceSlot = replaceItem.getEquipmentSlot();

		sourceItem.setEquipmentSlot(replaceSlot);
		replaceItem.setEquipmentSlot(sourceSlot);

		sourceStorage.remove(sourceItem);
		replaceStorage.remove(replaceItem);

		// correct UI update order is 1)delete items 2) add items
		sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem, ItemDeleteType.MOVE);
		sendItemDeletePacket(player, StorageType.getStorageTypeById(replaceStorageType), replaceItem, ItemDeleteType.MOVE);
		sourceStorage.add(replaceItem);
		replaceStorage.add(sourceItem);
	}
}
