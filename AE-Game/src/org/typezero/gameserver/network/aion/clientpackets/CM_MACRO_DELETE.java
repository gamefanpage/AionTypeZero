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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_MACRO_RESULT;
import org.typezero.gameserver.services.player.PlayerService;

/**
 * Packet that is responsible for macro deletion.<br>
 * Client sends id in the macro list.<br>
 * For instance client has 4 macros and we are going to delete macro #3.<br>
 * Client sends request to delete macro #3.<br>
 * And macro #4 becomes macro #3.<br>
 * So we have to use a list to store macros properly.
 *
 * @author SoulKeeper
 */
public class CM_MACRO_DELETE extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_MACRO_DELETE.class);

	/**
	 * Macro id that has to be deleted
	 */
	private int macroPosition;

	/**
	 * Constructs new client packet instance.
	 *
	 * @param opcode
	 */
	public CM_MACRO_DELETE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Reading macro id
	 */
	@Override
	protected void readImpl() {
		macroPosition = readC();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl() {
		log.debug("Request to delete macro #" + macroPosition);

		PlayerService.removeMacro(getConnection().getActivePlayer(), macroPosition);

		sendPacket(SM_MACRO_RESULT.SM_MACRO_DELETED);
	}
}
