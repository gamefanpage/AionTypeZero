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

package org.typezero.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate;
import org.typezero.gameserver.model.templates.tradelist.TradeNpcType;
import org.typezero.gameserver.model.trade.RepurchaseList;
import org.typezero.gameserver.model.trade.TradeList;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.PrivateStoreService;
import org.typezero.gameserver.services.RepurchaseService;
import org.typezero.gameserver.services.TradeService;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author orz, ATracer, Simple, xTz
 */
public class CM_BUY_ITEM extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_BUY_ITEM.class);
	private int sellerObjId;
	private int tradeActionId;
	private int amount;
	private int itemId;
	private long count;
	private boolean isAudit;
	private TradeList tradeList;
	private RepurchaseList repurchaseList;

	public CM_BUY_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		sellerObjId = readD();
		tradeActionId = readH();
		amount = readH(); // total no of items

		if (amount < 0 || amount > 36) {
			isAudit = true;
			AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM amount: " + amount);
			return;
		}
		if (tradeActionId == 2) {
			repurchaseList = new RepurchaseList(sellerObjId);
		}
		else {
			tradeList = new TradeList(sellerObjId);
		}

		for (int i = 0; i < amount; i++) {
			itemId = readD();
			count = readQ();

			// prevent exploit packets
			if (count < 0 || (itemId <= 0 && tradeActionId != 0) || itemId == 190000073 || itemId == 190000074 || count > 20000) {
				isAudit = true;
				AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM item: " + itemId + " count: " + count);
				break;
			}

			switch (tradeActionId) {
				case 0://private store
				case 1://sell to shop
					tradeList.addSellItem(itemId, count);
					break;
				case 2://repurchase
					repurchaseList.addRepurchaseItem(player, itemId, count);
					break;
				case 13://buy from shop
				case 14://buy from abyss shop
				case 15://buy from reward shop
					tradeList.addBuyItem(itemId, count);
					break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		if (isAudit || player == null)
			return;

		VisibleObject target = player.getKnownList().getKnownObjects().get(sellerObjId);

		if (target == null)
			return;

		if (target instanceof Player && tradeActionId == 0) {
			Player targetPlayer = (Player) target;
			PrivateStoreService.sellStoreItem(targetPlayer, player, tradeList);
		}
		else if (target instanceof Npc) {
			Npc npc = (Npc) target;
			TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
			switch (tradeActionId) {
				case 1://sell to shop
					if (npc.getObjectTemplate().getTitleId() == 463495 || npc.getObjectTemplate().getTitleId() == 463628 || npc.getObjectTemplate().getTitleId() == 463648 || npc.getObjectTemplate().getTitleId() == 463493
					 /*|| npc.getObjectTemplate().getTitleId() == 463224 || npc.getObjectTemplate().getTitleId() == 463491 || npc.getObjectTemplate().getTitleId() == 463209*/
                         || npc.getObjectTemplate().getTitleId() == 463230 || npc.getObjectTemplate().getTitleId() == 314357 || npc.getObjectTemplate().getTitleId() == 314358 || npc.getObjectTemplate().getTitleId() == 314359 || npc.getObjectTemplate().getTitleId() == 314360) {
						TradeListTemplate purchaseTemplate = DataManager.TRADE_LIST_DATA.getPurchaseTemplate(npc.getNpcId());
						TradeService.performSellForAPToShop(player, tradeList, purchaseTemplate);
                    }
                    if (npc.getObjectTemplate().getTitleId() == 463490)  {
                        TradeService.performSellToShopKinah(player, tradeList);
                    }
					else {
						TradeService.performSellToShop(player, tradeList);
					}
					break;
				case 2://repurchase
					RepurchaseService.getInstance().repurchaseFromShop(player, repurchaseList);
					break;
				case 13://buy from shop
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.NORMAL) {
						TradeService.performBuyFromShop(npc, player, tradeList);
					}
					break;
				case 14://buy from abyss shop
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.ABYSS) {
						TradeService.performBuyFromAbyssShop(npc, player, tradeList);
					}
					break;
				case 15://buy from reward shop
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.REWARD) {
						TradeService.performBuyFromRewardShop(npc, player, tradeList);
					}
					break;
				default:
					log.info(String.format("Unhandle shop action unk1: %d", tradeActionId));
					break;
			}
		}
	}

}
