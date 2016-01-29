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

package org.typezero.gameserver.services.mail;

import com.aionemu.commons.database.dao.DAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.dao.MailDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Letter;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Mailbox;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemFactory;
import org.typezero.gameserver.services.player.PlayerMailboxState;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author xTz
 */
public class SystemMailService {

	private static final Logger log = LoggerFactory.getLogger("SYSMAIL_LOG");

	public static final SystemMailService getInstance() {
		return SingletonHolder.instance;
	}

	private SystemMailService() {
		log.info("SystemMailService: Initialized.");
	}

	/**
	 * @param sender
	 * @param recipientName
	 * @param title
	 * @param message
	 * @param attachedItemObjId
	 * @param attachedItemCount
	 * @param attachedKinahCount
	 * @param express
	 */
	public boolean sendMail(String sender, String recipientName, String title, String message, int attachedItemObjId, long attachedItemCount,
			long attachedKinahCount, LetterType letterType) {

		if (attachedItemObjId != 0) {
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(attachedItemObjId);
			if (itemTemplate == null) {
				log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] RETURN ITEM ID:" + itemTemplate
						+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " ITEM TEMPLATE IS MISSING ");
				return false;
			}
		}

		if (attachedItemCount == 0 && attachedItemObjId != 0)
			return false;

		if (recipientName.length() > 16) {
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN" + attachedItemObjId
					+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " RECIPIENT NAME LENGTH > 16 ");
			return false;
		}

		if (!sender.startsWith("$$") && sender.length() > 16) {
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN" + attachedItemObjId
					+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " SENDER NAME LENGTH > 16 ");
			return false;
		}

		if (title.length() > 20)
			title = title.substring(0, 20);

		if (message.length() > 1000)
			message = message.substring(0, 1000);

		PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipientName);

		if (recipientCommonData == null) {
			log.info("[SYSMAILSERVICE] > [RecipientName: " + recipientName + "] NO SUCH CHARACTER NAME.");
			return false;
		}

		Player recipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		if (recipient != null) {
			if (recipient.getMailbox() != null && !(recipient.getMailbox().size() < 200)) {
				log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientCommonData.getName() + "] ITEM RETURN"
						+ attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " MAILBOX FULL ");
				return false;
			}
		}
		else if (recipientCommonData.getMailboxLetters() > 199) {
			return false;
		}
		Item attachedItem = null;
		long finalAttachedKinahCount = 0;
		int itemId = attachedItemObjId;
		long count = attachedItemCount;

		if (itemId != 0) {
			Item senderItem = ItemFactory.newItem(itemId, count);
			if (senderItem != null) {
				senderItem.setEquipped(false);
				senderItem.setEquipmentSlot(0);
				senderItem.setItemLocation(StorageType.MAILBOX.getId());
				attachedItem = senderItem;
			}
		}

		if (attachedKinahCount > 0)
			finalAttachedKinahCount = attachedKinahCount;

		String finalSender = sender;
		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem,
				finalAttachedKinahCount, title, message, finalSender, time, true, letterType);

		if (!DAOManager.getDAO(MailDAO.class).storeLetter(time, newLetter))
			return false;

		if (attachedItem != null)
			if (!DAOManager.getDAO(InventoryDAO.class).store(attachedItem, recipientCommonData.getPlayerObjId()))
				return false;

		/**
		 * Send mail update packets
		 */
		if (recipient != null && recipient.getMailbox() != null) {
			Mailbox recipientMailbox = recipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);

			PacketSendUtility.sendPacket(recipient, new SM_MAIL_SERVICE(recipient.getMailbox()));
			recipientMailbox.isMailListUpdateRequired = true;

			// if recipient have opened mail list we should update it
			if (recipientMailbox.mailBoxState != 0) {
				boolean isPostman = (recipientMailbox.mailBoxState & PlayerMailboxState.EXPRESS) == PlayerMailboxState.EXPRESS;
				PacketSendUtility.sendPacket(recipient, new SM_MAIL_SERVICE(recipient, recipientMailbox.getLetters(), isPostman));
			}

			if (letterType == LetterType.EXPRESS)
				PacketSendUtility.sendPacket(recipient, SM_SYSTEM_MESSAGE.STR_POSTMAN_NOTIFY);
			/*
			 else if (letterType == LetterType.BLACKCLOUD)
			 PacketSendUtility.sendPacket(recipient, SM_SYSTEM_MESSAGE.STR_MAIL_CASHITEM_BUY(itemId));
			 */
		}

		/**
		 * Update loaded common data and db if player is offline
		 */
		if (!recipientCommonData.isOnline()) {
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			DAOManager.getDAO(MailDAO.class).updateOfflineMailCounter(recipientCommonData);
		}
		if (LoggingConfig.LOG_SYSMAIL)
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] RETURN ITEM ID:" + itemId
					+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " MESSAGE SUCCESSFULLY SENDED ");
		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final SystemMailService instance = new SystemMailService();
	}

}
