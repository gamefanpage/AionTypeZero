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

package org.typezero.gameserver.model.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public class Exchange {

	private Player activeplayer;
	private Player targetPlayer;

	private boolean confirmed;
	private boolean locked;

	private long kinahCount;

	private Map<Integer, ExchangeItem> items = new HashMap<Integer, ExchangeItem>();
	private List<Item> itemsToUpdate = FastList.newInstance();

	public Exchange(Player activeplayer, Player targetPlayer) {
		super();
		this.activeplayer = activeplayer;
		this.targetPlayer = targetPlayer;
	}

	public void confirm() {
		confirmed = true;
	}

	/**
	 * @return the confirmed
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	public void lock() {
		this.locked = true;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param exchangeItem
	 */
	public void addItem(int parentItemObjId, ExchangeItem exchangeItem) {
		this.items.put(parentItemObjId, exchangeItem);
	}

	/**
	 * @param countToAdd
	 */
	public void addKinah(long countToAdd) {
		this.kinahCount += countToAdd;
	}

	/**
	 * @return the activeplayer
	 */
	public Player getActiveplayer() {
		return activeplayer;
	}

	/**
	 * @return the targetPlayer
	 */
	public Player getTargetPlayer() {
		return targetPlayer;
	}

	/**
	 * @return the kinahCount
	 */
	public long getKinahCount() {
		return kinahCount;
	}

	/**
	 * @return the items
	 */
	public Map<Integer, ExchangeItem> getItems() {
		return items;
	}

	public boolean isExchangeListFull() {
		return items.size() > 18;
	}

	/**
	 * @return the itemsToUpdate
	 */
	public List<Item> getItemsToUpdate() {
		return itemsToUpdate;
	}

	/**
	 * @param item
	 */
	public void addItemToUpdate(Item item) {
		itemsToUpdate.add(item);
	}
}
