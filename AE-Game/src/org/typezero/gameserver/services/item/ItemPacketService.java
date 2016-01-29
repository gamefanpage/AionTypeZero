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

package org.typezero.gameserver.services.item;

import java.util.Collections;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_WAREHOUSE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_ADD_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_WAREHOUSE_UPDATE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_WAREHOUSE_ADD_ITEM;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * TODO: <br>
 * 0x01 0000 0001 increase count by merge<br>
 * 0x06 0000 0110 decrease count after split, equip<br>
 * 0x16 0001 0110 decrease count by use item<br>
 * 0x19 0001 1001 increase count by looting<br>
 * 0x1A 0001 1010 increase kinah by loot<br>
 * 0x1D 0001 1101 decrease kinah<br>
 * 0x32 0011 0010 increase kinah by quest<br>
 *
 * @author ATracer
 */
public class ItemPacketService {

	public static enum ItemUpdateType {
		EQUIP_UNEQUIP(-1, false),	//internal usage only
		CHARGE(-2, false), //internal usage only
		INC_MERGE(0x01, true),
		INC_MERGE_KINAH(0x05, true),
		DEC_SPLIT(0x06, true),
		DEC_USE(0x16, true),
		INC_LOOT(0x19, true),
		INC_GATHER(0x19, true),
        INC_ITEM_COLLECT(0x19, true),
		INC_KINAH_LOOT(0x1A, true),
		DEC_KINAH(0x1D, true),
		INC_KINAH_QUEST(0x32, true),
        DEC_KINAH_FLY(0x4B, true), // teleport or fly
        INC_KINAH_SELL(0x20, true),
		DEC_PET_FOOD(0x5E, true),
        INC_PASSPORT_ADD(0x8A, true),
		DEFAULT(0x16, true);

		private final int mask;
		private final boolean	sendable;

		private ItemUpdateType(int mask, boolean sendable) {
			this.mask = mask;
			this.sendable = sendable;
		}

		public int getMask() {
			return mask;
		}

		public boolean isSendable() {
			return sendable;
		}

        public static ItemUpdateType getKinahUpdateTypeFromAddType(ItemAddType itemAddType, boolean isIncrease) {
            if (!isIncrease) {
                return ItemUpdateType.DEC_KINAH;
            }
            switch (itemAddType) {
                case BUY:
                    return ItemUpdateType.INC_KINAH_SELL;
                case ITEM_COLLECT:
                    return ItemUpdateType.INC_KINAH_LOOT;
                case QUEST:
                    return ItemUpdateType.INC_KINAH_QUEST;
                default:
                    return ItemUpdateType.INC_MERGE_KINAH;
            }
        }
	}

	public static enum ItemAddType {
		WITH_SLOT(0x07),
		PUT(0x13),
		BUY(0x1C),
        ITEM_COLLECT(0x19),
		QUEST(0x35),
		QUESTIONNAIRE(0x40);

		private final int mask;

		private ItemAddType(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	public static enum ItemDeleteType {
		UNKNOWN(0),
		SPLIT(0x04),
		MOVE(0x14),
		DISCARD(0x15),
		USE(0x17),
		REGISTER(0x78);

		private final int mask;

		private ItemDeleteType(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}

		public static final ItemDeleteType fromUpdateType(ItemUpdateType updateType) {
			switch (updateType) {
				case DEC_SPLIT:
					return SPLIT;
				default:
					return UNKNOWN;
			}
		}
	}

	public static final void updateItemAfterInfoChange(Player player, Item item) {
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
	}

	public static final void updateItemAfterEquip(Player player, Item item) {
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.EQUIP_UNEQUIP));
	}

	public static final void sendItemPacket(Player player, StorageType storageType, Item item, ItemUpdateType updateType) {
		if (item.getItemCount() <= 0 && !item.getItemTemplate().isKinah()) {
			sendItemDeletePacket(player, storageType, item, ItemDeleteType.fromUpdateType(updateType));
		}
		else {
			sendItemUpdatePacket(player, storageType, item, updateType);
		}
	}

	/**
	 * Item will be deleted from UI slot
	 */
	public static final void sendItemDeletePacket(Player player, StorageType storageType, Item item,
		ItemDeleteType deleteType) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId(), deleteType));
				break;
			default:
				PacketSendUtility.sendPacket(player, new SM_DELETE_WAREHOUSE_ITEM(storageType.getId(), item.getObjectId(),
					deleteType));
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}

	/**
	 * Item will be updated in UI slot (stacked items)
	 */
	public static final void sendItemUpdatePacket(Player player, StorageType storageType, Item item,
		ItemUpdateType updateType) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, updateType));
				break;
			case LEGION_WAREHOUSE:
				if (item.getItemTemplate().isKinah()) {
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			default:
				PacketSendUtility.sendPacket(player,
					new SM_WAREHOUSE_UPDATE_ITEM(player, item, storageType.getId(), updateType));
		}
	}

	/**
	 * New item will be displayed in storage
	 */
	public static final void sendStorageUpdatePacket(Player player, StorageType storageType, Item item) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_ADD_ITEM(Collections.singletonList(item), player));
				break;
			case LEGION_WAREHOUSE:
				if (item.getItemTemplate().isKinah()) {
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			default:
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_ADD_ITEM(item, storageType.getId(), player));
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}

}
