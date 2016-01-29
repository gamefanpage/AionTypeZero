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
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("unfinished_pletus")
public class UnfinishedPletusAI2 extends GeneralNpcAI2 {

	private Future<?> phaseTask;
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isSpawnedHelpers = new AtomicBoolean(false);

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			getPosition().getWorldMapInstance().getDoors().get(26).setOpen(false);
			startPhaseTask();
			sendMsg(1500466);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 75) {
			if (isSpawnedHelpers.compareAndSet(false, true)) {
				startWalker((Npc) spawn(282146, 880.497f, 1091.01f, 91.2582f, (byte) 0), "3001500001");
				startWalker((Npc) spawn(282146, 880.497f, 1091.01f, 91.2582f, (byte) 0), "3001500002");
			}
		}
	}

	private void startWalker(Npc npc, String walkerId) {
		npc.getSpawn().setWalkerId(walkerId);
		WalkManager.startWalking((NpcAI2) npc.getAi2());
		npc.setState(1);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	private void cancelSkillTask() {
		if (skillTask != null && !skillTask.isDone()) {
			skillTask.cancel(true);
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
					sendMsg(1500467);
					SkillEngine.getInstance().getSkill(getOwner(), 19304, 60, getOwner()).useNoAnimationSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead()) {
								sendMsg(1500469);
								SkillEngine.getInstance().getSkill(getOwner(), 19300, 60, getOwner()).useNoAnimationSkill();
								starSkillEvent();
							}
						}

					}, 3000);
				}
			}

		}, 34000, 60000);
	}

	private void starSkillEvent() {
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					sendMsg(1500468);
					SkillEngine.getInstance().getSkill(getOwner(), 19303, 60, getOwner()).useNoAnimationSkill();
				}
			}

		}, 30000);
	}

	private void deleteHelpers() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			deleteNpcs(instance.getNpcs(282148));
			deleteNpcs(instance.getNpcs(282146));
		}
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
	}

	@Override
	protected void handleDied() {
		sendMsg(1500470);
		cancelSkillTask();
		cancelPhaseTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			instance.getDoors().get(26).setOpen(true);
			instance.getDoors().get(158).setOpen(true);
			instance.getDoors().get(10).setOpen(true);
		}
		spawn(701013, 928.74146f, 1090.8639f, 91.22978f, (byte) 0);
		super.handleDied();
		deleteHelpers();
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelPhaseTask();
		cancelSkillTask();
		deleteHelpers();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelPhaseTask();
		cancelSkillTask();
		getPosition().getWorldMapInstance().getDoors().get(26).setOpen(true);
		isSpawnedHelpers.set(false);
		isHome.set(true);
		deleteHelpers();
	}
}
