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
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
@AIName("popuchin")
public class PopuchinAI2 extends AggressiveNpcAI2 {

	private boolean isHome = true;
	private Future<?> bombTask;

	private void startBombTask() {
		if (!isAlreadyDead() && !isHome) {
			bombTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead() && !isHome) {
						VisibleObject target = getTarget();
						if (target != null && target instanceof Player) {
							SkillEngine.getInstance().getSkill(getOwner(), 19413, 49, target).useNoAnimationSkill();
						}
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								if (!isAlreadyDead() && !isHome) {
									SkillEngine.getInstance().getSkill(getOwner(), 19412, 49, getOwner()).useNoAnimationSkill();
									ThreadPoolManager.getInstance().schedule(new Runnable() {

										@Override
										public void run() {
											if (!isAlreadyDead() && !isHome && getOwner().isSpawned()) {
												if (getLifeStats().getHpPercentage() > 50) {
													WorldPosition p = getPosition();
													if (p != null && p.getWorldMapInstance() != null) {
														spawn(217374, p.getX(), p.getY(), p.getZ(), p.getHeading());
														spawn(217374, p.getX(), p.getY(), p.getZ(), p.getHeading());
														startBombTask();
													}
												}
												else {
													spawnRndBombs();
													startBombTask();
												}
											}
										}

									}, 1500);
								}
							}

						}, 3000);
					}
				}

			}, 15500);
		}
	}

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome) {
			isHome = false;
			getPosition().getWorldMapInstance().getDoors().get(68).setOpen(false);
			startBombTask();
		}
	}

	private void rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
	}

	@Override
	protected void handleBackHome() {
		isHome = true;
		super.handleBackHome();
		getPosition().getWorldMapInstance().getDoors().get(68).setOpen(true);
		if (bombTask != null && !bombTask.isDone()) {
			bombTask.cancel(true);
		}
	}

	private void spawnRndBombs() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead() && !isHome) {
					for (int i = 0; i < 10; i++) {
						rndSpawnInRange(217375, Rnd.get(1, 12));
					}
				}
			}

		}, 1500);

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
	protected void handleDied() {
		super.handleDied();
	}

}
