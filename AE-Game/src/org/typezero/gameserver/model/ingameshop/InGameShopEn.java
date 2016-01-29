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

package org.typezero.gameserver.model.ingameshop;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.ingameshop.InGameShopProperty;
import org.typezero.gameserver.configs.main.InGameShopConfig;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.dao.InGameShopDAO;
import org.typezero.gameserver.dao.InGameShopLogDAO;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.templates.mail.MailMessage;
import org.typezero.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_PREMIUM_CONTROL;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.mail.SystemMailService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

import java.sql.Timestamp;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KID, xTz
 *
 */
public class InGameShopEn {

	private static InGameShopEn instance = new InGameShopEn();
	private final Logger log = LoggerFactory.getLogger("INGAMESHOP_LOG");

	public static InGameShopEn getInstance() {
		return instance;
	}

	private FastMap<Byte, List<IGItem>> items;
	private InGameShopDAO dao;
	private InGameShopProperty iGProperty;

	public InGameShopEn() {
		if (!InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			log.info("InGameShop is disabled.");
			return;
		}
		iGProperty = InGameShopProperty.load();
		dao = DAOManager.getDAO(InGameShopDAO.class);
		items = FastMap.newInstance();
		activeRequests = FastList.newInstance();
		items = dao.loadInGameShopItems();
		log.info("Loaded with " + items.size() + " items.");
	}

	public InGameShopProperty getIGSProperty() {
		return iGProperty;
	}

	public void reload() {
		if (!InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			log.info("InGameShop is disabled.");
			return;
		}
		iGProperty.clear();
		iGProperty = InGameShopProperty.load();
		items = DAOManager.getDAO(InGameShopDAO.class).loadInGameShopItems();
		log.info("Loaded with " + items.size() + " items.");
	}

	public IGItem getIGItem(int id) {
		for (byte key : items.keySet()) {
			for (IGItem item : items.get(key)) {
				if (item.getObjectId() == id) {
					return item;
				}
			}
		}
		return null;
	}

	public Collection<IGItem> getItems(byte category) {
		if (!items.containsKey(category)) {
			return Collections.emptyList();
		}
		return this.items.get(category);
	}

	@SuppressWarnings("unchecked")
	public FastList<Integer> getTopSales(int subCategory, byte category) {
		byte max = 6;
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(new DescFilter());
		if (!items.containsKey(category)) {
			return FastList.newInstance();
		}
		for (IGItem item : this.items.get(category)) {
			if (item.getSalesRanking() == 0)
				continue;

			if (subCategory != 2 && item.getSubCategory() != subCategory)
				continue;

			map.put(item.getSalesRanking(), item.getObjectId());
		}
		FastList<Integer> top = FastList.newInstance();
		byte cnt = 0;
		for (int objId : map.values()) {
			if (cnt <= max) {
				top.add(objId);
				cnt++;
			}
			else
				break;
		}
		map.clear();
		return top;
	}

