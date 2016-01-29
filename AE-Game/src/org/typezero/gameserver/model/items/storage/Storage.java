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

package org.typezero.gameserver.model.items.storage;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javolution.util.FastList;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemId;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.item.ItemFactory;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author KID, ATracer
 */
public abstract class Storage implements IStorage {

	private ItemStorage itemStorage;
	private Item kinahItem;
	private StorageType storageType;
	private Queue<Item> deletedItems;
	/**
	 * Can be of 2 types: UPDATED and UPDATE_REQUIRED
	 */
	private PersistentState persistentState = PersistentState.UPDATED;

	public Storage(StorageType storageType) {
		this(storageType, true);
	}

	public Storage(StorageType storageType, boolean withDeletedItems) {
		itemStorage = new ItemStorage(storageType.getLimit(), storageType.getSpecialLimit());
		this.storageType = storageType;
		if (withDeletedItems)
			this.deletedItems = new ConcurrentLinkedQueue<Item>();
	}

	@Override
	public long getKinah() {
		return kinahItem == null ? 0 : kinahItem.getItemCount();
	}

	@Override
	public Item getKinahItem() {
		return kinahItem;
	}

	@Override
	public StorageType getStorageType() {
		return storageType;
	}

	void increaseKinah(long amount, Player actor) {
		increaseKinah(amount, ItemUpdateType.INC_KINAH_LOOT, actor);
	}

	void increaseKinah(long amount, ItemUpdateType updateType, Player actor) {
		if (kinahItem == null) {
			add(ItemFactory.newItem(ItemId.KINAH.value(), 0), actor);
		}
		if (amount > 0) {
			increaseItemCount(kinahItem, amount, updateType, actor);
		}
	}

	/**
	 * Decrease kinah by {@code amount} but check first that its enough in storage
	 *
	 * @return true if decrease was successful
	 */
	boolean tryDecreaseKinah(long amount, Player actor) {
		if (getKinah() >= amount) {
			decreaseKinah(amount, actor);
			return true;
		}
		return false;
	}

	/**
	 * just decrease kinah without any checks
	 */
	void decreaseKinah(long amount, Player actor) {
		decreaseKinah(amount, ItemUpdateType.DEC_KINAH, actor);
	}

	void decreaseKinah(long amount, ItemUpdateType updateType, Player actor) {
		if (amount > 0) {
			decreaseItemCount(kinahItem, amount, updateType, actor);
		}
	}

	long increaseItemCount(Item item, long count, Player actor) {
		return increaseItemCount(item, count, ItemUpdateType.DEFAULT, actor);
	}

