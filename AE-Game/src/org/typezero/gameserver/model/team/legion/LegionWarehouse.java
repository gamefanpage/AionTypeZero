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

package org.typezero.gameserver.model.team.legion;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author Simple
 */
public class LegionWarehouse extends Storage {

	private Legion legion;
	private int curentWhUser;

	public LegionWarehouse(Legion legion) {
		super(StorageType.LEGION_WAREHOUSE);
		this.legion = legion;
		this.setLimit(legion.getWarehouseSlots());
	}

	public Legion getLegion() {
		return this.legion;
	}

	public void setOwnerLegion(Legion legion) {
		this.legion = legion;
	}

	@Override
	public void increaseKinah(long amount) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public void increaseKinah(long amount, ItemUpdateType updateType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public boolean tryDecreaseKinah(long amount) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public void decreaseKinah(long amount) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public void decreaseKinah(long amount, ItemUpdateType updateType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public long increaseItemCount(Item item, long count) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public long increaseItemCount(Item item, long count, ItemUpdateType updateType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public long decreaseItemCount(Item item, long count) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public Item add(Item item) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public Item put(Item item) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public Item delete(Item item) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public Item delete(Item item, ItemDeleteType deleteType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public boolean decreaseByItemId(int itemId, long count) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType) {
		throw new UnsupportedOperationException("LWH should be used behind proxy");
	}

	@Override
	public void setOwner(Player player) {
		throw new UnsupportedOperationException("LWH doesnt have owner");
	}

	public void setWhUser(int curentWhUser) {
		this.curentWhUser = curentWhUser;
	}

	public int getWhUser() {
		return curentWhUser;
	}
}
