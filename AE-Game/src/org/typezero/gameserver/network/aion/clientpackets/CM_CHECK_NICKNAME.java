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


package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import org.typezero.gameserver.network.aion.serverpackets.SM_NICKNAME_CHECK_RESPONSE;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.player.PlayerService;

/**
 * In this packets aion client is asking if given nickname is ok/free?.
 *
 * @author -Nemesiss-
 * @modified cura
 */
public class CM_CHECK_NICKNAME extends AionClientPacket {

	/**
	 * nick name that need to be checked
	 */
	private String nick;

	/**
	 * Constructs new instance of <tt>CM_CHECK_NICKNAME </tt> packet
	 *
	 * @param opcode
	 */
	public CM_CHECK_NICKNAME(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		nick = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();

		if (!PlayerService.isFreeName(nick) || PlayerService.isOldName(nick)) {
			if (GSConfig.CHARACTER_CREATION_MODE == 2)
				client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_NAME_RESERVED));
			else
				client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
		}
		else if (!NameRestrictionService.isValidName(nick)) {
			client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
		}
    else if (NameRestrictionService.isForbiddenWord(nick)) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_FORBIDDEN_CHAR_NAME));
		}
		else {
			client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_OK));
		}
	}
}
