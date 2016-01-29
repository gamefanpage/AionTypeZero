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

import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;
import org.typezero.gameserver.services.RepurchaseService;

/**
 * @author xTz, KID
 */
public class SM_REPURCHASE extends AionServerPacket {

	private Player player;
	private final int targetObjectId;
	private final Collection<Item> items;

	public SM_REPURCHASE(Player player, int npcId) {
		this.player = player;
		this.targetObjectId = npcId;
		items = RepurchaseService.getInstance().getRepurchaseItems(player.getObjectId());
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeD(1);
		writeH(items.size());

		for (Item item : items) {
			ItemTemplate itemTemplate = item.getItemTemplate();

			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeNameId(itemTemplate.getNameId());

			ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());

			writeQ(item.getRepurchasePrice());
		}
	}
}
