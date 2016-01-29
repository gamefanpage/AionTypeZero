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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javolution.util.FastMap;

import org.typezero.gameserver.model.gameobjects.Letter;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import org.typezero.gameserver.services.mail.MailService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author kosyachok
 * @modified Atracer
 */
public class Mailbox {

	private Map<Integer, Letter> mails = new FastMap<Integer, Letter>().shared();
	private Map<Integer, Letter> reserveMail = new FastMap<Integer, Letter>().shared();
	private Player owner;
	public boolean isMailListUpdateRequired;

	// 0x00 - closed
	// 0x01 - regular
	// 0x02 - express
	public byte mailBoxState = 0;

	public Mailbox(Player player) {
		this.owner = player;
	}

	/**
	 * @param letter
	 */
	public void putLetterToMailbox(Letter letter) {
		if (haveFreeSlots())
			mails.put(letter.getObjectId(), letter);
		else
			reserveMail.put(letter.getObjectId(), letter);
	}

	/**
	 * Get all letters in mailbox (sorted according to time received)
	 *
	 * @return
	 */
	public Collection<Letter> getLetters() {
		SortedSet<Letter> letters = new TreeSet<Letter>(new Comparator<Letter>() {

			@Override
			public int compare(Letter o1, Letter o2) {
				if (o1.getTimeStamp().getTime() > o2.getTimeStamp().getTime())
					return 1;
				if (o1.getTimeStamp().getTime() < o2.getTimeStamp().getTime())
					return -1;

				return o1.getObjectId() > o2.getObjectId() ? 1 : -1;
			}

		});

		for (Letter letter : mails.values()) {
			letters.add(letter);
		}
		return letters;
	}

	/**
	 * Get system letters which senders start with the string specified and were received since the last player login
	 *
	 * @param substring
	 *          must start with special characters: % or $$
	 * @return new list of letters
	 */
	public List<Letter> getNewSystemLetters(String substring) {
		List<Letter> letters = new ArrayList<Letter>();
		for (Letter letter : mails.values()) {
			if (letter.getSenderName() == null || !letter.isUnread())
				continue;
			if (owner.getCommonData().getLastOnline().getTime() > letter.getTimeStamp().getTime())
				continue;
			if (letter.getSenderName().startsWith("%") || letter.getSenderName().startsWith("$$")) {
				if (letter.getSenderName().startsWith(substring))
					letters.add(letter);
			}
		}
		return letters;
	}

	/**
	 * Get letter with specified letter id
	 *
	 * @param letterObjId
	 * @return
	 */
	public Letter getLetterFromMailbox(int letterObjId) {
		return mails.get(letterObjId);
	}

	/**
	 * Check whether mailbox contains empty letters
	 *
	 * @return
	 */
	public boolean haveUnread() {
		for (Letter letter : mails.values()) {
			if (letter.isUnread())
				return true;
		}
		return false;
	}

	public final int getUnreadCount() {
		int unreadCount = 0;
		for (Letter letter : mails.values()) {
			if (letter.isUnread())
				unreadCount++;
		}
		return unreadCount;
	}

	public boolean haveUnreadByType(LetterType letterType) {
		for (Letter letter : mails.values()) {
			if (letter.isUnread() && letter.getLetterType() == letterType)
				return true;
		}
		return false;
	}

	public final int getUnreadCountByType(LetterType letterType) {
		int count = 0;
		for (Letter letter : mails.values()) {
			if (letter.isUnread() && letter.getLetterType() == letterType)
				count++;
		}
		return count;
	}

	/**
	 * @return
	 */
	public boolean haveFreeSlots() {
		return mails.size() < 100;
	}

	/**
	 * @param letterId
	 */
	public void removeLetter(int letterId) {
		mails.remove(letterId);
		uploadReserveLetters();
	}

	/**
	 * Current size of mailbox
	 *
	 * @return
	 */
	public int size() {
		return mails.size();
	}

	public void uploadReserveLetters() {
		if (reserveMail.size() > 0 && haveFreeSlots()) {
			for (Letter letter : reserveMail.values()) {
				if (haveFreeSlots()) {
					mails.put(letter.getObjectId(), letter);
					reserveMail.remove(letter.getObjectId());
				}
				else
					break;
			}
			MailService.getInstance().refreshMail(getOwner());
		}
	}

	public void sendMailList(boolean expressOnly) {
		PacketSendUtility.sendPacket(owner, new SM_MAIL_SERVICE(owner, getLetters(), expressOnly));
	}

	public Player getOwner() {
		return owner;
	}

}
