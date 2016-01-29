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

package org.typezero.gameserver.network.aion;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Letter;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob;

import java.util.Collection;

/**
 * @author kosyachok, Source
 */
public abstract class MailServicePacket extends AionServerPacket {

	// private static final Logger log = LoggerFactory.getLogger(MailServicePacket.class);
	protected Player player;

	/**
	 * @param player
	 */
	public MailServicePacket(Player player) {
		this.player = player;
	}

	protected void writeLettersList(Collection<Letter> letters, Player player, boolean isPostman, int showCount) {
		writeD(player.getObjectId());
		writeC(0);
		writeH(isPostman ? -showCount : -letters.size()); // -loop cnt [stupid nc shit!]
		for (Letter letter : letters) {
			if (isPostman) {
				if (!letter.isExpress())
					continue;
				else if (!letter.isUnread())
					continue;
			}

			writeD(letter.getObjectId());
			writeS(letter.getSenderName());
			writeS(letter.getTitle());
			writeC(letter.isUnread() ? 0 : 1);
			if (letter.getAttachedItem() != null) {
				writeD(letter.getAttachedItem().getObjectId());
				writeD(letter.getAttachedItem().getItemTemplate().getTemplateId());
			}
			else {
				writeD(0);
				writeD(0);
			}
			writeQ(letter.getAttachedKinah());
			writeC(letter.getLetterType().getId());
		}
	}

	protected void writeMailMessage(int messageId) {
		writeC(messageId);
	}

	protected void writeMailboxState(int totalCount, int unreadCount, int expressCount, int blackCloudCount) {
		writeH(totalCount);
		writeH(unreadCount);
		writeH(expressCount);
		writeH(blackCloudCount);
	}

	protected void writeLetterRead(Letter letter, long time, int totalCount, int unreadCount, int expressCount, int blackCloudCount) {
		writeD(letter.getRecipientId());
		writeD(totalCount + unreadCount * 0x10000); // total count + unread hex
		writeD(expressCount + blackCloudCount); // unread express + BC letters count
		writeD(letter.getObjectId());
		writeD(letter.getRecipientId());
		writeS(letter.getSenderName());
		writeS(letter.getTitle());
		writeS(letter.getMessage());

		Item item = letter.getAttachedItem();
		if (item != null) {
			ItemTemplate itemTemplate = item.getItemTemplate();

			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeD(1); // unk
			writeD(0); // unk
			writeNameId(itemTemplate.getNameId());

			ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());
		}
		else {
			writeQ(0);
			writeQ(0);
			writeD(0);
		}

		writeD((int) letter.getAttachedKinah());
		writeD(0); // AP reward for castle assault/defense (in future)
		writeC(0);
		writeD((int) (time / 1000));
		writeC(letter.getLetterType().getId()); // mail type
	}

	protected void writeLetterState(int letterId, int attachmentType) {
		writeD(letterId);
		writeC(attachmentType);
		writeC(1);
	}

	protected void writeLetterDelete(int totalCount, int unreadCount, int expressCount, int blackCloudCount, int... letterIds) {
		writeD(totalCount + unreadCount * 0x10000); // total count + unread hex
		writeD(expressCount + blackCloudCount); // unread express + BC letters count
		writeH(letterIds.length);
		for (int letterId : letterIds)
			writeD(letterId);
	}

}
