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

package ai;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Request;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, nrg
 */
@AIName("groupgate")
public class GroupGateAI2 extends NpcAI2 {

	private final int CANCEL_DIALOG_METERS = 9;

	@Override
	protected void handleDialogStart(Player player) {

		boolean isMember = false;
		int creatorId = getCreatorId();
		if (player.getObjectId().equals(creatorId)) {
			isMember = true;
		}
		else if (player.isInGroup2()) {
			isMember = player.getPlayerGroup2().hasMember(creatorId);
		}

		if (isMember && player.getLevel() >= 10) {

			AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, getOwner().getObjectId(), CANCEL_DIALOG_METERS,
					new AI2Request() {

						private boolean decisionTaken = false;

						@Override
						public void acceptRequest(Creature requester, Player responder) {
							if (!decisionTaken) {
								switch (getNpcId()) {
									// Group Gates
									case 749017:
									case 833208:
										TeleportService2.teleportTo(responder, 110010000, 1444.9f, 1577.2f, 572.9f, (byte) 0, TeleportAnimation.JUMP_AIMATION);
										break;
									case 749083:
									case 833207:
										TeleportService2.teleportTo(responder, 120010000, 1657.5f, 1398.7f, 194.7f, (byte) 0, TeleportAnimation.JUMP_AIMATION);
										break;
									// Binding Group Gates
									case 749131:
									case 749132:
										TeleportService2.moveToBindLocation(responder, true);
										break;
								}
								decisionTaken = true;
							}
						}

						@Override
						public void denyRequest(Creature requester, Player responder) {
							decisionTaken = true;
						}

					});

		}
		else {
			PacketSendUtility.sendPacket(player, STR_SKILL_CAN_NOT_USE_GROUPGATE_NO_RIGHT);
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		getOwner().setVisualState(CreatureVisualState.HIDE10);
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

}
