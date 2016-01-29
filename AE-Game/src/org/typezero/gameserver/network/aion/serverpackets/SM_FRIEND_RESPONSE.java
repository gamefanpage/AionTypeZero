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


import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * Replies to a request to add or delete a friend
 *
 * @author Ben
 */
public class SM_FRIEND_RESPONSE extends AionServerPacket {

	/**
	 * The friend was successfully added to your list
	 */
	public static final int TARGET_ADDED = 0x00;
	/**
	 * The target of a friend request is offline
	 */
	public static final int TARGET_OFFLINE = 0x01;
	/**
	 * The target is already a friend
	 */
	public static final int TARGET_ALREADY_FRIEND = 0x02;
	/**
	 * The target does not exist
	 */
	public static final int TARGET_NOT_FOUND = 0x03;
	/**
	 * The friend denied your request to add him
	 */
	public static final int TARGET_DENIED = 0x04;
	/**
	 * The target's friend list is full
	 */
	public static final int TARGET_LIST_FULL = 0x05;
	/**
	 * The friend was removed from your list
	 */
	public static final int TARGET_REMOVED = 0x06;
	/**
	 * The target is in your blocked list, and cannot be added to your friends list.
	 */
	public static final int TARGET_BLOCKED = 0x08;
	/**
	 * The target is dead and cannot be befriended yet.
	 */
	public static final int TARGET_DEAD = 0x09;
	public static final int TARGET_NOTE = 0x21;

	private final String player;
	private final int code;

	public SM_FRIEND_RESPONSE(String playerName, int messageType) {
		player = playerName;
		code = messageType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {

		writeS(player);
		writeC(code);
	}

}
