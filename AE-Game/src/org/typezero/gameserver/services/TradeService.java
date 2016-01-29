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

package org.typezero.gameserver.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.typezero.gameserver.model.templates.item.AcquisitionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.GoodsListData;
import org.typezero.gameserver.dataholders.TradeListData;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.limiteditems.LimitedItem;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.goods.GoodsList;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.TradeinItem;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;
import org.typezero.gameserver.model.trade.TradeItem;
import org.typezero.gameserver.model.trade.TradeList;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.item.ItemFactory;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.player.PlayerLimitService;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.OverfowException;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.SafeMath;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer, Rama, Wakizashi, xTz
 */
public class TradeService {

	private static final Logger log = LoggerFactory.getLogger(TradeService.class);
	private static final TradeListData tradeListData = DataManager.TRADE_LIST_DATA;
	private static final GoodsListData goodsListData = DataManager.GOODSLIST_DATA;

	/**
	 * @param player
	 * @param tradeList
	 * @return true or false
	 */
	public static boolean performBuyFromShop(Npc npc, Player player, TradeList tradeList) {
		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}

		if (!validateBuyItems(npc, tradeList, player)) {
			PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("NO_ITEM_TO_TRADE"));
			return false;
		}

		Storage inventory = player.getInventory();

		int tradeModifier = tradeListData.getTradeListTemplate(npc.getNpcId()).getSellPriceRate();

		// 1. check kinah
		if (!tradeList.calculateBuyListPrice(player, tradeModifier))
			return false;
        if (!tradeList.calculateRewardBuyListPrice(player))
			return false;

		// 2. check free slots, need to check retail behaviour
		int freeSlots = inventory.getFreeSlots();
		if (freeSlots < tradeList.size())
			return false; // TODO message

		long tradeListPrice = tradeList.getRequiredKinah();
		// check if soldOutItem
		LimitedItem item = null;
		for (TradeItem tradeItem : tradeList.getTradeItems()) {
			item = LimitedItemTradeService.getInstance().getLimitedItem(tradeItem.getItemId(), npc.getNpcId());
			if (item != null) {
				if (item.getBuyLimit() == 0 && item.getDefaultSellLimit() != 0) { // type A
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (item.getSellLimit() - tradeItem.getCount() < 0)
						return false;
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
				else if (item.getBuyLimit() != 0 && item.getDefaultSellLimit() == 0) { // type B
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (item.getBuyLimit() - tradeItem.getCount() < 0)
						return false;
					if (item.getBuyCount().containsKey(player.getObjectId())) {
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit()) {
							item.getBuyCount().put(player.getObjectId(),
									item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
							return false;
					}
				}
				else if (item.getBuyLimit() != 0 && item.getDefaultSellLimit() != 0) { // type C
					item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
					if (item.getBuyLimit() - tradeItem.getCount() < 0 || item.getSellLimit() - tradeItem.getCount() < 0)
						return false;

					if (item.getBuyCount().containsKey(player.getObjectId())) {
						if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit()) {
							item.getBuyCount().put(player.getObjectId(),
									item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
						}
						else
							return false;
					}
					item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
				}
			}
            Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
            for (Integer itemId : requiredItems.keySet()) {
                if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId))) {
                    AuditLogger.info(player, "Possible hack. Not " +
                            "removed items on buy in rewardshop.");
                    return false;
				}
			}

			long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0) {
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d",
						player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				inventory.decreaseKinah(tradeListPrice);
				return false;
			}
		}
		inventory.decreaseKinah(tradeListPrice);
		// TODO message
		return true;
	}

	/**
	 * Probably later merge with regular buy
	 *
	 * @param player
	 * @param tradeList
	 * @return true or false
	 */
	public static boolean performBuyFromAbyssShop(Npc npc, Player player, TradeList tradeList) {
		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}

		if (!validateBuyItems(npc, tradeList, player)) {
			PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("NO_ITEM_TO_TRADE"));
			return false;
		}

		Storage inventory = player.getInventory();
		int freeSlots = inventory.getFreeSlots();

		if (!tradeList.calculateAbyssBuyListPrice(player))
			return false;

		if (tradeList.getRequiredAp() < 0) {
			AuditLogger.info(player, "Posible client hack. tradeList.getRequiredAp() < 0");
			// You do not have enough Abyss Points.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300927));
			return false;
		}

		// 2. check free slots, need to check retail behaviour
		if (freeSlots < tradeList.size()) {
			// You cannot trade as your inventory is full.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300762));
			return false;
		}
        int trlssell = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId()).getSellPriceRate();
        AbyssPointsService.addAGp(player, -tradeList.getRequiredAp()*trlssell/100, 0);
		Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
		for (Integer itemId : requiredItems.keySet()) {
			if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId))) {
				AuditLogger.info(player, "Possible hack. Not removed items on buy in abyss shop.");
				return false;
			}
		}
        LimitedItem item = null;

		for (TradeItem tradeItem : tradeList.getTradeItems()) {
            item = LimitedItemTradeService.getInstance().getLimitedItem(tradeItem.getItemId(), npc.getNpcId());
            if (item != null) {
                if (item.getBuyLimit() == 0 && item.getDefaultSellLimit() != 0) { // type A
                    item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
                    if (item.getSellLimit() - tradeItem.getCount() < 0)
                        return false;
                    item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
                }
                else if (item.getBuyLimit() != 0 && item.getDefaultSellLimit() == 0) { // type B
                    item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
                    if (item.getBuyLimit() - tradeItem.getCount() < 0)
                        return false;
                    if (item.getBuyCount().containsKey(player.getObjectId())) {
                        if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit()) {
                            item.getBuyCount().put(player.getObjectId(),
                                    item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
                        }
                        else
                            return false;
                    }
                }
                else if (item.getBuyLimit() != 0 && item.getDefaultSellLimit() != 0) { // type C
                    item.getBuyCount().putIfAbsent(player.getObjectId(), 0);
                    if (item.getBuyLimit() - tradeItem.getCount() < 0 || item.getSellLimit() - tradeItem.getCount() < 0)
                        return false;

                    if (item.getBuyCount().containsKey(player.getObjectId())) {
                        if (item.getBuyCount().get(player.getObjectId()) < item.getBuyLimit()) {
                            item.getBuyCount().put(player.getObjectId(),
                                    item.getBuyCount().get(player.getObjectId()) + (int) tradeItem.getCount());
                        }
                        else
                            return false;
                    }
                    item.setSellLimit(item.getSellLimit() - (int) tradeItem.getCount());
                }
            }

			long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0) {
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d",
						player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				return false;
			}

			if (tradeItem.getCount() > 1) // You have purchased %1 %0s.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300785, new DescriptionId(tradeItem
						.getItemTemplate().getNameId()), tradeItem.getCount()));
			else
				// You have purchased %0.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300784, new DescriptionId(tradeItem
						.getItemTemplate().getNameId())));
		}

		return true;
	}

	/**
	 * Probably later merge with regular buy
	 *
	 * @param player
	 * @param tradeList
	 * @return true or false
	 */
	public static boolean performBuyFromRewardShop(Npc npc, Player player, TradeList tradeList) {
		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}

		if (!validateBuyItems(npc, tradeList, player)) {
			PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("NO_ITEM_TO_TRADE"));
			return false;
		}

		Storage inventory = player.getInventory();
		int freeSlots = inventory.getFreeSlots();

		// 1. check required items
		if (!tradeList.calculateRewardBuyListPrice(player))
			return false;

		// 2. check free slots, need to check retail behaviour
		if (freeSlots < tradeList.size())
			return false; // TODO message

		Map<Integer, Long> requiredItems = tradeList.getRequiredItems();
		for (Integer itemId : requiredItems.keySet()) {
			if (!player.getInventory().decreaseByItemId(itemId, requiredItems.get(itemId))) {
				AuditLogger.info(player, "Possible hack. Not removed items on buy in rewardshop.");
				return false;
			}
		}

		for (TradeItem tradeItem : tradeList.getTradeItems()) {



			long count = ItemService.addItem(player, tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount());
			if (count != 0) {
				log.warn(String.format("CHECKPOINT: itemservice couldnt add all items on buy: %d %d %d %d",
						player.getObjectId(), tradeItem.getItemTemplate().getTemplateId(), tradeItem.getCount(), count));
				return false;
			}
		}
		// TODO message
		return true;
	}

	/**
	 * @param tradeList
	 */
	private static boolean validateBuyItems(Npc npc, TradeList tradeList, Player player) {
		TradeListTemplate tradeListTemplate = tradeListData.getTradeListTemplate(npc.getObjectTemplate().getTemplateId());

		Set<Integer> allowedItems = new HashSet<Integer>();
		for (TradeTab tradeTab : tradeListTemplate.getTradeTablist()) {
			GoodsList goodsList = goodsListData.getGoodsListById(tradeTab.getId());
			if (goodsList != null && goodsList.getItemIdList() != null) {
				allowedItems.addAll(goodsList.getItemIdList());
			}
		}

		for (TradeItem tradeItem : tradeList.getTradeItems()) {
			if (tradeItem.getCount() < 1) {
				AuditLogger.info(player, "BUY packet hack item count < 1!");
				return false;
			}
			if (!allowedItems.contains(tradeItem.getItemId()))
				return false;
		}
		return true;
	}

	/**
	 * @param player
	 * @param tradeList
	 * @return true or false
	 */
	public static boolean performSellToShop(Player player, TradeList tradeList) {
		Storage inventory = player.getInventory();
		long kinahReward = 0;
		List<Item> items = new ArrayList<Item>();

		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}

		for (TradeItem tradeItem : tradeList.getTradeItems()) {
			Item item = inventory.getItemByObjId(tradeItem.getItemId());
			// 1) don't allow to sell fake items;
			if (item == null)
				return false;

			if (!item.isSellable()) { // %0 is not an item that can be sold.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300344, new DescriptionId(item.getNameId())));
				return false;
			}

			Item repurchaseItem = null;
			long sellReward = PricesService.getKinahForSellWithOutTax(item.getItemTemplate().getPrice(), item.getItemTemplate(), player.getRace());
			long realReward = sellReward * tradeItem.getCount();
			if (!PlayerLimitService.updateSellLimit(player, realReward))
				break;

			if (item.getItemCount() - tradeItem.getCount() < 0) {
				AuditLogger.info(player, "Trade exploit, sell item count big");
				return false;
			}
			else if (item.getItemCount() - tradeItem.getCount() == 0) {
				inventory.delete(item); // need to be here to avoid exploit by sending packet with many
				// items with same unique ids
				repurchaseItem = item;
			}
			else if (item.getItemCount() - tradeItem.getCount() > 0) {
				repurchaseItem = ItemFactory.newItem(item.getItemId(), tradeItem.getCount());
				inventory.decreaseItemCount(item, tradeItem.getCount());
			}
			else
				return false;
			kinahReward += realReward;
			repurchaseItem.setRepurchasePrice(realReward);
			items.add(repurchaseItem);
		}
		RepurchaseService.getInstance().addRepurchaseItems(player, items);
		inventory.increaseKinah(kinahReward);
		return true;
	}

    public static boolean performSellToShopKinah(Player player, TradeList tradeList) {
        Storage inventory = player.getInventory();
        long kinahReward = 0;
        List<Item> items = new ArrayList<Item>();

        if (!RestrictionsManager.canTrade(player)) {
            return false;
        }

        for (TradeItem tradeItem : tradeList.getTradeItems()) {
            Item item = inventory.getItemByObjId(tradeItem.getItemId());
            // 1) don't allow to sell fake items;
            if (item == null)
                return false;

            Item repurchaseItem = null;
            long sellReward = PricesService.getKinahForSellWithOutTax(item.getItemTemplate().getPrice(), item.getItemTemplate(), player.getRace());
            long realReward = sellReward * tradeItem.getCount();
            if (!PlayerLimitService.updateSellLimit(player, realReward))
                break;

            if (item.getItemCount() - tradeItem.getCount() < 0) {
                AuditLogger.info(player, "Trade exploit, sell item count big");
                return false;
            }
            else if (item.getItemCount() - tradeItem.getCount() == 0) {
                inventory.delete(item); // need to be here to avoid exploit by sending packet with many
                // items with same unique ids
                repurchaseItem = item;
            }
            else if (item.getItemCount() - tradeItem.getCount() > 0) {
                repurchaseItem = ItemFactory.newItem(item.getItemId(), tradeItem.getCount());
                inventory.decreaseItemCount(item, tradeItem.getCount());
            }
            else
                return false;

			kinahReward += realReward;
			repurchaseItem.setRepurchasePrice(realReward);
			items.add(repurchaseItem);
		}
		RepurchaseService.getInstance().addRepurchaseItems(player, items);
		inventory.increaseKinah(kinahReward);

		return true;
	}

	public static boolean performBuyFromTradeInTrade(Player player, int npcObjectId, int itemId, int count) {
		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			return false;
		}
		VisibleObject visibleObject = player.getKnownList().getObject(npcObjectId);
		if (visibleObject == null || !(visibleObject instanceof Npc) || MathUtil.getDistance(visibleObject, player) > 10)
			return false;
		int npcId = ((Npc) visibleObject).getNpcId();
		TradeListTemplate tradeInList = tradeListData.getTradeInListTemplate(npcId);
		if (tradeInList == null)
			return false;
		boolean valid = false;
		for (TradeTab tab : tradeInList.getTradeTablist()) {
			GoodsList goodList = goodsListData.getGoodsInListById(tab.getId());
			if (goodList.getItemIdList().contains(itemId)) {
				valid = true;
				break;
			}
		}
		if (!valid)
			return false;

		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemTemplate.getMaxStackCount() < count)
			return false;
		try {
			for (TradeinItem treadInList : itemTemplate.getTradeinList().getTradeinItem()) {
				if (player.getInventory().getItemCountByItemId(treadInList.getId()) < SafeMath.multSafe(treadInList.getPrice(), count))
					return false;
			}

            if (itemTemplate.getAcquisition() != null) {
                if (itemTemplate.getAcquisition().getType() == AcquisitionType.AP && itemTemplate.getAcquisition().getRequiredAp() > player.getAbyssRank().getAp())
                    return false;
            }
			for (TradeinItem treadInList : itemTemplate.getTradeinList().getTradeinItem()) {
				if (!player.getInventory().decreaseByItemId(treadInList.getId(), SafeMath.multSafe(treadInList.getPrice(), count)))
					return false;
			}
            if (itemTemplate.getTradeinList().getAP() != 0) {
                AbyssPointsService.addAp(player, -itemTemplate.getTradeinList().getAP());
            }
		}
		catch (OverfowException e) {
			AuditLogger.info(player, "OverfowException using tradeInTrade " + e.getMessage());
			return false;
		}

		ItemService.addItem(player, itemId, count);
		return false;
	}

	public static boolean performSellForAPToShop(Player player, TradeList tradeList, TradeListTemplate purchaseTemplate) {
		if (!RestrictionsManager.canTrade(player)) {
			return false;
		}
		Storage inventory = player.getInventory();
		for (TradeItem tradeItem : tradeList.getTradeItems()) {
			int itemObjectId = tradeItem.getItemId();
			long count = tradeItem.getCount();
			Item item = inventory.getItemByObjId(itemObjectId);
			if (item == null) {
				return false;
			}
			int itemId = item.getItemId();
			boolean valid = false;
			for (TradeTab tab : purchaseTemplate.getTradeTablist()) {
				GoodsList goodList = goodsListData.getGoodsPurchaseListById(tab.getId());
				if (goodList.getItemIdList().contains(itemId)) {
					valid = true;
					break;
				}
			}
			if (!valid)         {
				return false;
            }
			if (inventory.decreaseByObjectId(itemObjectId, count)) {
                int price = item.getItemTemplate().getAcquisition().getRequiredAp() * (int) count;
                int modifier = 0;
                if(player.getTarget() instanceof Npc){
                    modifier = getPriceModifier((Npc) player.getTarget());
                }

                if(modifier != 0){
                    price /= modifier;
                }
				AbyssPointsService.addAGp(player, price, 0);
			}
		}
		return true;
	}

    public static int getPriceModifier(Npc n){
        if(n.getObjectTemplate().getTitleId() == 463224 || n.getObjectTemplate().getTitleId() == 463230 || n.getObjectTemplate().getTitleId() == 463491
           || n.getObjectTemplate().getTitleId() == 463493 || n.getObjectTemplate().getTitleId() == 463209
                ) {
            return 10;
        }
        /*if(n.getObjectTemplate().getNameId() == 389894) {
            return 15;
        }*/
        if(n.getObjectTemplate().getTitleId() == 463648) {
            return 3;
        }
        if(n.getObjectTemplate().getTitleId() == 463490) {
            return 0;
        }
        return 0;
    }

	/**
	 * @return the tradeListData
	 */
	public static TradeListData getTradeListData() {
		return tradeListData;
	}

	/**
	 * @return the goodsListData
	 */
	public static GoodsListData getGoodsListData() {
		return goodsListData;
	}

}
