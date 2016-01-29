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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.LinkedHashMap;

import org.typezero.gameserver.model.trade.TradePSItem;

/**
 * @author Xav Modified by Simple
 */
public class PrivateStore {

	private Player owner;
	private LinkedHashMap<Integer, TradePSItem> items;
	private String storeMessage;

	/**
	 * This method binds a player to the store and creates a list of items
	 *
	 * @param owner
	 */
	public PrivateStore(Player owner) {
		this.owner = owner;
		this.items = new LinkedHashMap<Integer, TradePSItem>();
	}

	/**
	 * This method will return the owner of the store
	 *
	 * @return Player
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * This method will return the items being sold
	 *
	 * @return LinkedHashMap<Integer, TradePSItem>
	 */
	public LinkedHashMap<Integer, TradePSItem> getSoldItems() {
		return items;
	}

	/**
	 * This method will add an item to the list and price
	 *
	 * @param tradeList
	 * @param price
	 */
	public void addItemToSell(int itemObjId, TradePSItem tradeItem) {
		items.put(itemObjId, tradeItem);
	}

	/**
	 * This method will remove an item from the list
	 *
	 * @param item
	 */
	public void removeItem(int itemObjId) {
		if (items.containsKey(itemObjId)) {
			LinkedHashMap<Integer, TradePSItem> newItems = new LinkedHashMap<Integer, TradePSItem>();
			for (int itemObjIds : items.keySet()) {
				if (itemObjId != itemObjIds)
					newItems.put(itemObjIds, items.get(itemObjIds));
			}
			this.items = newItems;
		}
	}

	/**
	 * @param itemId
	 *          return tradeItem
	 */
	public TradePSItem getTradeItemByObjId(int itemObjId) {
		return items.get(itemObjId);
	}

	/**
	 * @param storeMessage
	 *          the storeMessage to set
	 */
	public void setStoreMessage(String storeMessage) {
		this.storeMessage = storeMessage;
	}

	/**
	 * @return the storeMessage
	 */
	public String getStoreMessage() {
		return storeMessage;
	}
}
