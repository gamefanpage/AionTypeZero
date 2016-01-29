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

package ai.instance.aturamSkyFortress;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
@AIName("shulack_guided_bomb")
public class ShulackGuidedBombAI2 extends AggressiveNpcAI2 {
	private boolean isDestroyed;
	private boolean isHome = true;
	Future<?> task;

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (task != null) {
			task.cancel(true);
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		starLifeTask();
	}

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome) {
			isHome = false;
			doSchedule(creature);
		}
	}

	private void starLifeTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead() && !isDestroyed) {
					despawn();
				}
			}

		}, 10000);
	}

	private void doSchedule(final Creature creature) {

		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead() && !isDestroyed) {
					destroy(creature);
				}
				else {
					if (task != null) {
						task.cancel(true);
					}
				}
			}

		}, 1000, 1000);

	}

	private void despawn() {
		if (!isAlreadyDead()) {
			AI2Actions.deleteOwner(this);
		}
	}

	private void destroy(Creature creature) {
		if (!isDestroyed && !isAlreadyDead()) {
			if (creature != null && MathUtil.getDistance(getOwner(), creature) <= 4) {
				isDestroyed = true;
				SkillEngine.getInstance().getSkill(getOwner(), 19415, 49, getOwner()).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						despawn();
					}

				}, 3200);
			}
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
