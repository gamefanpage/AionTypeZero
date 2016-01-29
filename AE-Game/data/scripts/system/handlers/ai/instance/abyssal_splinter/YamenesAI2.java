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

package ai.instance.abyssal_splinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritsu, Luzien
 */
@AIName("yamennes")
public class YamenesAI2 extends AggressiveNpcAI2 {

	private boolean top;
	private List<Integer> percents = new ArrayList<Integer>();
	private Future<?> portalTask = null;
	private AtomicBoolean isStart = new AtomicBoolean(false);

	@Override
	protected void handleSpawned() {
		addPercent();
		top = true;
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1400732);
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isStart.compareAndSet(false, true)) {
			startTasks();
		}
	}

	private void startTasks() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 19098, 55, getOwner()).useSkill();
				}
			}
		}, 600000);

		portalTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}
				else {
					spawnPortal();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							WorldMapInstance instance = getPosition().getWorldMapInstance();
							deleteNpcs(instance.getNpcs(282107));
							Npc boss = getOwner();
							EmoteManager.emoteStopAttacking(getOwner());
							SkillEngine.getInstance().getSkill(boss, 19282, 55, getTarget()).useSkill();
							spawn(282107, boss.getX() + 10, boss.getY() - 10, boss.getZ(), (byte) 0);
							spawn(282107, boss.getX() - 10, boss.getY() + 10, boss.getZ(), (byte) 0);
							spawn(282107, boss.getX() + 10, boss.getY() + 10, boss.getZ(), (byte) 0);
							boss.clearAttackedCount();
							NpcShoutsService.getInstance().sendMsg(getOwner(), 1400729);
						}
					}, 3000);
				}
			}
		}, 60000, 60000);
	}

	private void spawnPortal() {
		Npc portalA = getPosition().getWorldMapInstance().getNpc(282014);
		Npc portalB = getPosition().getWorldMapInstance().getNpc(282015);
		Npc portalC = getPosition().getWorldMapInstance().getNpc(282131);
		if (portalA == null && portalB == null && portalC == null) {
			if (!top) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1400637);
				spawn(282014, 288.10f, 741.95f, 216.81f, (byte) 3);
				spawn(282015, 375.05f, 750.67f, 216.82f, (byte) 59);
				spawn(282131, 341.33f, 699.38f, 216.86f, (byte) 59);
				top = true;
			}
			else {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1400637);
				spawn(282014, 303.69f, 736.35f, 198.7f, (byte) 0);
				spawn(282015, 335.19f, 708.92f, 198.9f, (byte) 35);
				spawn(282131, 360.23f, 741.07f, 198.7f, (byte) 0);
				top = false;
			}
		}
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null)
				npc.getController().onDelete();
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100});
	}

	private void cancelTask() {
		if (portalTask != null && !portalTask.isDone()) {
			portalTask.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		top = true;
		cancelTask();
		isStart.set(false);
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		cancelTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282107));
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		cancelTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282107));
		super.handleDied();
	}
}
