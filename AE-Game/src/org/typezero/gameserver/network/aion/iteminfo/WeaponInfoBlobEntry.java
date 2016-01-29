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

package org.typezero.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for weapons. It keeps info about slots that weapon can be equipped to.
 *
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry {

	WeaponInfoBlobEntry() {
		super(ItemBlobType.SLOTS_WEAPON);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		ItemSlot[] slots = ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot());
		if (slots.length == 1) {
			writeQ(buf, slots[0].getSlotIdMask());
			writeQ(buf, item.hasFusionedItem() ? 0x00 : 0x02);
			return;
		}
		if (item.getItemTemplate().isTwoHandWeapon()) {
			// must occupy two slots
			writeQ(buf, slots[0].getSlotIdMask() | slots[1].getSlotIdMask());
			writeQ(buf, 0);
		}
		else {
			// primary and secondary slots
			writeQ(buf, slots[0].getSlotIdMask());
			writeQ(buf, slots[1].getSlotIdMask());
		}
	}

	@Override
	public int getSize() {
		return 16;
	}
}
