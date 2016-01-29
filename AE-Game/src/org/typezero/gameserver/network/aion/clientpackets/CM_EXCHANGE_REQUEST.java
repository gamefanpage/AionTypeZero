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
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.DeniedStatus;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.ExchangeService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author -Avol-
 */
public class CM_EXCHANGE_REQUEST extends AionClientPacket {

	public Integer targetObjectId;

	private static final Logger log = LoggerFactory.getLogger(CM_EXCHANGE_REQUEST.class);

	public CM_EXCHANGE_REQUEST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		targetObjectId = readD();
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = World.getInstance().findPlayer(targetObjectId);

		if (targetPlayer == null) {
			log.warn("CM_EXCHANGE_REQUEST null target from {} to {}", activePlayer.getObjectId(), targetObjectId);
			return;
		}
		/**
		 * check if not trading with yourself.
		 */
		if (!activePlayer.equals(targetPlayer)) {
			/**
			 * check distance between players.
			 */
			if (activePlayer.getKnownList().getObject(targetPlayer.getObjectId()) == null) {
				log.info("[AUDIT] Player " + activePlayer.getName() + " tried trade with player (" + targetPlayer.getName()
					+ ") not from knownlist.");
				return;
			}
			if (!activePlayer.getRace().equals(targetPlayer.getRace())) {
				log.info("[AUDIT] Player " + activePlayer.getName() + " tried trade with player (" + targetPlayer.getName()
					+ ") another race.");
				return;
			}
			/**
			 * check if trade partner exists or is he/she a player.
			 */
			if (targetPlayer != null) {
				if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.TRADE)) {
					sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_TRADE(targetPlayer.getName()));
					return;
				}
				sendPacket(SM_SYSTEM_MESSAGE.STR_EXCHANGE_ASKED_EXCHANGE_TO_HIM(targetPlayer.getName()));
				RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						ExchangeService.getInstance().registerExchange(activePlayer, targetPlayer);
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						PacketSendUtility.sendPacket(activePlayer, new SM_SYSTEM_MESSAGE(
							SystemMessageId.EXCHANGE_HE_REJECTED_EXCHANGE, targetPlayer.getName()));
					}
				};

				boolean requested = targetPlayer.getResponseRequester().putRequest(
					SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE, responseHandler);
				if (requested) {
					PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(
						SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE, 0, 0, activePlayer.getName()));
				}
			}
		}
		else {
			// TODO: send message, cannot trade with yourself.
		}
	}
}
