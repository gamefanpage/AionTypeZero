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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.IStorage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.ExchangeService;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.utils.PacketSendUtility;

import static org.typezero.gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;

/**
 * @author ATracer
 */
public class ItemSplitService {

	private static final Logger log = LoggerFactory.getLogger(ItemSplitService.class);

	/**
	 * Move part of stack into different slot
	 */
	public static final void splitItem(Player player, int itemObjId, int destinationObjId, long splitAmount,
		short slotNum, byte sourceStorageType, byte destinationStorageType) {
		if (splitAmount <= 0) {
			return;
		}
		if (player.isTrading()) {
			// You cannot split items in the inventory during a trade.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300713));
			return;
		}

		IStorage sourceStorage = player.getStorage(sourceStorageType);
		IStorage destStorage = player.getStorage(destinationStorageType);
		if (sourceStorage == null || destStorage == null) {
			log.warn(String.format("storage null playerName sourceStorage destStorage %s %d %d", player.getName(), sourceStorageType, destinationStorageType));
			return;
		}
		Item sourceItem = sourceStorage.getItemByObjId(itemObjId);
		Item targetItem = destStorage.getItemByObjId(destinationObjId);

		if (sourceItem == null) {
			sourceItem = sourceStorage.getKinahItem();
			if (sourceItem == null || sourceItem.getObjectId() != itemObjId) {
				log.warn(String.format("CHECKPOINT: attempt to split null item %d %d %d", itemObjId, splitAmount, slotNum));
				return;
			}
		}

		if (sourceStorageType != destinationStorageType
			&& (ItemRestrictionService.isItemRestrictedTo(player, sourceItem, destinationStorageType) || ItemRestrictionService
				.isItemRestrictedFrom(player, sourceItem, sourceStorageType))) {
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem);
			return;
		}

		// To move kinah from inventory to warehouse and vice versa client using split item packet
		if (sourceItem.getItemTemplate().isKinah()) {
			moveKinah(player, sourceStorage, splitAmount);
			return;
		}

		if (targetItem == null) {
			long oldItemCount = sourceItem.getItemCount() - splitAmount;
			if (sourceItem.getItemCount() < splitAmount || oldItemCount == 0) {
				return;
			}
			if (sourceStorageType != destinationStorageType) {
				LegionService.getInstance().addWHItemHistory(player, sourceItem.getItemId(), splitAmount, sourceStorage, destStorage);
			}
			Item newItem = ItemFactory.newItem(sourceItem.getItemTemplate().getTemplateId(), splitAmount);
			newItem.setEquipmentSlot(slotNum);
			sourceStorage.decreaseItemCount(sourceItem, splitAmount, ItemUpdateType.DEC_SPLIT);
			PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(sourceStorage.getStorageType(), player));
			if (destStorage.add(newItem) == null) {
				// if item was not added - we can release its id
				ItemService.releaseItemId(newItem);
			}
		}
		else if (targetItem.getItemId() == sourceItem.getItemId()) {
			if (sourceStorageType != destinationStorageType) {
				LegionService.getInstance().addWHItemHistory(player, sourceItem.getItemId(), splitAmount, sourceStorage, destStorage);
			}
			mergeStacks(sourceStorage, destStorage, sourceItem, targetItem, splitAmount);
		}
	}

	/**
	 * Merge 2 stacks with simple validation validation
	 */
	public static void mergeStacks(IStorage sourceStorage, IStorage destStorage, Item sourceItem, Item targetItem,
		long count) {
		if (sourceItem.getItemCount() >= count) {
			long freeCount = targetItem.getFreeCount();
			count = count > freeCount ? freeCount : count;
			long leftCount = destStorage.increaseItemCount(targetItem, count, ItemUpdateType.INC_MERGE);
			sourceStorage.decreaseItemCount(sourceItem, count - leftCount, ItemUpdateType.DEC_SPLIT);
		}

	}

	private static void moveKinah(Player player, IStorage source, long splitAmount) {
		if (source.getKinah() < splitAmount)
			return;
		if (ExchangeService.getInstance().isPlayerInExchange(player))
			return;

		switch (source.getStorageType()) {
			case CUBE: {
				IStorage destination = player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
				long chksum = (source.getKinah() - splitAmount) + (destination.getKinah() + splitAmount);

				if (chksum != source.getKinah() + destination.getKinah())
					return;

				updateKinahCount(source, splitAmount, destination);
				break;
			}

			case ACCOUNT_WAREHOUSE: {
				IStorage destination = player.getStorage(StorageType.CUBE.getId());
				long chksum = (source.getKinah() - splitAmount) + (destination.getKinah() + splitAmount);

				if (chksum != source.getKinah() + destination.getKinah())
					return;

				updateKinahCount(source, splitAmount, destination);
				break;
			}
		}
	}

	private static final void updateKinahCount(IStorage source, long splitAmount, IStorage destination) {
		source.decreaseKinah(splitAmount, ItemUpdateType.DEC_SPLIT);
		destination.increaseKinah(splitAmount, ItemUpdateType.INC_MERGE_KINAH);
	}
}