	@SuppressWarnings("rawtypes")
	class DescFilter implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			Integer i1 = (Integer) o1;
			Integer i2 = (Integer) o2;
			return -i1.compareTo(i2);
		}

	}

	public int getMaxList(byte subCategoryId, byte category) {
		int id = 0;
		if (!items.containsKey(category)) {
			return id;
		}
		for (IGItem item : items.get(category)) {
			if (item.getSubCategory() == subCategoryId)
				if (item.getList() > id)
					id = item.getList();
		}

		return id;
	}

	private int lastRequestId = 0;
	private FastList<IGRequest> activeRequests;

	public void acceptRequest(Player player, int itemObjId) {
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
			return;
		}

		IGItem item = InGameShopEn.getInstance().getIGItem(itemObjId);
		lastRequestId++;
		IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), itemObjId);
		request.accountId = player.getClientConnection().getAccount().getId();
		if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, item.getItemPrice())))
			activeRequests.add(request);
		if (LoggingConfig.LOG_INGAMESHOP)
			log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " is watching item:" + item.getItemId() + " cost " + item.getItemPrice() + " toll.");
	}

	public void sendRequest(Player player, String receiver, String message, int itemObjId) {
		if (receiver.equalsIgnoreCase(player.getName())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_CANNOT_GIVE_TO_ME);
			return;
		}

		if (!InGameShopConfig.ALLOW_GIFTS) {
			PacketSendUtility.sendMessage(player, "Gifts are disabled.");
			return;
		}

		if (!DAOManager.getDAO(PlayerDAO.class).isNameUsed(receiver)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_NO_USER_TO_GIFT);
			return;
		}

		PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(receiver);
		if (recipientCommonData.getMailboxLetters() >= 100) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MAIL_MSG_RECIPIENT_MAILBOX_FULL(recipientCommonData.getName()));
			return;
		}

		if (!InGameShopConfig.ENABLE_GIFT_OTHER_RACE && !player.isGM())
			if (player.getRace() != recipientCommonData.getRace()) {
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(MailMessage.MAIL_IS_ONE_RACE_ONLY));
				return;
			}

		IGItem item = getIGItem(itemObjId);
		lastRequestId++;
		IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), receiver, message, itemObjId);
		request.accountId = player.getClientConnection().getAccount().getId();
		if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, item.getItemPrice())))
			activeRequests.add(request);
	}

	public void addToll(Player player, long cnt) {
		if (InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			lastRequestId++;
			IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), 0);
			request.accountId = player.getClientConnection().getAccount().getId();
			if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, cnt * -1)))
				activeRequests.add(request);
		}
		else {
			PacketSendUtility.sendMessage(player, "You can't add toll if ingameshop is disabled!");
		}
	}

	public void delToll(Player player, long cnt) {
			lastRequestId++;
			IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), 0);
			request.accountId = player.getClientConnection().getAccount().getId();
			if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, cnt)))
				activeRequests.remove(request);
	}

	public void finishRequest(int requestId, int result, long toll) {
		for (IGRequest request : activeRequests)
			if (request.requestId == requestId) {
				Player player = World.getInstance().findPlayer(request.playerId);
				if (player != null) {
					if (result == 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_ERROR);
					}
					else if (result == 2) {
						IGItem item = getIGItem(request.itemObjId);
						if (item == null) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_ERROR);
							log.error("player " + player.getName() + " requested " + request.itemObjId + " that was not exists in list.");
							return;
						}
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_INGAMESHOP_NOT_ENOUGH_CASH("\u041f\u043e\u0438\u043d\u0442\u043e\u0432 . \u041f\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0412\u0430\u0448 \u0438\u0433\u0440\u043e\u0432\u043e\u0439 \u0431\u0430\u043b\u0430\u043d\u0441."));
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
						if (LoggingConfig.LOG_INGAMESHOP)
							log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " has not bought item: " + item.getItemId() + " count: " + item.getItemCount() + " Cause: NOT ENOUGH TOLLS");
					}
					else if (result == 3) {
						IGItem item = getIGItem(request.itemObjId);
						if (item == null) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_ERROR);
							log.error("player " + player.getName() + " requested " + request.itemObjId + " that was not exists in list.");
							return;
						}

						if (request.gift) {
							SystemMailService.getInstance().sendMail(player.getName(), request.receiver, "In Game Shop", request.message, item.getItemId(), item.getItemCount(), 0, LetterType.BLACKCLOUD);
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_GIFT_SUCCESS);
							if (LoggingConfig.LOG_INGAMESHOP)
								log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " BUY ITEM: " + item.getItemId() + " COUNT: " + item.getItemCount() + " FOR PlayerName: " + request.receiver);
							if (LoggingConfig.LOG_INGAMESHOP_SQL)
								DAOManager.getDAO(InGameShopLogDAO.class).log("GIFT", new Timestamp(System.currentTimeMillis()), player.getName(), player.getAcountName(), request.receiver, item.getItemId(), item.getItemCount(), item.getItemPrice());
						}
						else {
							ItemService.addItem(player, item.getItemId(), item.getItemCount());
							if (LoggingConfig.LOG_INGAMESHOP)
								log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " BUY ITEM: " + item.getItemId() + " COUNT: " + item.getItemCount());
							if (LoggingConfig.LOG_INGAMESHOP_SQL)
								DAOManager.getDAO(InGameShopLogDAO.class).log("BUY", new Timestamp(System.currentTimeMillis()), player.getName(), player.getAcountName(), player.getName(), item.getItemId(), item.getItemCount(), item.getItemPrice());
							DAOManager.getDAO(InventoryDAO.class).store(player);
						}

						item.increaseSales();
						dao.increaseSales(item.getObjectId(), item.getSalesRanking());
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					}
					else if (result == 4) {
						player.getClientConnection().getAccount().setToll(toll);
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					}
				}

				activeRequests.remove(request);
				break;
			}
	}

}
