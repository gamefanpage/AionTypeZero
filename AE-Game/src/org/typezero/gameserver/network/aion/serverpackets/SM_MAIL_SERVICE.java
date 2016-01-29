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

import org.typezero.gameserver.model.gameobjects.Letter;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Mailbox;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.mail.MailMessage;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.MailServicePacket;
import org.typezero.gameserver.utils.collections.ListSplitter;
import java.util.Collection;

/**
 * @author kosyachok, Source
 */
public class SM_MAIL_SERVICE extends MailServicePacket {

	private int serviceId;
	private Collection<Letter> letters;
	private int totalCount;
	private int unreadCount;
	private int unreadExpressCount;
	private int unreadBlackCloudCount;
	private int mailMessage;
	private Letter letter;
	private long time;
	private int letterId;
	private int[] letterIds;
	private int attachmentType;
	private boolean isExpress;

	public SM_MAIL_SERVICE(Mailbox mailbox) {
		super(null);
		this.serviceId = 0;
	}

	/**
	 * Send mailMessage(ex. Send OK, Mailbox full etc.)
	 *
	 * @param mailMessage
	 */
	public SM_MAIL_SERVICE(MailMessage mailMessage) {
		super(null);
		this.serviceId = 1;
		this.mailMessage = mailMessage.getId();
	}

	/**
	 * Send mailbox info
	 *
	 * @param player
	 * @param letters
	 */
	public SM_MAIL_SERVICE(Player player, Collection<Letter> letters) {
		super(player);
		this.serviceId = 2;
		this.letters = letters;
	}

	/**
	 * Send mailbox info
	 *
	 * @param player
	 * @param letters
	 * @param express
	 */
	public SM_MAIL_SERVICE(Player player, Collection<Letter> letters, boolean isExpress) {
		super(player);
		this.serviceId = 2;
		this.letters = letters;
		this.isExpress = isExpress;
	}

	/**
	 * used when reading letter
	 *
	 * @param player
	 * @param letter
	 * @param time
	 */
	public SM_MAIL_SERVICE(Player player, Letter letter, long time) {
		super(player);
		this.serviceId = 3;
		this.letter = letter;
		this.time = time;
	}

	/**
	 * used when getting attached items
	 *
	 * @param letterId
	 * @param attachmentType
	 */
	public SM_MAIL_SERVICE(int letterId, int attachmentType) {
		super(null);
		this.serviceId = 5;
		this.letterId = letterId;
		this.attachmentType = attachmentType;
	}

	/**
	 * used when deleting letter
	 *
	 * @param letterId
	 */
	public SM_MAIL_SERVICE(int[] letterIds) {
		super(null);
		this.serviceId = 6;
		this.letterIds = letterIds;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Mailbox mailbox = con.getActivePlayer().getMailbox();
		this.totalCount = mailbox.size();
		this.unreadCount = mailbox.getUnreadCount();
		this.unreadExpressCount = mailbox.getUnreadCountByType(LetterType.EXPRESS);
		this.unreadBlackCloudCount = mailbox.getUnreadCountByType(LetterType.BLACKCLOUD);
		writeC(serviceId);
		switch (serviceId) {
			case 0:
				mailbox.isMailListUpdateRequired = true;
				writeMailboxState(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
				break;
			case 1:
				writeMailMessage(mailMessage);
				break;
			case 2:
				Collection<Letter> _letters;
				if (!letters.isEmpty()) {
					ListSplitter<Letter> splittedLetters = new ListSplitter<Letter>(letters, 100);
					_letters = splittedLetters.getNext();
				}
				else {
					_letters = letters;
				}
				writeLettersList(_letters, player, isExpress, unreadExpressCount + unreadBlackCloudCount);
				break;
			case 3:
				writeLetterRead(letter, time, totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
				break;
			case 5:
				writeLetterState(letterId, attachmentType);
				break;
			case 6:
				mailbox.isMailListUpdateRequired = true;
				writeLetterDelete(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount, letterIds);
				break;
		}
	}

}
