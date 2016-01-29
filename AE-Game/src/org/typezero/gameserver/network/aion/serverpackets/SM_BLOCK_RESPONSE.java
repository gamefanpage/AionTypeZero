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
 * Responses to block list related requests
 *
 * @author Ben
 */
public class SM_BLOCK_RESPONSE extends AionServerPacket {

	/**
	 * You have blocked %0
	 */
	public static final int BLOCK_SUCCESSFUL = 0;
	/**
	 * You have unblocked %0
	 */
	public static final int UNBLOCK_SUCCESSFUL = 1;
	/**
	 * That character does not exist.
	 */
	public static final int TARGET_NOT_FOUND = 2;
	/**
	 * Your Block List is full.
	 */
	public static final int LIST_FULL = 3;
	/**
	 * You cannot block yourself.
	 */
	public static final int CANT_BLOCK_SELF = 4;

	private int code;
	private String playerName;

	/**
	 * Constructs a new block request response packet
	 *
	 * @param code
	 *          Message code to use - see class constants
	 * @param playerName
	 *          Parameters inserted into message. Usually the target player's name
	 */
	public SM_BLOCK_RESPONSE(int code, String playerName) {
		this.code = code;
		this.playerName = playerName;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeS(playerName);
		writeD(code);

	}
}
