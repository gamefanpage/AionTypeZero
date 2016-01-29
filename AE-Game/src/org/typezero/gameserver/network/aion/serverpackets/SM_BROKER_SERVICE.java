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

package org.typezero.gameserver.network.aion.serverpackets;

import java.sql.Timestamp;
import java.util.Calendar;

import org.typezero.gameserver.model.gameobjects.BrokerItem;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * @author IlBuono, kosyachok
 */
public class SM_BROKER_SERVICE extends AionServerPacket {

	private enum BrokerPacketType {
		SEARCHED_ITEMS(0),
		REGISTERED_ITEMS(1),
		REGISTER_ITEM(3),
		SHOW_SETTLED_ICON(5),
		SETTLED_ITEMS(5),
		REMOVE_SETTLED_ICON(6),
		AVE_LOW_HIGH_ITEM(7);

		private int id;

		private BrokerPacketType(int id) {
			this.id = id;
		}

		private int getId() {
			return id;
		}
	}

	private BrokerPacketType type;
	private BrokerItem[] brokerItems;
	private int itemsCount;
	private int startPage;
	private int message;
	private long settled_kinah;

	private int itemUniqueId;
	private long Ave7day;
	private long CurrentLow;
	private long CurrentHigh;
	private boolean IsLowHighSame;

	public SM_BROKER_SERVICE(BrokerItem brokerItem, int message , int itemsCount) {
		this.type = BrokerPacketType.REGISTER_ITEM;
		this.brokerItems = new BrokerItem[] { brokerItem };
		this.message = message;
		this.itemsCount = itemsCount;
	}

	public SM_BROKER_SERVICE(int message) {
		this.type = BrokerPacketType.REGISTER_ITEM;
		this.message = message;
	}

	public SM_BROKER_SERVICE(BrokerItem[] brokerItems) {
		this.type = BrokerPacketType.REGISTERED_ITEMS;
		this.brokerItems = brokerItems;
	}

	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, long settled_kinah) {
		this.type = BrokerPacketType.SETTLED_ITEMS;
		this.brokerItems = brokerItems;
		this.settled_kinah = settled_kinah;
	}

	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, int itemsCount, int startPage) {
		this.type = BrokerPacketType.SEARCHED_ITEMS;
		this.brokerItems = brokerItems;
		this.itemsCount = itemsCount;
		this.startPage = startPage;
	}

	public SM_BROKER_SERVICE(boolean showSettledIcon, long settled_kinah) {
		this.type = showSettledIcon ? BrokerPacketType.SHOW_SETTLED_ICON : BrokerPacketType.REMOVE_SETTLED_ICON;
		this.settled_kinah = settled_kinah;
	}

	public SM_BROKER_SERVICE(int itemUniqueId ,long Ave7day, long CurrentLow, long CurrentHigh, boolean IsLowHighSame) {
		this.type = BrokerPacketType.AVE_LOW_HIGH_ITEM;
		this.itemUniqueId = itemUniqueId;
		this.Ave7day = Ave7day;
		this.CurrentLow = CurrentLow;
		this.CurrentHigh = CurrentHigh;
		this.IsLowHighSame = IsLowHighSame;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		switch (type) {
			case SEARCHED_ITEMS:
				writeSearchedItems();
				break;
			case REGISTERED_ITEMS:
				writeRegisteredItems();
				break;
			case REGISTER_ITEM:
				writeRegisterItem();
				break;
			case SHOW_SETTLED_ICON:
				writeShowSettledIcon();
				break;
			case REMOVE_SETTLED_ICON:
				writeRemoveSettledIcon();
				break;
			case SETTLED_ITEMS:
				writeShowSettledItems();
				break;
			case AVE_LOW_HIGH_ITEM:
				writeItemAveLowHigh();
				break;
		}
	}

	private void writeItemAveLowHigh() {
		writeC(type.getId());
		writeC(0x00);
		writeD(itemUniqueId);
		writeQ(Ave7day);
		writeC(IsLowHighSame ? 0x02 : 0x00);
		writeQ(CurrentLow);
		writeQ(CurrentHigh);
	}

	private void writeSearchedItems() {
		writeC(type.getId());
		writeD(itemsCount);
		writeC(0);
		writeH(startPage);
		writeH(brokerItems.length);
		for (BrokerItem item : brokerItems) {
			writeItemInfo(item);
		}
	}

	private void writeRegisteredItems() {
		writeC(type.getId());
		writeD(0x00);
		writeH(brokerItems.length); // you can register a max of 15 items, so 0x0F
		for (BrokerItem brokerItem : brokerItems) {
			writeRegisteredItemInfo(brokerItem);
		}
	}

	private void writeRegisterItem() {
		writeC(type.getId());
		writeC(message);
		if (message == 0) {
			writeC(itemsCount + 1);
			BrokerItem itemForRegistration = brokerItems[0];
			writeRegisteredItemInfo(itemForRegistration);
		}
		else {
			writeB(new byte[107]);
		}
	}

	private void writeShowSettledIcon() {
		writeC(type.getId());
		writeQ(settled_kinah);
		writeD(0x00);
		writeH(0x00);
		writeH(0x01);
		writeC(0x00);
	}

	private void writeRemoveSettledIcon() {
		writeH(type.getId());
	}

	private void writeShowSettledItems() {
		writeC(type.getId());
		writeQ(settled_kinah);
		writeH(brokerItems.length);
		writeD(0x00);
		writeC(0x00);
		writeH(brokerItems.length);
		for (BrokerItem settledItem : brokerItems) {
			writeD(settledItem.getItemId());
			if (settledItem.isSold())
				writeQ(settledItem.getPrice());
			else
				writeQ(0);
			writeQ(settledItem.getItemCount());
			writeQ(settledItem.getItemCount());
			writeD((int) settledItem.getSettleTime().getTime() / 60000);

			//TODO! thats really odd - looks like getItem() may return null...
			Item item = settledItem.getItem();
			if(item != null)
				ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
			else
				writeB(new byte[54]);

			writeS(settledItem.getItemCreator());
		}
	}

	private void writeRegisteredItemInfo(BrokerItem brokerItem) {
		Item item = brokerItem.getItem();

		writeD(brokerItem.getItemUniqueId());
		writeD(brokerItem.getItemId());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());
		writeQ(item.getItemCount());
		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		int daysLeft = (int) ((brokerItem.getExpireTime().getTime() - currentTime.getTime()) / 86400000);
		writeC(daysLeft);
		writeC(0x01); //unk 4.5

		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());

		writeS(brokerItem.getItemCreator());
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.POLISH_INFO, null, item).writeThisBlob(getBuf());
	}

	private void writeItemInfo(BrokerItem brokerItem) {
		Item item = brokerItem.getItem();

		writeD(item.getObjectId());
		writeD(item.getItemTemplate().getTemplateId());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());

		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());

		writeS(brokerItem.getSeller());
		writeS(brokerItem.getItemCreator()); // creator
		writeC(0x01); //unk 4.5
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.POLISH_INFO, null, item).writeThisBlob(getBuf());
	}
}
