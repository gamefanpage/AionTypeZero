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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.trade.Exchange;
import org.typezero.gameserver.model.trade.ExchangeItem;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_KINAH;
import org.typezero.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_EXCHANGE_REQUEST;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.item.ItemFactory;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import org.typezero.gameserver.taskmanager.tasks.TemporaryTradeTimeTask;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ExchangeService {

	private static final Logger log = LoggerFactory.getLogger("EXCHANGE_LOG");

	private Map<Integer, Exchange> exchanges = new HashMap<Integer, Exchange>();

	private ExchangePeriodicTaskManager saveManager;

	private final int DELAY_EXCHANGE_SAVE = 5000;

	public static final ExchangeService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Default constructor
	 */
	private ExchangeService() {
		saveManager = new ExchangePeriodicTaskManager(DELAY_EXCHANGE_SAVE);
	}

	/**
	 * @param objectId
	 * @param objectId2
	 */
	public void registerExchange(Player player1, Player player2) {
		if (!validateParticipants(player1, player2))
			return;

		player1.setTrading(true);
		player2.setTrading(true);

		exchanges.put(player1.getObjectId(), new Exchange(player1, player2));
		exchanges.put(player2.getObjectId(), new Exchange(player2, player1));

		PacketSendUtility.sendPacket(player2, new SM_EXCHANGE_REQUEST(player1.getName()));
		PacketSendUtility.sendPacket(player1, new SM_EXCHANGE_REQUEST(player2.getName()));
	}

	/**
	 * @param player1
	 * @param player2
	 */
	private boolean validateParticipants(Player player1, Player player2) {
		return RestrictionsManager.canTrade(player1) && RestrictionsManager.canTrade(player2);
	}

	private Player getCurrentParter(Player player) {
		Exchange exchange = exchanges.get(player.getObjectId());
		return exchange != null ? exchange.getTargetPlayer() : null;
	}

	/**
	 * @param player
	 * @return Exchange
	 */
	private Exchange getCurrentExchange(Player player) {
		return exchanges.get(player.getObjectId());
	}

	/**
	 * @param player
	 * @return Exchange
	 */
	public Exchange getCurrentParnterExchange(Player player) {
		Player partner = getCurrentParter(player);
		return partner != null ? getCurrentExchange(partner) : null;
	}

	/**
	 * @param player
	 */
	public boolean isPlayerInExchange(Player player) {
		return getCurrentExchange(player) != null;
	}

	/**
	 * @param activePlayer
	 * @param itemCount
	 */
	public void addKinah(Player activePlayer, long itemCount) {
		Exchange currentExchange = getCurrentExchange(activePlayer);
		if (currentExchange == null || currentExchange.isLocked())
			return;

		if (itemCount < 1)
			return;

		// count total amount in inventory
		long availableCount = activePlayer.getInventory().getKinah();

		// count amount that was already added to exchange
		availableCount -= currentExchange.getKinahCount();

		long countToAdd = availableCount > itemCount ? itemCount : availableCount;

		if (countToAdd > 0) {
			Player partner = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_KINAH(countToAdd, 0));
			PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_KINAH(countToAdd, 1));
			currentExchange.addKinah(countToAdd);
			if(LoggingConfig.LOG_PLAYER_EXCHANGE)
				log.info("[PLAYER EXCHANGE] > [Player: " + activePlayer.getName() + "] exchanged [Item: 182400001" +
				(LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: Kinah]" : "]")  + " [Count: " + countToAdd + "] with [Partner: " + partner.getName() + "]");
		}
	}

	/**
	 * @param activePlayer
	 * @param itemObjId
	 * @param itemCount
	 */
	public void addItem(Player activePlayer, int itemObjId, long itemCount) {
		Item item = activePlayer.getInventory().getItemByObjId(itemObjId);
		if (item == null)
			return;

		Player partner = getCurrentParter(activePlayer);
		if (partner == null)
			return;
		if (!TemporaryTradeTimeTask.getInstance().canTrade(item, partner.getObjectId()))
			if (!item.isTradeable(activePlayer))
				return;

		if (itemCount < 1)
			return;

		if (itemCount > item.getItemCount())
			return;

		Exchange currentExchange = getCurrentExchange(activePlayer);

		if (currentExchange == null)
			return;

		if (currentExchange.isLocked())
			return;

		if (currentExchange.isExchangeListFull())
			return;

		if(!AdminService.getInstance().canOperate(activePlayer, partner, item, "trade"))
			return;

		ExchangeItem exchangeItem = currentExchange.getItems().get(item.getObjectId());

		long actuallAddCount = 0;
		// item was not added previosly
		if (exchangeItem == null) {
			Item newItem = null;
			if (itemCount < item.getItemCount()) {
				newItem = ItemFactory.newItem(item.getItemId(), itemCount);
			}
			else {
				newItem = item;
			}
			exchangeItem = new ExchangeItem(itemObjId, itemCount, newItem);
			currentExchange.addItem(itemObjId, exchangeItem);
			actuallAddCount = itemCount;
		}
		// item was already added
		else {
			// if player add item count that is more than possible
			// happens with exploits
			if (item.getItemCount() == exchangeItem.getItemCount())
				return;

			long possibleToAdd = item.getItemCount() - exchangeItem.getItemCount();
			actuallAddCount = itemCount > possibleToAdd ? possibleToAdd : itemCount;
			exchangeItem.addCount(actuallAddCount);
		}

		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_ITEM(0, exchangeItem.getItem(), activePlayer));
		PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_ITEM(1, exchangeItem.getItem(), partner));

		Item exchangedItem = exchangeItem.getItem();

		if(LoggingConfig.LOG_PLAYER_EXCHANGE)
			log.info("[PLAYER EXCHANGE] > [Player: " + activePlayer.getName() + "] exchanged [Item: " + exchangedItem.getItemId() +
			(LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: " + exchangedItem.getItemName() + "]" : "]") + " [Count: " + exchangeItem.getItemCount() + " with [Partner: " + partner.getName() + "]");
	}

	/**
	 * @param activePlayer
	 */
	public void lockExchange(Player activePlayer) {
		Exchange exchange = getCurrentExchange(activePlayer);
		if (exchange != null) {
			exchange.lock();
			Player currentParter = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(3));
		}
	}

	/**
	 * @param activePlayer
	 */
	public void cancelExchange(Player activePlayer) {
		Player currentParter = getCurrentParter(activePlayer);
		cleanupExchanges(activePlayer, currentParter);
		if (currentParter != null)
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(1));
	}

	/**
	 * @param activePlayer
	 */
	public void confirmExchange(Player activePlayer) {
		if (activePlayer == null || !activePlayer.isOnline())
			return;

		Exchange currentExchange = getCurrentExchange(activePlayer);

		// TODO: Why is exchange null =/
		if (currentExchange == null)
			return;
		currentExchange.confirm();

		Player currentPartner = getCurrentParter(activePlayer);
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(2));

		if (getCurrentExchange(currentPartner).isConfirmed()) {
			performTrade(activePlayer, currentPartner);
		}
	}

	/**
	 * @param activePlayer
	 * @param currentPartner
	 */
	private void performTrade(Player activePlayer, Player currentPartner) {
		// TODO message here
		// TODO release item id if return
		if (!validateExchange(activePlayer, currentPartner)) {
			cleanupExchanges(activePlayer, currentPartner);
			return;
		}

		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);

		cleanupExchanges(activePlayer, currentPartner);

		if (!removeItemsFromInventory(activePlayer, exchange1) || !removeItemsFromInventory(currentPartner, exchange2)) {
			AuditLogger.info(activePlayer, "Exchange kinah exploit partner: "
				+ currentPartner.getName());
			return;
		}

		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_CONFIRMATION(0));
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(0));

		putItemToInventory(currentPartner, exchange1, exchange2);
		putItemToInventory(activePlayer, exchange2, exchange1);

		saveManager.add(new ExchangeOpSaveTask(exchange1.getActiveplayer().getObjectId(), exchange2.getActiveplayer()
			.getObjectId(), exchange1.getItemsToUpdate(), exchange2.getItemsToUpdate()));
	}

	/**
	 * @param activePlayer
	 * @param currentPartner
	 */
	private void cleanupExchanges(Player activePlayer, Player currentPartner) {
		if (activePlayer != null) {
			exchanges.remove(activePlayer.getObjectId());
			activePlayer.setTrading(false);
		}

		if (currentPartner != null) {
			exchanges.remove(currentPartner.getObjectId());
			currentPartner.setTrading(false);
		}
	}

	/**
	 * @param player
	 * @param exchange
	 */
	private boolean removeItemsFromInventory(Player player, Exchange exchange) {
		Storage inventory = player.getInventory();

		for (ExchangeItem exchangeItem : exchange.getItems().values()) {
			Item item = exchangeItem.getItem();
			Item itemInInventory = inventory.getItemByObjId(exchangeItem.getItemObjId());
			if(itemInInventory == null) {
				AuditLogger.info(player, "Try to trade unexisting item.");
				return false;
			}

			long itemCount = exchangeItem.getItemCount();

			if (itemCount < itemInInventory.getItemCount()) {
				inventory.decreaseItemCount(itemInInventory, itemCount);
				exchange.addItemToUpdate(itemInInventory);
			}
			else {
				//remove from source inventory only
				inventory.remove(itemInInventory);
				exchangeItem.setItem(itemInInventory);
				// release when only part stack was added in the beginning -> full stack in the end
				if (item.getObjectId() != exchangeItem.getItemObjId()){
					ItemService.releaseItemId(item);
				}
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemInInventory.getObjectId()));
			}
		}
		if (!player.getInventory().tryDecreaseKinah(exchange.getKinahCount()))
			return false;
		exchange.addItemToUpdate(player.getInventory().getKinahItem());
		return true;
	}

	/**
	 * @param activePlayer
	 * @param currentPartner
	 * @return
	 */
	private boolean validateExchange(Player activePlayer, Player currentPartner) {
		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);

		return validateInventorySize(activePlayer, exchange2) && validateInventorySize(currentPartner, exchange1);
	}

	private boolean validateInventorySize(Player activePlayer, Exchange exchange) {
		int numberOfFreeSlots = activePlayer.getInventory().getFreeSlots();
		return numberOfFreeSlots >= exchange.getItems().size();
	}

	/**
	 * @param player
	 * @param exchange1
	 * @param exchange2
	 */
	private void putItemToInventory(Player player, Exchange exchange1, Exchange exchange2) {
		for (ExchangeItem exchangeItem : exchange1.getItems().values()) {
			Item itemToPut = exchangeItem.getItem();
			itemToPut.setEquipmentSlot(0);
			player.getInventory().add(itemToPut);
			exchange2.addItemToUpdate(itemToPut);
		}
		long kinahToExchange = exchange1.getKinahCount();
		if (kinahToExchange > 0) {
			player.getInventory().increaseKinah(exchange1.getKinahCount());
			exchange2.addItemToUpdate(player.getInventory().getKinahItem());
		}
	}

	/**
	 * Frequent running save task
	 */
	public static final class ExchangePeriodicTaskManager extends AbstractFIFOPeriodicTaskManager<ExchangeOpSaveTask> {

		private static final String CALLED_METHOD_NAME = "exchangeOperation()";

		/**
		 * @param period
		 */
		public ExchangePeriodicTaskManager(int period) {
			super(period);
		}

		@Override
		protected void callTask(ExchangeOpSaveTask task) {
			task.run();
		}

		@Override
		protected String getCalledMethodName() {
			return CALLED_METHOD_NAME;
		}

	}

	/**
	 * This class is used for storing all items in one shot after any exchange operation
	 */
	public static final class ExchangeOpSaveTask implements Runnable {

		private int player1Id;
		private int player2Id;
		private List<Item> player1Items;
		private List<Item> player2Items;

		/**
		 * @param player1Id
		 * @param player2Id
		 * @param player1Items
		 * @param player2Items
		 */
		public ExchangeOpSaveTask(int player1Id, int player2Id, List<Item> player1Items, List<Item> player2Items) {
			this.player1Id = player1Id;
			this.player2Id = player2Id;
			this.player1Items = player1Items;
			this.player2Items = player2Items;
		}

		@Override
		public void run() {
			DAOManager.getDAO(InventoryDAO.class).store(player1Items, player1Id);
			DAOManager.getDAO(InventoryDAO.class).store(player2Items, player2Id);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ExchangeService instance = new ExchangeService();
	}
}
