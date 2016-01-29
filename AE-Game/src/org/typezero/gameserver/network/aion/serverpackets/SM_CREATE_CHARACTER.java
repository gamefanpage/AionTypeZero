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


import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.PlayerInfo;

/**
 * This packet is response for CM_CREATE_CHARACTER
 *
 * @author Nemesiss, AEJTester
 */
public class SM_CREATE_CHARACTER extends PlayerInfo {

	/** If response is ok */
	public static final int RESPONSE_OK = 0x00;

	public static final int FAILED_TO_CREATE_THE_CHARACTER = 1;
	/** Failed to create the character due to world db error */
	public static final int RESPONSE_DB_ERROR = 2;
	/** The number of characters exceeds the maximum allowed for the server */
	public static final int RESPONSE_SERVER_LIMIT_EXCEEDED = 4;
	/** Invalid character name */
	public static final int RESPONSE_INVALID_NAME = 5;
	/** The name includes forbidden words */
	public static final int RESPONSE_FORBIDDEN_CHAR_NAME = 9;
	/** A character with that name already exists */
	public static final int RESPONSE_NAME_ALREADY_USED = 10;
	/** The name is already reserved */
	public static final int RESPONSE_NAME_RESERVED = 11;
	/** You cannot create characters of other races in the same server */
	public static final int RESPONSE_OTHER_RACE = 12;
	/** Character Creator initialize for creating the char */
	public static final int RESPONSE_CREATE_CHAR = 22;

	/**
	 * response code
	 */
	private final int responseCode;

	/**
	 * Newly created player.
	 */
	private final PlayerAccountData player;

	/**
	 * Constructs new <tt>SM_CREATE_CHARACTER </tt> packet
	 *
	 * @param accPlData
	 *          playerAccountData of player that was created
	 * @param responseCode
	 *          response code (invalid nickname, nickname is already taken, ok)
	 */

	public SM_CREATE_CHARACTER(PlayerAccountData accPlData, int responseCode) {
		this.player = accPlData;
		this.responseCode = responseCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(responseCode);

        if (responseCode == RESPONSE_OK) {
            writePlayerInfo(player); // if everything is fine, all the character's data should be sent
            writeB(new byte[44]);
        } else {
            writeB(new byte[616]); // if something is wrong, only return code should be sent in the packet
        }
    }
}
