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
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("celestius")
public class CelestiusAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> helpersTask;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startHelpersCall();

		}
	}

	private void cancelHelpersTask() {
		if (helpersTask != null && !helpersTask.isDone()) {
			helpersTask.cancel(true);
		}
	}

	private void startHelpersCall() {
		helpersTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead() && getLifeStats().getHpPercentage() < 90) {
					deleteHelpers();
					cancelHelpersTask();
				}
				else {
					deleteHelpers();
					SkillEngine.getInstance().getSkill(getOwner(), 18981, 44, getOwner()).useNoAnimationSkill();
					startRun((Npc) spawn(281514, 518, 813, 1378, (byte) 0), "3001900001");
					startRun((Npc) spawn(281514, 551, 795, 1376, (byte) 0), "3001900002");
					startRun((Npc) spawn(281514, 574, 854, 1375, (byte) 0), "3001900003");
				}
			}

		}, 1000, 25000);
	}

	private void startRun(Npc npc, String walkId) {
		npc.getSpawn().setWalkerId(walkId);
		WalkManager.startWalking((NpcAI2) npc.getAi2());
		npc.setState(1);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}

	private void deleteHelpers() {
		WorldPosition p = getPosition();
		if (p != null) {
			WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null) {
				List<Npc> npcs = instance.getNpcs(281514);
				for (Npc npc : npcs) {
					SpawnTemplate template =  npc.getSpawn();
					if (npc != null && (template.getX() == 518 || template.getX() == 551 || template.getX() == 574)) {
						npc.getController().onDelete();
					}
				}
			}
		}
	}

	@Override
	protected void handleBackHome() {
		cancelHelpersTask();
		deleteHelpers();
		isHome.set(true);
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		cancelHelpersTask();
		deleteHelpers();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancelHelpersTask();
		deleteHelpers();
		super.handleDied();
	}

}
