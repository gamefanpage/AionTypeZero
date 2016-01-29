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

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.player.DeniedStatus;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.DuelService;

/**
 * @author xavier
 */
public class CM_DUEL_REQUEST extends AionClientPacket {

	/**
	 * Target object id that client wants to start duel with
	 */
	private int objectId;

	/**
	 * Constructs new instance of <tt>CM_DUEL_REQUEST</tt> packet
	 *
	 * @param opcode
	 */
	public CM_DUEL_REQUEST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		objectId = readD();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		AionObject target = activePlayer.getKnownList().getObject(objectId);

		if(!CustomConfig.INSTANCE_DUEL_ENABLE && activePlayer.isInInstance())
			return;

		if (target == null)
			return;

		if (target instanceof Player && !((Player) target).equals(activePlayer)) {
			DuelService duelService = DuelService.getInstance();

			Player targetPlayer = (Player) target;

			if (duelService.isDueling(activePlayer.getObjectId())) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_DUEL_YOU_ARE_IN_DUEL_ALREADY);
				return;
			}
			if (duelService.isDueling(targetPlayer.getObjectId())) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_IN_DUEL_ALREADY(target.getName()));
				return;
			}
			if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.DUEL)) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_DUEL(targetPlayer.getName()));
				return;
			}
			duelService.onDuelRequest(activePlayer, targetPlayer);
			duelService.confirmDuelWith(activePlayer, targetPlayer);
		}
		else {
			sendPacket(SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_INVALID(target.getName()));
		}
	}
}
