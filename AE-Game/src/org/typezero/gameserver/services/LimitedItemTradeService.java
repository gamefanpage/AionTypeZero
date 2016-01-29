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

import com.aionemu.commons.services.CronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.GoodsListData;
import org.typezero.gameserver.dataholders.TradeListData;
import org.typezero.gameserver.model.limiteditems.LimitedItem;
import org.typezero.gameserver.model.limiteditems.LimitedTradeNpc;
import org.typezero.gameserver.model.templates.goods.GoodsList;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author xTz
 *
 * TYPE_A: BuyLimit == 0 && SellLimit != 0
 * TYPE_B: BuyLimit != 0 && SellLimit == 0
 * TYPE_C: BuyLimit != 0 && SellLimit != 0
 */
public class LimitedItemTradeService {

	private static final Logger log = LoggerFactory.getLogger(LimitedItemTradeService.class);
	private GoodsListData goodsListData = DataManager.GOODSLIST_DATA;
	private TradeListData tradeListData = DataManager.TRADE_LIST_DATA;
	private FastMap<Integer, LimitedTradeNpc> limitedTradeNpcs = new FastMap<Integer, LimitedTradeNpc>().shared();

	public void start() {
		for (int npcId : tradeListData.getTradeListTemplate().keys()) {
			for (TradeTab list : tradeListData.getTradeListTemplate(npcId).getTradeTablist()) {
				GoodsList goodsList = goodsListData.getGoodsListById(list.getId());
				if (goodsList == null) {
					log.warn("No goodslist for tradelist of npc " + npcId);
					continue;
				}
				FastList<LimitedItem> limitedItems = goodsList.getLimitedItems();
				if (limitedItems.isEmpty()) {
					continue;
				}
				if (!limitedTradeNpcs.containsKey(npcId)) {
					limitedTradeNpcs.putIfAbsent(npcId, new LimitedTradeNpc(limitedItems));
				}
				else {
					limitedTradeNpcs.get(npcId).putLimitedItems(limitedItems);
				}
			}
		}

		for (LimitedTradeNpc limitedTradeNpc : limitedTradeNpcs.values()) {
			for (final LimitedItem limitedItem : limitedTradeNpc.getLimitedItems()) {
				CronService.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						limitedItem.setToDefault();
					}

				}, limitedItem.getSalesTime());
			}
		}
		log.info("Scheduled Limited Items based on cron expression size: " + limitedTradeNpcs.size());
	}

	public LimitedItem getLimitedItem(int itemId, int npcId) {
		if (limitedTradeNpcs.containsKey(npcId)) {
			for (LimitedItem limitedItem : limitedTradeNpcs.get(npcId).getLimitedItems()) {
				if (limitedItem.getItemId() == itemId) {
					return limitedItem;
				}
			}
		}
		return null;
	}

	public boolean isLimitedTradeNpc(int npcId) {
		return limitedTradeNpcs.containsKey(npcId);
	}

	public LimitedTradeNpc getLimitedTradeNpc(int npcId) {
		return limitedTradeNpcs.get(npcId);
	}

	public static LimitedItemTradeService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final LimitedItemTradeService INSTANCE = new LimitedItemTradeService();
	}

}
