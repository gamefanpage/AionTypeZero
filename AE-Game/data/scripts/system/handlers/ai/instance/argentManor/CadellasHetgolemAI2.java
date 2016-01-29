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

package ai.instance.argentManor;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("cadellas_hetgolem")
public class CadellasHetgolemAI2 extends GeneralNpcAI2 {

	private AtomicBoolean isDestroyed = new AtomicBoolean(false);

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		setStateIfNot(AIState.FOLLOWING);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 19571, 55, getOwner()).useNoAnimationSkill();
				}
			}

		}, 1000);
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		getMoveController().abortMove();
		if (isDestroyed.compareAndSet(false, true)) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1500462, getObjectId(), 0, 0);
			SkillEngine.getInstance().getSkill(getOwner(), getHealSkill(), 60, getOwner()).useNoAnimationSkill();
			startDespawnTask();
		}
	}

	private void startDespawnTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(CadellasHetgolemAI2.this);
				}
			}

		}, 4000);
	}

	private int getHealSkill() {
		switch(getNpcId()) {
			case 282345:
				return 19525;
			case 282346:
				return 19526;
			case 282347:
				return 19527;
			case 282348:
				return 19528;
			case 282349:
				return 19529;
			default:
				return 0;
		}
	}

	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_RESIST_ABNORMAL:
				return AIAnswers.POSITIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
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
