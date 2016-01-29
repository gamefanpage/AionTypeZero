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
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.geometry.Point3D;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("empress_muada")
public class EmpressMuadaAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean sandSquallEvent = new AtomicBoolean(false);
	private Future<?> phaseTask;
	private Future<?> enrageTask;
	private boolean think = true;

	@Override
	public boolean canThink() {
		return think;
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			startEnrageTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 75) {
			if (startedEvent.compareAndSet(false, true)) {
				startPhaseTask();
			}
		}
		if (hpPercentage <= 50) {
			if (startedEvent.compareAndSet(true, false)) {
				cancelPhaseTask();
				removeHelpers();
			}
		}
		if (hpPercentage <= 25) {
			if (sandSquallEvent.compareAndSet(false, true)) {
				think = false;
				EmoteManager.emoteStopAttacking(getOwner());
				SkillEngine.getInstance().getSkill(getOwner(), 20499, 2, getOwner()).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						if (!isAlreadyDead()) {
							SkillEngine.getInstance().getSkill(getOwner(), 19893, 2, getOwner()).useNoAnimationSkill();
							spawn(282533, 523.1f, 541.1f, 106.7f, (byte) 0);
							NpcShoutsService.getInstance().sendMsg(getOwner(), 1401300);
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									if (!isAlreadyDead()) {
										think = true;
										Creature creature = getAggroList().getMostHated();
										if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
											setStateIfNot(AIState.FIGHT);
											think();
										}
										else {
											getMoveController().abortMove();
											getOwner().setTarget(creature);
											getOwner().getGameStats().renewLastAttackTime();
											getOwner().getGameStats().renewLastAttackedTime();
											getOwner().getGameStats().renewLastChangeTargetTime();
											getOwner().getGameStats().renewLastSkillTime();
											setStateIfNot(AIState.FIGHT);
											handleMoveValidate();
											SkillEngine.getInstance().getSkill(getOwner(), 19897, 2, getOwner()).useNoAnimationSkill();
										}
									}
								}

							}, 20000);
						}
					}

				}, 3500);
			}
		}
	}

	private void startEnrageTask() {
		enrageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 19859, 2, getOwner()).useNoAnimationSkill();
					enrageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead()) {
								SkillEngine.getInstance().getSkill(getOwner(), 20089, 2, getOwner()).useNoAnimationSkill();

							}
						}

					}, 15000);
				}
			}

		}, 1200000); // 20 min
	}

	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					SkillEngine.getInstance().getSkill(getOwner(), 19898, 2, getOwner()).useNoAnimationSkill();
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401299);
                                        Npc Shirik = getPosition().getWorldMapInstance().getNpc(282535);
                                    if (Shirik == null) {
                                        spawn(282535, getOwner().getX() + 5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
                                        spawn(282535, getOwner().getX() + 10, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
                                        spawn(282535, getOwner().getX() -5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
                                        spawn(282535, getOwner().getX() -6, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);

					}
				}
			}

		}, 1000, 45000);
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void removeHelpers() {
		WorldPosition p = getPosition();
		if (p != null) {
			WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null) {
				deleteNpcs(instance.getNpcs(282556));
				deleteNpcs(instance.getNpcs(282535));
				deleteNpcs(instance.getNpcs(282539));
			}
		}
	}

	private Point3D getRndPos() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(2, 5);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		return new Point3D(p.getX() + x1, p.getY() + y1, p.getZ());
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	private void cancelEnrageTask() {
		if (enrageTask != null && !enrageTask.isDone()) {
			enrageTask.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		cancelEnrageTask();
		cancelPhaseTask();
		getEffectController().removeEffect(19859);
		removeHelpers();
		think = true;
		isAggred.set(false);
		sandSquallEvent.set(false);
		startedEvent.set(false);
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		cancelEnrageTask();
		cancelPhaseTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		removeHelpers();
		cancelEnrageTask();
		cancelPhaseTask();
		super.handleDied();
	}

}
