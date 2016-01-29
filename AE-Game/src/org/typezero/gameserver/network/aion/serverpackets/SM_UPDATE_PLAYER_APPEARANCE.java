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
import org.typezero.gameserver.model.items.GodStone;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Avol modified by ATracer
 */
public class SM_UPDATE_PLAYER_APPEARANCE extends AionServerPacket {

	public int playerId;
	public int size;
	public List<Item> items;

	public SM_UPDATE_PLAYER_APPEARANCE(int playerId, List<Item> items) {
		this.playerId = playerId;
		this.items = items;
		this.size = items.size();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(playerId);

		int mask = 0;
		for (Item item : items) {
			if (item.getItemTemplate().isTwoHandWeapon()) {
				ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
				mask |= slots[0].getSlotIdMask();
			}
			else {
				mask |= item.getEquipmentSlot();
			}
		}

		writeD(mask); // Wrong !!! It's item count, but doesn't work

		for (Item item : items) {
			writeD(item.getItemSkinTemplate().getTemplateId());
			GodStone godStone = item.getGodStone();
			writeD(godStone != null ? godStone.getItemId() : 0);
			writeD(item.getItemColor());
            if (item.getAuthorize() > 0 && item.getItemTemplate().isAccessory()){
                if (item.getItemTemplate().isPlume()) {
                    float aLvl = item.getAuthorize() / 5;
                    if (item.getAuthorize() >= 5){
                        aLvl = aLvl > 2.0f ? 2.0f : aLvl;
                        writeD((int) aLvl << 3);
                    }else{
                        writeD(0);
                    }
                }else{
                    writeD(item.getAuthorize() >= 5 ? 2 : 0);
                }
            }else{
                if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isTwoHandWeapon()){
                    writeD(item.getEnchantLevel() == 15 ? 2 : item.getEnchantLevel() >= 20 ? 4 : 0);
                }else{
                    writeD(0);
                }
            }
        }
    }
}
