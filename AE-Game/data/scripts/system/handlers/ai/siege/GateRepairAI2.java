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
package ai.siege;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Source
 */
@AIName("siege_gaterepair")
public class GateRepairAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(final Player player) {
		RequestResponseHandler gaterepair = new RequestResponseHandler(player) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				RequestResponseHandler repairstone = new RequestResponseHandler(player) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						onActivate(player);
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						// Nothing Happens
					}

				};
				if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, repairstone))
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, player.getObjectId(), 5, new DescriptionId(2 * 716568 + 1)));
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Nothing Happens
			}

		};

		if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, gaterepair))
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, player.getObjectId(), 5));
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	public void onActivate(Player player) {
		// Stert repair process
	}

}
