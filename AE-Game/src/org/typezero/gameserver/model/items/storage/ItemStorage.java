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

import static ch.lambdaj.Lambda.*;
import org.typezero.gameserver.model.gameobjects.Item;
import java.util.List;
import javolution.util.FastList;
import javolution.util.FastMap;
import static org.hamcrest.Matchers.*;

/**
 * @author KID
 */
public class ItemStorage {

	public static final long FIRST_AVAILABLE_SLOT = 65535L;

	private FastMap<Integer, Item> items;
	private int limit;
	private int specialLimit;

	public ItemStorage(int limit, int specialLimit) {
		this.limit = limit;
		this.specialLimit = specialLimit;
		this.items = FastMap.newInstance();
	}

	public FastList<Item> getItems() {
		FastList<Item> temp = FastList.newInstance();
		temp.addAll(items.values());
		return temp;
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean setLimit(int limit) {
		if (getCubeItems().size() > limit) {
			return false;
		}

		this.limit = limit;
		return true;
	}

	public Item getFirstItemById(int itemId) {
		for (Item item : items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				return item;
			}
		}
		return null;
	}

	public FastList<Item> getItemsById(int itemId) {
		FastList<Item> temp = FastList.newInstance();
		for (Item item : items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				temp.add(item);
			}
		}
		return temp;
	}

	public Item getItemByObjId(int itemObjId) {
		return this.items.get(itemObjId);
	}

	public long getSlotIdByItemId(int itemId) {
		for (Item item : this.items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				return item.getEquipmentSlot();
			}
		}
		return -1;
	}

	public Item getItemBySlotId(short slotId) {
		for (Item item : getCubeItems()) {
			if (item.getEquipmentSlot() == slotId) {
				return item;
			}
		}
		return null;
	}

	public Item getSpecialItemBySlotId(short slotId) {
		for (Item item : getSpecialCubeItems()) {
			if (item.getEquipmentSlot() == slotId) {
				return item;
			}
		}
		return null;
	}

	public long getSlotIdByObjId(int objId) {
		Item item = this.getItemByObjId(objId);
		if (item != null)
			return item.getEquipmentSlot();
		else
			return -1;
	}

	public long getNextAvailableSlot() {
		return FIRST_AVAILABLE_SLOT;
	}

	public boolean putItem(Item item) {
		if (this.items.containsKey(item.getObjectId()))
			return false;

		this.items.put(item.getObjectId(), item);
		return true;
	}

	public Item removeItem(int objId) {
		return this.items.remove(objId);
	}

	public boolean isFull() {
		return getCubeItems().size() >= limit;
	}

	public boolean isFullSpecialCube() {
		return getSpecialCubeItems().size() >= specialLimit;
	}

	public List<Item> getSpecialCubeItems() {
		return select(items.values(), having(on(Item.class).getItemTemplate().getExtraInventoryId(), greaterThan(0)));
	}

	public List<Item> getCubeItems() {
		return select(items.values(), having(on(Item.class).getItemTemplate().getExtraInventoryId(), lessThan(1)));
	}

	public int getFreeSlots() {
		return limit - getCubeItems().size();
	}

	public int getSpecialCubeFreeSlots() {
		return specialLimit - getSpecialCubeItems().size();
	}

	public int size() {
		return this.items.size();
	}

}
