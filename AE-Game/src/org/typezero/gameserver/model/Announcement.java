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

package org.typezero.gameserver.model;

/**
 * This class represents an announcement
 *
 * @author Divinity
 */
public class Announcement {

	private int id;
	private String faction;
	private String announce;
	private String chatType;
	private int delay;

	/**
	 * Constructor without the ID of announcement
	 *
	 * @param announce
	 * @param faction
	 * @param chatType
	 * @param delay
	 */
	public Announcement(String announce, String faction, String chatType, int delay) {
		this.announce = announce;

		// Checking the right syntax
		if (!faction.equalsIgnoreCase("ELYOS") && !faction.equalsIgnoreCase("ASMODIANS"))
			faction = "ALL";

		this.faction = faction;
		this.chatType = chatType;
		this.delay = delay;
	}

	/**
	 * Constructor with the ID of announcement
	 *
	 * @param id
	 * @param announce
	 * @param faction
	 * @param chatType
	 * @param delay
	 */
	public Announcement(int id, String announce, String faction, String chatType, int delay) {
		this.id = id;
		this.announce = announce;

		// Checking the right syntax
		if (!faction.equalsIgnoreCase("ELYOS") && !faction.equalsIgnoreCase("ASMODIANS"))
			faction = "ALL";

		this.faction = faction;
		this.chatType = chatType;
		this.delay = delay;
	}

	/**
	 * Return the id of the announcement In case of the id doesn't exist, return -1
	 *
	 * @return int - Announcement's id
	 */
	public int getId() {
		if (id != 0)
			return id;
		else
			return -1;
	}

	/**
	 * Return the announcement's text
	 *
	 * @return String - Announcement's text
	 */
	public String getAnnounce() {
		return announce;
	}

	/**
	 * Return the announcement's faction in string mode : - ELYOS - ASMODIANS - ALL
	 *
	 * @return String - Announcement's faction
	 */
	public String getFaction() {
		return faction;
	}

	/**
	 * Return the announcement's faction in Race enum mode : - Race.ELYOS - Race.ASMODIANS
	 *
	 * @return Race - Announcement's faction
	 */
	public Race getFactionEnum() {
		if (faction.equalsIgnoreCase("ELYOS"))
			return Race.ELYOS;
		else if (faction.equalsIgnoreCase("ASMODIANS"))
			return Race.ASMODIANS;

		return null;
	}

	/**
	 * Return the chatType in String mode (for the insert in database)
	 *
	 * @return String - Announcement's chatType
	 */
	public String getType() {
		return chatType;
	}

	/**
	 * Return the chatType with the ChatType Enum
	 *
	 * @return ChatType - Announcement's chatType
	 */
	public ChatType getChatType() {
		if (chatType.equalsIgnoreCase("System"))
			return ChatType.BRIGHT_YELLOW_CENTER;
		else if (chatType.equalsIgnoreCase("White"))
			return ChatType.WHITE_CENTER;
		else if (chatType.equalsIgnoreCase("Yellow"))
			return ChatType.YELLOW_CENTER;
		else if (chatType.equalsIgnoreCase("Shout"))
			return ChatType.SHOUT;
		else if (chatType.equalsIgnoreCase("Orange"))
			return ChatType.GROUP_LEADER;
		else
			return ChatType.GOLDEN_YELLOW;
	}

	/**
	 * Return the announcement's delay
	 *
	 * @return int - Announcement's delay
	 */
	public int getDelay() {
		return delay;
	}
}
