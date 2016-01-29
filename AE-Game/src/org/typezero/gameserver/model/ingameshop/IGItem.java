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

package org.typezero.gameserver.model.ingameshop;

/**
 * @author xTz
 */
public class IGItem {

	private int objectId;
	private int itemId;
	private long itemCount;
	private long itemPrice;
	private byte category;
	private byte subCategory;
	private int list;
	private int salesRanking;
	private	byte itemType;
	private	byte gift;
	private String titleDescription;
	private String itemDescription;

	public IGItem(int objectId, int itemId, long itemCount, long itemPrice, byte category, byte subCategory, int list, int salesRanking,
		byte itemType, byte gift, String titleDescription, String itemDescription) {
		this.objectId = objectId;
		this.itemId = itemId;
		this.itemCount = itemCount;
		this.itemPrice = itemPrice;
		this.category = category;
		this.subCategory = subCategory;
		this.list = list;
		this.salesRanking = salesRanking;
		this.itemType = itemType;
		this.gift = gift;
		this.titleDescription = titleDescription;
		this.itemDescription = itemDescription;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getItemId() {
		return itemId;
	}

	public long getItemCount() {
		return itemCount;
	}

	public long getItemPrice() {
		return itemPrice;
	}

	public byte getCategory() {
		return category;
	}

	public byte getSubCategory() {
		return subCategory;
	}

	public int getList() {
		return list;
	}

	public int getSalesRanking() {
		return salesRanking;
	}

	public byte getItemType() {
		return itemType;
	}

	public byte getGift() {
		return gift;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getTitleDescription() {
		return titleDescription;
	}

	public void increaseSales() {
		salesRanking++;
	}
}
