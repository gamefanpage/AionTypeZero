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

import java.util.LinkedHashMap;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PrivateStore;
import org.typezero.gameserver.model.trade.TradePSItem;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * @author Simple
 */
public class SM_PRIVATE_STORE extends AionServerPacket {

	private Player player;
	/** Private store Information **/
	private PrivateStore store;

	public SM_PRIVATE_STORE(PrivateStore store, Player player) {
		this.player = player;
		this.store = store;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (store != null) {
			Player storePlayer = store.getOwner();
			LinkedHashMap<Integer, TradePSItem> soldItems = store.getSoldItems();

			writeD(storePlayer.getObjectId());
			writeH(soldItems.size());
			for (Integer itemObjId : soldItems.keySet()) {
				Item item = storePlayer.getInventory().getItemByObjId(itemObjId);
				TradePSItem tradeItem = store.getTradeItemByObjId(itemObjId);
				long price = tradeItem.getPrice();
				writeD(itemObjId);
				writeD(item.getItemTemplate().getTemplateId());
				writeH((int) tradeItem.getCount());
				writeQ((int) price);

				ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				itemInfoBlob.writeMe(getBuf());
			}
		}
	}
}
