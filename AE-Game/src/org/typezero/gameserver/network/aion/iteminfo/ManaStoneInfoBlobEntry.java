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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.items.IdianStone;
import org.typezero.gameserver.model.items.ItemStone;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * This blob sends info about mana stones.
 *
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class ManaStoneInfoBlobEntry extends ItemBlobEntry {

	ManaStoneInfoBlobEntry() {
		super(ItemBlobType.MANA_SOCKETS);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeC(buf, item.isSoulBound() ? 1 : 0);
		writeC(buf, item.getEnchantLevel()); // enchant (1-15)
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeC(buf, item.getOptionalSocket());
		writeC(buf, 0);//maxEnchant

		writeItemStones(buf);

		ItemStone god = item.getGodStone();
		writeD(buf, god == null ? 0 : god.getItemId());

		int itemColor = item.getItemColor();
		int dyeExpiration = item.getColorTimeLeft();
		// expired dyed items
		if ((dyeExpiration > 0 && item.getColorExpireTime() > 0 || dyeExpiration == 0 && item.getColorExpireTime() == 0)
			&& item.getItemTemplate().isItemDyePermitted()) {
			writeC(buf, itemColor == 0 ? 0 : 1);
			writeD(buf, itemColor);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, dyeExpiration); // seconds until dye expires
		}
		else {
			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, 0);
		}
		IdianStone idianStone = item.getIdianStone();
		if (idianStone != null && idianStone.getPolishNumber()> 0) {
			writeD(buf, idianStone.getItemId() ); // Idian Stone template ID
			writeC(buf, idianStone.getPolishNumber()); // polish statset ID
		}
		else {
			writeD(buf, 0); // Idian Stone template ID
			writeC(buf, 0); // polish statset ID
		}

        writeC(buf, item.getAuthorize()); // unk 4.7
        writeH(buf, 0); // unk 4.7

        writePlumeStats(buf); // 64-bytes

        writeD(buf, 0);

        writeAmplification(buf); // 13-bytes
	}

    /**
     * Writes amplification data
     * @param item
     */
    private void writeAmplification(ByteBuffer buf) {
    	Item item = ownerItem;

    	//writeC(buf, item.isAmplified() ? 1 : 0);
    	writeC(buf, 0);//skillId
        //writeD(buf, item.getBuffSkill());
        writeD(buf, 0);//skillId

        writeD(buf, 0);//skillId

        writeD(buf, 0);//skillId
    }

    private void writePlumeStats(ByteBuffer buf) {
    	Item item = ownerItem;
    	if (item.getItemTemplate().isPlume()) {
    		writeD(buf, 0);//unk plume stat
            writeD(buf, 0);//value
    		writeD(buf, 0);//unk plume stat
            writeD(buf, 0);//value
            writeD(buf, 42);
            writeD(buf, item.getAuthorize() * 150);//HP Boost for Tempering Solution
            if (item.getItemTemplate().getAuthorizeName() == 52) {
                writeD(buf, 30);
                writeD(buf, item.getAuthorize() * 4);//Physical Attack
                writeD(buf, 0);//New Plume Stat 4.7.5.6 (NcSoft will implement it at future)
                writeD(buf, 0);//it's Value
            } else {
            	writeD(buf, 0);//New Plume Stat 4.7.5.6 (NcSoft will implement it at future)
            	writeD(buf, 0);//it's Value
                writeD(buf, 35);
                writeD(buf, item.getAuthorize() * 20);//Magic Boost
            }
            //Some Padding for future.
            writeD(buf, 0);//unk plume stat
            writeD(buf, 0);//value
            writeD(buf, 0);//unk plume stat
            writeD(buf, 0);//value
            writeD(buf, 0);//unk plume stat
            writeD(buf, 0);//value
        } else {
            writeB(buf, new byte[64]);
        }
    }

	/**
	 * Writes manastones
	 *
	 * @param buf
	 */
	private void writeItemStones(ByteBuffer buf) {
		Item item = ownerItem;
        Set<ManaStone> specialStone = new HashSet<ManaStone>();
        Set<ManaStone> normalStone = new HashSet<ManaStone>();

		if (item.hasManaStones()) {
			Set<ManaStone> itemStones = item.getItemStones();

            for (ManaStone ms : itemStones)
                if (ms.isAncient())
                    specialStone.add(ms);
                else
                    normalStone.add(ms);

            for (ManaStone sms : specialStone)
                writeD(buf, sms.getItemId());

            if (specialStone.size() < item.getSpecialSlots())
                skip(buf, (item.getSpecialSlots() - specialStone.size()) * 4);

            for (ManaStone nms : normalStone)
                writeD(buf, nms.getItemId());

            skip(buf, (6 - item.getSpecialSlots() - normalStone.size()) * 4);
		}
		else {
			skip(buf, 24);
		}
	}

	@Override
	public int getSize() {
        return 138;
	}
}
