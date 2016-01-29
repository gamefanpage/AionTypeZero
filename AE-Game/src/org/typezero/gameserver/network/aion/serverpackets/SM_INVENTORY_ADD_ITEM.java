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

import java.util.List;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.ItemStorage;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * @author ATracer
 */
public class SM_INVENTORY_ADD_ITEM extends AionServerPacket {

	private final List<Item> items;
	private final int size;
	private Player player;

	public SM_INVENTORY_ADD_ITEM(List<Item> items, Player player) {
		this.player = player;
		this.items = items;
		this.size = items.size();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		//TODO! why its not use ItemAddType!?
		// 0x1C after buy, 0x35 after quest, 0x40 questionnaire;
		int mask = (size == 1 && items.get(0).getEquipmentSlot() != ItemStorage.FIRST_AVAILABLE_SLOT) ? 0x07 : 0x19;
		writeH(mask); //
		writeH(size); // number of entries
		for (Item item : items)
			writeItemInfo(item);
	}

	private void writeItemInfo(Item item) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());

		writeH(-1);
		writeC(item.getItemTemplate().isCloth() ? 1 : 0);
	}
}
