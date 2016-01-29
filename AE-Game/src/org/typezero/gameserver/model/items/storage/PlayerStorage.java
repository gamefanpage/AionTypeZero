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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author ATracer
 */
public class PlayerStorage extends Storage {

	private Player actor;

	/**
	 * @param storageType
	 */
	public PlayerStorage(StorageType storageType) {
		super(storageType);
	}

	@Override
	public final void setOwner(Player actor) {
		this.actor = actor;
	}

	public void onLoadHandler(Item item) {
		if (item.isEquipped())
			actor.getEquipment().onLoadHandler(item);
		else {
			super.onLoadHandler(item);
		}
	}

	@Override
	public void increaseKinah(long amount) {
		increaseKinah(amount, actor);
	}

	@Override
	public void increaseKinah(long amount, ItemUpdateType updateType) {
		increaseKinah(amount, updateType, actor);
	}

	@Override
	public boolean tryDecreaseKinah(long amount) {
		return tryDecreaseKinah(amount, actor);
	}

	@Override
	public void decreaseKinah(long amount) {
		decreaseKinah(amount, actor);
	}

	@Override
	public void decreaseKinah(long amount, ItemUpdateType updateType) {
		decreaseKinah(amount, updateType, actor);
	}

	@Override
	public long increaseItemCount(Item item, long count) {
		return increaseItemCount(item, count, actor);
	}

	@Override
	public long increaseItemCount(Item item, long count, ItemUpdateType updateType) {
		return increaseItemCount(item, count, updateType, actor);
	}

	@Override
	public long decreaseItemCount(Item item, long count) {
		return decreaseItemCount(item, count, actor);
	}

	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType) {
		return decreaseItemCount(item, count, updateType, actor);
	}

	@Override
	public Item add(Item item) {
		return add(item, actor);
	}

	@Override
	public Item put(Item item) {
		return put(item, actor);
	}

	@Override
	public Item delete(Item item) {
		return delete(item, actor);
	}

	@Override
	public Item delete(Item item, ItemDeleteType deleteType) {
		return delete(item, deleteType, actor);
	}

	@Override
	public boolean decreaseByItemId(int itemId, long count) {
		return decreaseByItemId(itemId, count, actor);
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count) {
		return decreaseByObjectId(itemObjId, count, actor);
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType) {
		return decreaseByObjectId(itemObjId, count, updateType, actor);
	}

}
