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

import org.typezero.gameserver.model.drop.Drop;
import java.util.Set;

import javolution.util.FastList;

import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author alexa026, Avol, Corrected by Metos modified by ATracer, KID
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket {

	private int targetObjectId;
	private FastList<DropItem> dropItems;

	public SM_LOOT_ITEMLIST(int targetObjectId, Set<DropItem> setItems, Player player) {
		this.targetObjectId = targetObjectId;
		this.dropItems = new FastList<DropItem>();
		if (setItems == null) {
			LoggerFactory.getLogger(SM_LOOT_ITEMLIST.class).warn("null Set<DropItem>, skip");
			return;
		}

		for (DropItem item : setItems) {
			if (item.getPlayerObjId() == 0 || player.getObjectId() == item.getPlayerObjId())
				dropItems.add(item);
		}
	}

	/**
	 * {@inheritDoc} dc
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeC(dropItems.size());

		for (DropItem dropItem : dropItems) {
			Drop drop = dropItem.getDropTemplate();
			writeC(dropItem.getIndex()); // index in droplist
			writeD(drop.getItemId());
			writeD((int) dropItem.getCount());
			writeH(0);
			writeC(0); // 3.5
			ItemTemplate template = drop.getItemTemplate();
			writeC(!template.getCategory().equals(ItemCategory.QUEST) && !template.isTradeable() ? 1 : 0);
		}
		FastList.recycle(dropItems);
	}

}
