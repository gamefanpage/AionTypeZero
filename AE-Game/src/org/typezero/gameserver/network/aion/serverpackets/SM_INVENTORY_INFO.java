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

import java.util.Collections;
import java.util.List;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * In this packet Server is sending Inventory Info
 *
 * @author -Nemesiss-, alexa026, Avol ;d modified by ATracer, Rolandas
 */
public class SM_INVENTORY_INFO extends AionServerPacket {

	private boolean isFirstPacket;
    private boolean isPopUp;
	private int npcExpandsSize = 0;
	private int questExpandsSize = 0;

	private List<Item> items;
	private Player player;

	public SM_INVENTORY_INFO(boolean isFirstPacket, List<Item> items, int npcExpandsSize, int questExpandsSize, boolean isPopUp, Player player) {
		// this should prevent client crashes but need to discover when item is null
		items.removeAll(Collections.singletonList(null));
		this.isFirstPacket = isFirstPacket;
		this.items = items;
		this.npcExpandsSize = npcExpandsSize;
		this.questExpandsSize = questExpandsSize;
        this.isPopUp = isPopUp;
        this.player = player;
	}

	@Override
	protected void writeImpl(AionConnection con) {

		// something wrong with cube part.
		writeC(isFirstPacket ? 1 : 0);
		writeC(npcExpandsSize); // cube size from npc (so max 5 for now)
		writeC(questExpandsSize); // cube size from quest (so max 2 for now)
		writeC(isPopUp ? 1 : 0); // popup cube on login
		writeH(items.size()); // number of entries
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

		// invisible -1, visible is a slot
		writeH((int) (item.getEquipmentSlot() & 0xFFFF));

		// probably a right to equip the item, related to passive skill learn
		writeC(itemTemplate.isCloth() ? 1 : 0);
	}
}
