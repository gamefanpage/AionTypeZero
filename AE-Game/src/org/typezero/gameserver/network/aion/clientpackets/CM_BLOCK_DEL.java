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

import org.typezero.gameserver.model.gameobjects.player.BlockedPlayer;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SocialService;

/**
 * @author Ben
 */
public class CM_BLOCK_DEL extends AionClientPacket {

	private static Logger log = LoggerFactory.getLogger(CM_BLOCK_DEL.class);

	private String targetName;

	/**
	 * @param opcode
	 */
	public CM_BLOCK_DEL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetName = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		BlockedPlayer target = activePlayer.getBlockList().getBlockedPlayer(targetName);
		if (target == null) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_BUDDYLIST_NOT_IN_LIST);
		}
		else {
			if (!SocialService.deleteBlockedUser(activePlayer, target.getObjId())) {
				log.debug("Could not unblock " + targetName + " from " + activePlayer.getName()
					+ " blocklist. Check database setup.");
			}
		}
	}
}