	/**
	 * increase item count and return left count
	 */
	long increaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor) {
		long leftCount = item.increaseItemCount(count);
		ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}

	long decreaseItemCount(Item item, long count, Player actor) {
		return this.decreaseItemCount(item, count, ItemUpdateType.DEFAULT, actor);
	}

	/**
	 * decrease item count and return left count
	 */
	long decreaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor) {
		if (item == null) {
			return 0;
		}
		long leftCount = item.decreaseItemCount(count);
		if (item.getItemCount() <= 0 && !item.getItemTemplate().isKinah()) {
			delete(item, ItemDeleteType.fromUpdateType(updateType), actor);
		}
		else {
			ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		}
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}

	/**
	 * This method should be called only for new items added to inventory (loading from DB) If item is equiped - will be
	 * put to equipment if item is unequiped - will be put to default bag for now Kinah is stored separately as it will be
	 * used frequently
	 *
	 * @param item
	 */
	@Override
	public void onLoadHandler(Item item) {
		if (item.getItemTemplate().isKinah())
			kinahItem = item;
		else
			itemStorage.putItem(item);
	}

	Item add(Item item, Player actor) {
		if (item.getItemTemplate().isKinah()) {
			this.kinahItem = item;
		}
		else if (!itemStorage.putItem(item)) {
			return null;
		}
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendStorageUpdatePacket(actor, storageType, item);
		// TODO: move to ItemService
		QuestEngine.getInstance().onItemGet(new QuestEnv(null, actor, 0, 0), item.getItemTemplate().getTemplateId());
		if (item.getItemTemplate().isQuestUpdateItem())
			actor.getController().updateNearbyQuests();
		return item;
	}

	//a bit misleading name - but looks like its used only for equipment
	Item put(Item item, Player actor) {
		if (!itemStorage.putItem(item)) {
			return null;
		}
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendItemUpdatePacket(actor, storageType, item, ItemUpdateType.EQUIP_UNEQUIP);
		return item;
	}

	/**
	 * Remove item from storage without changing its state
	 */
	@Override
	public Item remove(Item item) {
		return itemStorage.removeItem(item.getObjectId());
	}

	/**
	 * Delete item from storage and mark for DB update. UNKNOWN delete type
	 */
	Item delete(Item item, Player actor) {
		return delete(item, ItemDeleteType.UNKNOWN, actor);
	}

	/**
	 * Delete item from storage and mark for DB update
	 */
	Item delete(Item item, ItemDeleteType deleteType, Player actor) {
		if (remove(item) != null) {
			item.setPersistentState(PersistentState.DELETED);
			deletedItems.add(item);
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			ItemPacketService.sendItemDeletePacket(actor, StorageType.getStorageTypeById(item.getItemLocation()), item,
				deleteType);
			if (item.getItemTemplate().isQuestUpdateItem())
				actor.getController().updateNearbyQuests();
			return item;
		}
		return null;
	}

	boolean decreaseByItemId(int itemId, long count, Player actor) {
		FastList<Item> items = itemStorage.getItemsById(itemId);
		if (items.size() == 0)
			return false;

		for (Item item : items) {
			if (count == 0) {
				break;
			}
			count = decreaseItemCount(item, count, actor);
		}

		FastList.recycle(items);
		return count == 0;
	}

	boolean decreaseByObjectId(int itemObjId, long count, Player actor) {
		return decreaseByObjectId(itemObjId, count, ItemUpdateType.DEFAULT, actor);
	}

	boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType, Player actor) {
		Item item = itemStorage.getItemByObjId(itemObjId);
		if (item == null || item.getItemCount() < count)
			return false;

		return decreaseItemCount(item, count, updateType, actor) == 0;
	}

	@Override
	public Item getFirstItemByItemId(int itemId) {
		return this.itemStorage.getFirstItemById(itemId);
	}

	@Override
	public FastList<Item> getItemsWithKinah() {
		FastList<Item> items = this.itemStorage.getItems();
		if (this.kinahItem != null) {
			items.add(this.kinahItem);
		}
		return items;
	}

	@Override
	public List<Item> getItems() {
		return this.itemStorage.getItems();
	}

	@Override
	public List<Item> getItemsByItemId(int itemId) {
		return this.itemStorage.getItemsById(itemId);
	}

	@Override
	public Queue<Item> getDeletedItems() {
		return deletedItems;
	}

	@Override
	public Item getItemByObjId(int itemObjId) {
		return this.itemStorage.getItemByObjId(itemObjId);
	}

	@Override
	public long getItemCountByItemId(int itemId) {
		FastList<Item> temp = this.itemStorage.getItemsById(itemId);
		if (temp.size() == 0)
			return 0;

		long cnt = 0;
		for (Item item : temp)
			cnt += item.getItemCount();

		return cnt;
	}

	@Override
	public boolean isFull() {
		return this.itemStorage.isFull();
	}

	public boolean isFullSpecialCube() {
		return this.itemStorage.isFullSpecialCube();
	}

	public boolean isFull(int inventory) {
		if (inventory > 0) {
			return isFullSpecialCube();
		}
		return isFull();
	}

	public int getFreeSlots(int inventory) {
		if (inventory > 0) {
			return getSpecialCubeFreeSlots();
		}
		return getFreeSlots();
	}

	public int getSpecialCubeFreeSlots() {
		return this.itemStorage.getSpecialCubeFreeSlots();
	}

	@Override
	public int getFreeSlots() {
		return this.itemStorage.getFreeSlots();
	}

	public boolean setLimit(int limit) {
		return this.itemStorage.setLimit(limit);
	}

	@Override
	public int getLimit() {
		return this.itemStorage.getLimit();
	}

	@Override
	public final PersistentState getPersistentState() {
		return persistentState;
	}

	@Override
	public final void setPersistentState(PersistentState persistentState) {
		this.persistentState = persistentState;
	}

	@Override
	public int size() {
		return itemStorage.size();
	}

	public void clear() {
		for (Item i : itemStorage.getItems()) {
			remove(i);
		}
	}

}
