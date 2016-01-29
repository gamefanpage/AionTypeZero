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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.DeniedStatus;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SocialService;
import org.typezero.gameserver.world.World;

/**
 * Received when a user tries to add someone as his friend
 *
 * @author Ben
 */
public class CM_FRIEND_ADD extends AionClientPacket {

	private String targetName;

	public CM_FRIEND_ADD(int opcode, State state, State... restStates) {
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

		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = World.getInstance().findPlayer(targetName);

		if (targetName.equalsIgnoreCase(activePlayer.getName())) {
			// Adding self to friend list not allowed - Its blocked by the client by default, so no need to send an error
		}
		// if offline
		else if (targetPlayer == null) {
			sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_OFFLINE));
		}
		else if (activePlayer.getFriendList().getFriend(targetPlayer.getObjectId()) != null) {
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_ALREADY_FRIEND));
		}
		else if (activePlayer.getFriendList().isFull()) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_BUDDYLIST_LIST_FULL);
		}
                else if (activePlayer.getCommonData().getRace() != targetPlayer.getCommonData().getRace()) {
                        sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_NOT_FOUND));
                }
                else if (targetPlayer.getFriendList().isFull()) {
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_LIST_FULL));
		}
		else if (activePlayer.getBlockList().contains(targetPlayer.getObjectId())) {
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_BLOCKED));
		}
		else if (targetPlayer.getBlockList().contains(activePlayer.getObjectId())) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_YOU_EXCLUDED(targetName));
		}
		else // Send request
		{
			RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer) {

				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (!targetPlayer.getCommonData().isOnline()) {
						sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_OFFLINE));
					}
					else if (activePlayer.getFriendList().isFull() || responder.getFriendList().isFull()) {
						return;
					}
					else {
						SocialService.makeFriends((Player) requester, responder);
					}

				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_DENIED));

				}
			};

			boolean requested = targetPlayer.getResponseRequester().putRequest(
				SM_QUESTION_WINDOW.STR_BUDDYLIST_ADD_BUDDY_REQUEST, responseHandler);
			// If the player is busy and could not be asked
			if (!requested) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_BUDDYLIST_BUSY);
			}
			else {
				if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.FRIEND)) {
					sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_FRIEND(targetPlayer.getName()));
					return;
				}
				// Send question packet to buddy
				targetPlayer.getClientConnection().sendPacket(
					new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BUDDYLIST_ADD_BUDDY_REQUEST, activePlayer.getObjectId(), 0,
						activePlayer.getName()));
			}
		}
	}

}
