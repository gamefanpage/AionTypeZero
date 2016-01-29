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
import java.util.Set;

import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.ItemStoneListDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.GodstoneInfo;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ItemSocketService {

	private static final Logger log = LoggerFactory.getLogger(ItemSocketService.class);

	public static ManaStone addManaStone(Item item, int itemId) {
		if (item == null)
			return null;

		Set<ManaStone> manaStones = item.getItemStones();
        if (manaStones.size() > item.getSockets(false)) {
            return null;
        }

        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        int nextSlot = -1;

        // new slot number for special manastone
        int newSpecial = -1;
        if (item.getSpecialSlots() > 0)
            for (int i = 0; i < item.getSpecialSlots(); i++) {
                boolean found = false;
                for (ManaStone ms : manaStones) {
                    if (ms.getSlot() == i) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newSpecial = i;
                    break;
                }
            }

        // new slot for manastone
        int newNormal = -1;
        for (int i = item.getSpecialSlots(); i < item.getSockets(false); i++) {
            boolean found = false;
            for (ManaStone ms : manaStones) {
                if (ms.getSlot() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newNormal = i;
                break;
            }
        }

        boolean slotFound = true;
        if (itemTemplate.getCategory() == ItemCategory.ANCIENT_MANASTONE) {
            if (newSpecial >= 0)
                nextSlot = newSpecial;
            else
                slotFound = false;
        } else {
            if (newNormal >= 0)
                nextSlot = newNormal;
            else
                slotFound = false;
        }

        if (!slotFound)
            return null;

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);

		return stone;
	}

	public static ManaStone addManaStone(Item item, int itemId, int slotId) {
		if (item == null)
			return null;

		Set<ManaStone> manaStones = item.getItemStones();
		// temp fix for manastone spam till templates are updated
        if (manaStones.size() > item.getSockets(false)) {
            return null;
        }

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}

	public static void copyFusionStones(Item source, Item target) {
		if (source.hasManaStones()) {
			for (ManaStone manaStone : source.getItemStones()) {
				target.getFusionStones().add(
					new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
		}
	}

	public static ManaStone addFusionStone(Item item, int itemId) {
		if (item == null)
			return null;

		Set<ManaStone> fusionStones = item.getFusionStones();
		if (fusionStones.size() > item.getSockets(true))
			return null;

        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        ItemTemplate itemTemplate2 = item.getFusionedItemTemplate();
        int nextSlot = -1;
        // new slot number for special manastone
        int newSpecial = -1;
        if (itemTemplate2.getSpecialSlots() > 0)
            for (int i = 0; i < itemTemplate2.getSpecialSlots(); i++) {
                boolean found = false;
                for (ManaStone ms : fusionStones) {
                    if (ms.getSlot() == i) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newSpecial = i;
                    break;
                }
            }

        // new slot for manastone
        int newNormal = -1;
        for (int i = itemTemplate2.getSpecialSlots(); i < item.getSockets(true); i++) {
            boolean found = false;
            for (ManaStone ms : fusionStones) {
                if (ms.getSlot() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newNormal = i;
                break;
            }
        }

        boolean slotFound = true;
        if (itemTemplate.getCategory() == ItemCategory.ANCIENT_MANASTONE) {
            if (newSpecial >= 0)
                nextSlot = newSpecial;
            else
                slotFound = false;
        } else {
            if (newNormal >= 0)
                nextSlot = newNormal;
            else
                slotFound = false;
        }

        if (!slotFound)
            return null;

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		fusionStones.add(stone);
		return stone;
	}

	public static ManaStone addFusionStone(Item item, int itemId, int slotId) {
		if (item == null)
			return null;

		Set<ManaStone> fusionStones = item.getFusionStones();
		if (fusionStones.size() > item.getSockets(true))
			return null;

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		fusionStones.add(stone);
		return stone;
	}

	public static void removeManastone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasManaStones()) {
			log.warn("Item stone list is empty");
			return;
		}

		Set<ManaStone> itemStones = item.getItemStones();

        if (itemStones.size() <= slotNum)
            return;

		int counter = 0;
		for (ManaStone ms : itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(Collections.singleton(ms));
				itemStones.remove(ms);
                break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeFusionstone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasFusionStones()) {
			log.warn("Item stone list is empty");
			return;
		}

		Set<ManaStone> itemStones = item.getFusionStones();

        if (itemStones.size() <= slotNum)
            return;

		int counter = 0;
		for (ManaStone ms : itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStone(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeAllManastone(Player player, Item item) {
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasManaStones()) {
			return;
		}

		Set<ManaStone> itemStones = item.getItemStones();
		for (ManaStone ms : itemStones) {
			ms.setPersistentState(PersistentState.DELETED);
		}
		DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(itemStones);
		itemStones.clear();

		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeAllFusionStone(Player player, Item item) {
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasFusionStones()) {
			return;
		}

		Set<ManaStone> fusionStones = item.getFusionStones();
		for (ManaStone ms : fusionStones) {
			ms.setPersistentState(PersistentState.DELETED);
		}
		DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStone(fusionStones);
		fusionStones.clear();

		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

    public static void socketGodstone(Player player, int weaponIdObj, int stoneId) {
        Item weaponItem = player.getInventory().getItemByObjId(weaponIdObj);

        if (weaponItem == null) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_CANNOT_GIVE_PROC_TO_EQUIPPED_ITEM);
            return;
        }

        if (!weaponItem.canSocketGodstone()) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_NOT_ADD_PROC(new DescriptionId(weaponItem.getNameId())));
        }

        Item godstone = player.getInventory().getFirstItemByItemId(stoneId);

        int godStoneItemId = godstone.getItemTemplate().getTemplateId();
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(godStoneItemId);
        GodstoneInfo godstoneInfo = itemTemplate.getGodstoneInfo();

        if (godstoneInfo == null) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_NO_PROC_GIVE_ITEM);
            log.warn("Godstone info missing for itemid " + godStoneItemId);
            return;
        }

        if (!player.getInventory().decreaseByItemId(stoneId, 1)) {
            return;
        }

        weaponItem.addGodStone(godStoneItemId);
        PacketSendUtility.sendPacket(player,
                SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_ENCHANTED_TARGET_ITEM(new DescriptionId(weaponItem.getNameId())));

        ItemPacketService.updateItemAfterInfoChange(player, weaponItem);
    }
}
