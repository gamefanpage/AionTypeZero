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

package ai.instance.raksang;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("illusion_maseter_sharik")
public class IllusionMasterSharikAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	private AtomicBoolean started80PercentEvent = new AtomicBoolean(false);
	private int position = 1;
	private int percent = 100;
	private Future<?> phaseTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		WorldPosition p = getPosition();
		if (p.getX() == 738.065f && p.getY() == 311.606f) {
			position = 1;
		}
		else {
			position = 2;
		}
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 30) {
				if (startedEvent.compareAndSet(false, true)) {
					sendMsg(1401112);
				}
			}
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		percent = hpPercentage;
		if (hpPercentage <= 80) {
			if (started80PercentEvent.compareAndSet(false, true)) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401136);
				if (position == 1) {
					spawn(730446, 738.766f, 317.482f, 911.897f, (byte) 0, 5);
				}
				else {
					spawn(730447, 735.909f, 265.696f, 911.897f, (byte) 0, 278);
				}
				startPhaseTask();
			}
		}
	}

	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					sendMsg(1401114);
					SkillEngine.getInstance().getSkill(getOwner(), 19981, 46, getOwner()).useNoAnimationSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead()) {
								SkillEngine.getInstance().getSkill(getOwner(), 19901, 44, getOwner()).useNoAnimationSkill();
								if (percent <= 50) {
									ThreadPoolManager.getInstance().schedule(new Runnable() {

										@Override
										public void run() {
											if (!isAlreadyDead()) {
												SkillEngine.getInstance().getSkill(getOwner(), 19903, 44, getOwner()).useNoAnimationSkill();
											}
										}

									}, 13000);
								}
							}
						}

					}, 3000);
				}
			}

		}, 3000, 40000);
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), false, 0, 0);
	}

	@Override
	protected void handleBackHome() {
		despawnMirrors();
		cancelPhaseTask();
		super.handleBackHome();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1401137);
		if (position == 1) {
			spawn(217425, 736.21704f, 270.8546f, 910.678f, (byte) 53);
		}
		else {
			spawn(217425, 738.065f, 311.606f, 910.678f, (byte) 53);
		}
		AI2Actions.deleteOwner(this);
	}

	private void despawnMirrors() {
		WorldPosition p = getPosition();
		if (p != null) {
			WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null) {
				deleteNpcs(instance.getNpcs(730446));
				deleteNpcs(instance.getNpcs(730447));
			}
		}
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	@Override
	protected void handleDespawned() {
		cancelPhaseTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		despawnMirrors();
		cancelPhaseTask();
		getPosition().getWorldMapInstance().getDoors().get(294).setOpen(true);
		getPosition().getWorldMapInstance().getDoors().get(295).setOpen(true);
		super.handleDied();
	}

}
