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

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Luzien, xTz
 */
@AIName("tuali")
public class TualiAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isStart = new AtomicBoolean(false);
	private AtomicBoolean isStart65Event = new AtomicBoolean(false);
	private AtomicBoolean isStart45Event = new AtomicBoolean(false);
	private AtomicBoolean isStart25Event = new AtomicBoolean(false);
	private Future<?> task;

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isStart.compareAndSet(false, true)) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1500454, getObjectId(), true, 0, 0);
			scheduleSkills();

			task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), 19348, 60, getOwner()).useNoAnimationSkill();
						int size = getPosition().getWorldMapInstance().getNpcs(282308).size();
						for (int i = 0; i < 6; i++) {
							if (size >= 12) {
								break;
							}
							size++;
							rndSpawn(282307);
						}
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1401378, 6000);
					}
				}

			}, 20000, 50000);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 65) {
			if (isStart65Event.compareAndSet(false, true)) {
				buff();
			}
		}
		if (hpPercentage <= 45) {
			if (isStart45Event.compareAndSet(false, true)) {
				buff();
			}
		}
		if (hpPercentage <= 25) {
			if (isStart25Event.compareAndSet(false, true)) {
				buff();
			}
		}
	}

	private void buff() {
		SkillEngine.getInstance().getSkill(getOwner(), 19511, 60, getOwner()).useNoAnimationSkill();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500456, getObjectId(), true, 0, 0);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1401041, 3500);
	}

	private void rndSpawn(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(5, 12);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
	}

	@Override
	public void handleDied() {
		cancelTask();
		super.handleDied();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500457, getObjectId(), true, 0, 0);
	}

	private void cancelTask() {
		if (task != null && !task.isDone()) {
			task.cancel(true);
		}
	}

	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}

	@Override
	public void handleBackHome() {
		cancelTask();
		WorldPosition p = getPosition();
		if (p != null) {
			WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null) {
				deleteNpcs(instance.getNpcs(282307));
				deleteNpcs(instance.getNpcs(282308));
			}
		}
		super.handleBackHome();
		isStart65Event.set(false);
		isStart45Event.set(false);
		isStart25Event.set(false);
		isStart.set(false);
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void scheduleSkills() {
		if (isAlreadyDead() || !isStart.get()) {
			return;
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.useSkill(TualiAI2.this, 19512 + Rnd.get(5));
				scheduleSkills();
			}
		}, Rnd.get(18, 22) * 1000);
	}
}
