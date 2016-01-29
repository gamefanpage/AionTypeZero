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
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.KiskService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Source
 */
@AIName("kisk")
public class KiskAI2 extends NpcAI2 {

        private final int CANCEL_DIALOG_METERS = 5;

	@Override
	public Kisk getOwner() {
		return (Kisk) super.getOwner();
	}

	@Override
	protected void handleAttack(Creature creature) {
		if (getLifeStats().isFullyRestoredHp())
			for (Player member : getOwner().getCurrentMemberList())
				PacketSendUtility.sendPacket(member, STR_BINDSTONE_IS_ATTACKED);
	}

	@Override
	protected void handleDied() {
		if (isAlreadyDead()) {
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.DIE, 0, 0));
			getOwner().broadcastPacket(STR_BINDSTONE_IS_DESTROYED);
		}

		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		KiskService.getInstance().removeKisk(getOwner());
		if (!isAlreadyDead())
			getOwner().broadcastPacket(STR_BINDSTONE_IS_REMOVED);

		super.handleDespawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getKisk() == getOwner()) {
			PacketSendUtility.sendPacket(player, STR_BINDSTONE_ALREADY_REGISTERED);
			return;
		}

		if (getOwner().canBind(player)) {
			AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_REGISTER_BINDSTONE, getOwner().getObjectId(), CANCEL_DIALOG_METERS, new AI2Request() {

                                private boolean decisionTaken = false;

				@Override
				public void acceptRequest(Creature requester, Player responder) {
                                        if(!decisionTaken) {
                                                // Check again if it's full (If they waited to press OK)
                                                if (!getOwner().canBind(responder)) {
                                                        PacketSendUtility.sendPacket(responder, STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
                                                        return;
                                                }
                                                KiskService.getInstance().onBind(getOwner(), responder);
                                        }
                                }

                                @Override
                                public void denyRequest(Creature requester, Player responder) {
                                        decisionTaken = true;
                                }
			});

		}
		else if (getOwner().getCurrentMemberCount() >= getOwner().getMaxMembers())
			PacketSendUtility.sendPacket(player, STR_CANNOT_REGISTER_BINDSTONE_FULL);
		else
			PacketSendUtility.sendPacket(player, STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

}
