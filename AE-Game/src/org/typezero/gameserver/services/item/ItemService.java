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

import java.util.Collection;
import java.util.List;

import org.typezero.gameserver.network.aion.serverpackets.SM_EVENT_BUFF;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.dao.ItemStoneListDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Equipment;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemId;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.ArmorType;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.quest.QuestItems;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemAddType;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

/**
 * @author KID
 */
public class ItemService {

	private static final Logger log = LoggerFactory.getLogger("ITEM_LOG");
    public static final ItemUpdatePredicate DEFAULT_UPDATE_PREDICATE = new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);

	public static void loadItemStones(Collection<Item> itemList) {
		if (itemList != null && itemList.size() > 0) {
			DAOManager.getDAO(ItemStoneListDAO.class).load(itemList);
		}
	}

	public static long addItem(Player player, int itemId, long count) {
		return addItem(player, itemId, count, DEFAULT_UPDATE_PREDICATE);
	}

    public static long addItem(Player player, int itemId, long count, ItemUpdatePredicate predicate) {
        return addItem(player, itemId, count, null, predicate);
    }

	/**
	 * Add new item based on all sourceItem values
	 */
	public static long addItem(Player player, Item sourceItem) {
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, DEFAULT_UPDATE_PREDICATE);
	}

    public static long addItem(Player player, Item sourceItem, ItemUpdatePredicate predicate) {
        return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, predicate);
    }

	public static long addItem(Player player, int itemId, long count, Item sourceItem) {
		return addItem(player, itemId, count, sourceItem, DEFAULT_UPDATE_PREDICATE);
	}

	/**
	 * Add new item based on sourceItem values
	 */
	public static long addItem(Player player, int itemId, long count, Item sourceItem, ItemUpdatePredicate predicate) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (count <= 0 || itemTemplate == null) {
			return 0;
		}
		Preconditions.checkNotNull(itemTemplate, "No item with id " + itemId);
		Preconditions.checkNotNull(predicate, "Predicate is not supplied");

		if (LoggingConfig.LOG_ITEM) {
			log.info("[ITEM] ID/Count"
				+ (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + itemTemplate.getTemplateId() + "/" + count + "/"
					+ itemTemplate.getName() : " - " + itemTemplate.getTemplateId() + "/" + count) + " to player " + player.getName());
		}

		Storage inventory = player.getInventory();
		if (itemTemplate.isKinah()) {
			inventory.increaseKinah(count);
			return 0;
		}

		if (itemTemplate.isStackable()) {
			count = addStackableItem(player, itemTemplate, count, predicate);
		}
		else {
			count = addNonStackableItem(player, itemTemplate, count, sourceItem, predicate);
		}

		if (inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
		}
        if ((itemTemplate.isNewPlayerBuffItem()) || (itemTemplate.isOldPlayerBuffItem()) || (itemTemplate.isEventBuffItem())) {
            player.updataItemEffectId();
            PacketSendUtility.broadcastPacket(player, new SM_EVENT_BUFF(player, player.getItemEffectId()), true);
            PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
        }
		return count;
	}

	/**
	 * Add non-stackable item to inventory
	 */
	private static long addNonStackableItem(Player player, ItemTemplate itemTemplate, long count, Item sourceItem, ItemUpdatePredicate predicate) {
		Storage inventory = player.getInventory();
		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());

			if (newItem.getExpireTime() != 0) {
				ExpireTimerTask.getInstance().addTask(newItem, player);
			}
			if (sourceItem != null) {
				copyItemInfo(sourceItem, newItem);
			}
			predicate.changeItem(newItem);
			inventory.add(newItem);
			count--;
		}
		return count;
	}

	/**
	 * Copy some item values like item stones and enchange level
	 */
	private static void copyItemInfo(Item sourceItem, Item newItem) {
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		if (sourceItem.hasManaStones()) {
			for (ManaStone manaStone : sourceItem.getItemStones()) {
				ItemSocketService.addManaStone(newItem, manaStone.getItemId());
			}
		}
		if (sourceItem.getGodStone() != null) {
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		}
		if (sourceItem.getEnchantLevel() > 0) {
			newItem.setEnchantLevel(sourceItem.getEnchantLevel());
		}
		if (sourceItem.isSoulBound()) {
			newItem.setSoulBound(true);
		}
		newItem.setBonusNumber(sourceItem.getBonusNumber());
		newItem.setRandomStats(sourceItem.getRandomStats());
		newItem.setIdianStone(sourceItem.getIdianStone());
		newItem.setItemColor(sourceItem.getItemColor());
		newItem.setItemSkinTemplate(sourceItem.getItemSkinTemplate());
	}

	/**
	 * Add stackable item to inventory
	 */
	private static long addStackableItem(Player player, ItemTemplate itemTemplate, long count, ItemUpdatePredicate predicate) {
		Storage inventory = player.getInventory();
		Collection<Item> items = inventory.getItemsByItemId(itemTemplate.getTemplateId());
		for (Item item : items) {
			if (count == 0) {
				break;
			}
			count = inventory.increaseItemCount(item, count, predicate.getUpdateType(item, true));
		}

		// dirty & hacky check for arrows and shards...
		if (itemTemplate.getCategory() != ItemCategory.SHARD || itemTemplate.getArmorType() == ArmorType.ARROW) {
			Equipment equipement = player.getEquipment();
			items = equipement.getEquippedItemsByItemId(itemTemplate.getTemplateId());
			for (Item item : items) {
				if (count == 0) {
					break;
				}
				count = equipement.increaseEquippedItemCount(item, count);
			}
		}

		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId(), count);
			count -= newItem.getItemCount();
			inventory.add(newItem);
		}
		return count;
	}

	public static boolean addQuestItems(Player player, List<QuestItems> questItems) {
		int slotReq = 0, specialSlot = 0;

		for (QuestItems qi : questItems) {
			if (qi.getItemId() != ItemId.KINAH.value() && qi.getCount() != 0) {
				ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(qi.getItemId());
				long stackCount = template.getMaxStackCount();
				long count = qi.getCount() / stackCount;
				if (qi.getCount() % stackCount != 0)
					count++;
				if (template.getExtraInventoryId() > 0) {
					specialSlot += count;
				}
				else {
					slotReq += count;
				}
			}
		}
		Storage inventory = player.getInventory();
		if (slotReq > 0 && inventory.getFreeSlots() < slotReq) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
			return false;
		}
		if (specialSlot > 0 && inventory.getSpecialCubeFreeSlots() < specialSlot) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
			return false;
		}
		for (QuestItems qi : questItems) {
			addItem(player, qi.getItemId(), qi.getCount());
		}
		return true;
	}

	public static void releaseItemId(Item item) {
		IDFactory.getInstance().releaseId(item.getObjectId());
	}

	public static void releaseItemIds(Collection<Item> items) {
		Collection<Integer> idIterator = Collections2.transform(items, AionObject.OBJECT_TO_ID_TRANSFORMER);
		IDFactory.getInstance().releaseIds(idIterator);
	}

	public static boolean dropItemToInventory(int playerObjectId, int itemId) {
		return dropItemToInventory(World.getInstance().findPlayer(playerObjectId), itemId);
	}

    public static class ItemUpdatePredicate {

        private final ItemUpdateType itemUpdateType;
        private final ItemAddType itemAddType;

        public ItemUpdatePredicate(ItemAddType itemAddType, ItemUpdateType itemUpdateType) {
            this.itemUpdateType = itemUpdateType;
            this.itemAddType = itemAddType;
        }

        public ItemUpdatePredicate() {
            this(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);
        }

        public ItemUpdateType getUpdateType(Item item, boolean isIncrease) {
            if (item.getItemTemplate().isKinah()) {
                return ItemUpdateType.getKinahUpdateTypeFromAddType(itemAddType, isIncrease);
            }
            return itemUpdateType;
        }

        public ItemAddType getAddType() {
            return itemAddType;
        }

        /**
         * @param item
         */
        public boolean changeItem(Item item) {
            return true;
        }
    }

	public static boolean dropItemToInventory(Player player, int itemId) {
		if (player == null || !player.isOnline())
			return false;

		Storage storage = player.getInventory();
		if (storage.getFreeSlots() < 1) {
			List<Item> items = storage.getItemsByItemId(itemId);
			boolean hasFreeStack = false;
			for (Item item : items) {
				if (item.getPersistentState() == PersistentState.DELETED || item.getItemCount() < item.getItemTemplate().getMaxStackCount()) {
					hasFreeStack = true;
					break;
				}
			}
			if (!hasFreeStack)
				return false;
		}
		return addItem(player, itemId, 1) == 0;
	}

	public static boolean checkRandomTemplate(int randomItemId) {
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(randomItemId);
		return template != null;
	}

}
