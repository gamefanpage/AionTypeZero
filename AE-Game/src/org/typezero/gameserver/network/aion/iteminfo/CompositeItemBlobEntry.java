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
import java.util.HashSet;
import java.util.Set;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sending info about the item that were fused with current item.
 *
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class CompositeItemBlobEntry extends ItemBlobEntry {

	CompositeItemBlobEntry() {
		super(ItemBlobType.COMPOSITE_ITEM);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeD(buf, item.getFusionedItemId());
		writeFusionStones(buf);
		writeH(buf, item.hasOptionalFusionSocket() ? item.getOptionalFusionSocket() : 0x00);
	}

	private void writeFusionStones(ByteBuffer buf) {
		Item item = ownerItem;

        Set<ManaStone> specialStone = new HashSet<ManaStone>();
        Set<ManaStone> normalStone = new HashSet<ManaStone>();

        if (item.hasFusionStones()) {
            Set<ManaStone> itemStones = item.getFusionStones();

            for (ManaStone ms : itemStones)
                if (ms.isAncient())
                    specialStone.add(ms);
                else
                    normalStone.add(ms);

            for (ManaStone sms : specialStone)
                writeD(buf, sms.getItemId());

            if (specialStone.size() < item.getFusionedItemTemplate().getSpecialSlots())
                skip(buf, (item.getFusionedItemTemplate().getSpecialSlots() - specialStone.size()) * 4);

            for (ManaStone nms : normalStone)
                writeD(buf, nms.getItemId());

            skip(buf, (6 - item.getFusionedItemTemplate().getSpecialSlots() - normalStone.size()) * 4);
		}
		else {
			skip(buf, 24);
		}
	}

	@Override
	public int getSize() {
		return 12 * 2 + 6;
	}
}
