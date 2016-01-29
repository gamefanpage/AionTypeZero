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

package ai.instance.muadasTrencher;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
@AIName("sand_squall")
public class SandSquallAI2 extends AggressiveNpcAI2 {

	private Future<?> lifeTask;

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startLifeTask();
		castSkillTask(19896, 500);
		castSkillTask(19894, 500);
		castSkillTask(19894, 2500);
		castSkillTask(20444, 4500);
		castSkillTask(19894, 6500);
		castSkillTask(19894, 8500);
		castSkillTask(19894, 10500);
		castSkillTask(20444, 12500);
		castSkillTask(19894, 14500);
		castSkillTask(19894, 16500);
		castSkillTask(19895, 18500);
	}

	@Override
	protected void handleDespawned() {
		cancelLifeTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancelLifeTask();
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

	private void castSkillTask(final int skill, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					WorldPosition p = getPosition();
					if (p != null) {
						WorldMapInstance instance = p.getWorldMapInstance();
						if (instance != null) {
							SkillEngine.getInstance().getSkill(getOwner(), skill, 60, getOwner()).useNoAnimationSkill();
						}
					}
				}
			}

		}, time);
	}

	private void startLifeTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(SandSquallAI2.this);
				}
			}

		}, 20000);
	}

	private void cancelLifeTask() {
		if (lifeTask != null && !lifeTask.isDone()) {
			lifeTask.cancel(true);
		}
	}

	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_ATTACK_PLAYER:
				return AIAnswers.POSITIVE;
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
