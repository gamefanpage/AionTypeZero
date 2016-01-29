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

package ai.instance.tallocsHollow;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("kinquid")
public class KinquidAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> skillTask;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome.compareAndSet(true, false)) {
			getPosition().getWorldMapInstance().getDoors().get(48).setOpen(false);
			check();
			cancelSkillTask();
			startSkillTask();
		}
	}

	@Override
	protected void handleBackHome() {
		cancelSkillTask();
		isHome.set(true);
		getPosition().getWorldMapInstance().getDoors().get(48).setOpen(true);
		super.handleBackHome();
		despawnDestroyer();
	}

	@Override
	protected void handleDespawned() {
		cancelSkillTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancelSkillTask();
		super.handleDied();
	}

	private void cancelSkillTask() {
		if (skillTask != null && !skillTask.isDone()) {
			skillTask.cancel(true);
		}
	}

	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelSkillTask();
				}
				else {
					SkillEngine.getInstance().getSkill(getOwner(), 19233, 60, getOwner()).useNoAnimationSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead() && getPosition().isSpawned()) {
								SkillEngine.getInstance().getSkill(getOwner(), 19234, 60, getOwner()).useNoAnimationSkill();
							}
						}

					}, 3500);
				}
			}

		}, 35000, 35000);
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				check();
			}

		}, 25000);
	}

	private void despawnDestroyer() {
		Npc cleaveArmor = getPosition().getWorldMapInstance().getNpc(282008);
		if (cleaveArmor != null) {
			cleaveArmor.getController().onDelete();
		}
		Npc accessoryDestruction = getPosition().getWorldMapInstance().getNpc(282009);
		if (accessoryDestruction != null) {
			accessoryDestruction.getController().onDelete();
		}
	}

	private void check() {
		despawnDestroyer();
		if (getPosition().isSpawned() && !isAlreadyDead() && !isHome.get()) {
			int spawnId = 0;
			switch (Rnd.get(1, 2)) {
				case 1:
					spawnId = 282008;
					break;
				case 2:
					spawnId = 282009;
					break;
			}

			switch (Rnd.get(1, 3)) {
				case 1:
					spawn(spawnId, 266.70685f, 680.6733f, 1167.2369f, (byte) 0);
					break;
				case 2:
					spawn(spawnId, 292.02466f, 719.7132f, 1169.3982f, (byte) 0);
					break;
				case 3:
					spawn(spawnId, 263.4334f, 716.73004f, 1170.3693f, (byte) 0);
					break;
			}
		}
		doSchedule();
	}

}
