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

package ai.instance.elementisForest;


import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Luzien
 */
@AIName("terma")
public class TrappedTermaAI2 extends NpcAI2 {

	private boolean isMove;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isMove) {
			isMove = true;
	  moveToDead();
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		Npc freeTerma = (Npc)spawn(205495, 455.94f, 537.06f, 132.6f,(byte) 0);
		spawn(701009, 451.706f, 534.313f, 131.979f, (byte) 0);
		NpcShoutsService.getInstance().sendMsg(freeTerma, 1500444, freeTerma.getObjectId(), 0, 3000);
	}

	private void moveToDead() {
		((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
		WalkManager.startWalking((NpcAI2) getOwner().getAi2());
		getOwner().setState(1);
		getOwner().getMoveController().moveToPoint(455.93f, 537.2f, 132.55f);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		dead();
	}

	private void dead() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				getOwner().getController().die();
			}

		}, 16000);
	}

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	public int modifyDamage(int damage) {
		return 1;
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
