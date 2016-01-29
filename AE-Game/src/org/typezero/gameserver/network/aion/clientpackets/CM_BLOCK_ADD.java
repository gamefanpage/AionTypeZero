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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SocialService;
import org.typezero.gameserver.world.World;

/**
 * @author Ben
 */
public class CM_BLOCK_ADD extends AionClientPacket {

	private static Logger log = LoggerFactory.getLogger(CM_BLOCK_ADD.class);

	private String targetName;
	private String reason;

	public CM_BLOCK_ADD(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetName = readS();
		reason = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {

		Player activePlayer = getConnection().getActivePlayer();

		Player targetPlayer = World.getInstance().findPlayer(targetName);

		// Trying to block self
		if (activePlayer.getName().equalsIgnoreCase(targetName)) {
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.CANT_BLOCK_SELF, targetName));
		}

		// List full
		else if (activePlayer.getBlockList().isFull()) {
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.LIST_FULL, targetName));
		}

		// Player offline
		else if (targetPlayer == null) {
			sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.TARGET_NOT_FOUND, targetName));
		}

		// Player is your friend
		else if (activePlayer.getFriendList().getFriend(targetPlayer.getObjectId()) != null) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_BLOCKLIST_NO_BUDDY);
		}

		// Player already blocked
		else if (activePlayer.getBlockList().contains(targetPlayer.getObjectId())) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_BLOCKLIST_ALREADY_BLOCKED);
		}

		// Try and block player
		else if (!SocialService.addBlockedUser(activePlayer, targetPlayer, reason)) {
			log.error("Failed to add " + targetPlayer.getName() + " to the block list for " + activePlayer.getName()
				+ " - check database setup.");
		}

	}

}
