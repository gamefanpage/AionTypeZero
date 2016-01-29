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


import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * Massage [chat, etc]
 * 
 * @author -Nemesiss-, Sweetkr
 */
public class SM_MESSAGE extends AionServerPacket {

	/**
	 * Player.
	 */
	private Player player;
	/**
	 * Object that is saying smth or null.
	 */
	private int senderObjectId;

	/**
	 * Message.
	 */
	private String message;

	/**
	 * Name of the sender
	 */
	private String senderName;

	/**
	 * Sender race
	 */
	private Race race;

	/**
	 * Chat type
	 */
	private ChatType chatType;

	/**
	 * Sender coordinates
	 */
	private float x;
	private float y;
	private float z;

	/**
	 * Constructs new <tt>SM_MESSAGE </tt> packet
	 * 
	 * @param player
	 *          who sent message
	 * @param message
	 *          actual message
	 * @param chatType
	 *          what chat type should be used
	 */
	public SM_MESSAGE(Player player, String message, ChatType chatType) {
		this.player = player;
		this.senderObjectId = player.getObjectId();
		this.senderName = player.getName();
		this.message = message;
		this.race = player.getRace();
		this.chatType = chatType;
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
	}

	/**
	 * Manual creation of chat message.<br>
	 * 
	 * @param senderObjectId
	 *          - can be 0 if system message(like announcements)
	 * @param senderName
	 *          - used for shout ATM, can be null in other cases
	 * @param message
	 *          - actual text
	 * @param chatType
	 *          type of chat, Normal, Shout, Announcements, Etc...
	 */
	public SM_MESSAGE(int senderObjectId, String senderName, String message, ChatType chatType) {
		this.senderObjectId = senderObjectId;
		this.senderName = senderName;
		this.message = message;
		this.chatType = chatType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		boolean canRead = true;

		if (race != null) {
			canRead = chatType.isSysMsg() || CustomConfig.SPEAKING_BETWEEN_FACTIONS || player.getAccessLevel() > 0
				|| (con.getActivePlayer() != null && con.getActivePlayer().getAccessLevel() > 0);
		}

		writeC(chatType.toInteger()); // type

		/*
		 * 0 : all 1 : elyos 2 : asmodians
		 */
		writeC(canRead ? 0 : race.getRaceId() + 1);
		writeD(senderObjectId); // sender object id

		switch (chatType) {
			case NORMAL:
			case GOLDEN_YELLOW:
			case WHITE:
			case YELLOW:
			case BRIGHT_YELLOW:
			case WHITE_CENTER:
			case YELLOW_CENTER:
			case BRIGHT_YELLOW_CENTER:
				writeH(0x00); // unknown
				writeS(message);
				break;
			case SHOUT:
				writeS(senderName);
				writeS(message);
				writeF(x);
				writeF(y);
				writeF(z);
				break;
			case ALLIANCE:
			case GROUP:
			case GROUP_LEADER:
			case LEGION:
			case WHISPER:
			case LEAGUE:
			case LEAGUE_ALERT:
			case CH1:
			case CH2:
			case CH3:
			case CH4:
			case CH5:
			case CH6:
			case CH7:
			case CH8:
			case CH9:
			case CH10:
			case COMMAND:
				writeS(senderName);
				writeS(message);
				break;
		}
	}
}
