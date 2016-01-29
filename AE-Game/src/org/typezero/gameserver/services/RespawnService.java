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

package org.typezero.gameserver.services;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.team2.group.events.PlayerConnectedEvent;
import org.typezero.gameserver.model.templates.spawns.Spawn;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author ATracer, Source, xTz
 */
public class RespawnService {
	private static final int IMMEDIATE_DECAY = 90 * 1000;
	private static final int WITHOUT_DROP_DECAY = (int)(1.5 * 60 * 1000);
	private static final int WITH_DROP_DECAY = 5 * 60 * 1000;
    private static final Logger log = LoggerFactory.getLogger(RespawnService.class);

    /**
	 * @param npc
	 * @return Future<?>
	 */
	public static Future<?> scheduleDecayTask(Npc npc) {
		int decayInterval;
		Set<DropItem> drop = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());

		if(drop == null)
			decayInterval = IMMEDIATE_DECAY;
		else if(drop.isEmpty())
			decayInterval = WITHOUT_DROP_DECAY;
		else
			decayInterval = WITH_DROP_DECAY;

		return scheduleDecayTask(npc, decayInterval);
	}

	public static Future<?> scheduleDecayTask(Npc npc, long decayInterval) {
		return ThreadPoolManager.getInstance().schedule(new DecayTask(npc.getObjectId()), decayInterval);
	}

	/**
	 * @param visibleObject
	 */
	public static final Future<?> scheduleRespawnTask(VisibleObject visibleObject) {
		final int interval = visibleObject.getSpawn().getRespawnTime() + Rnd.get(visibleObject.getSpawn().getRandomSpawntime());
		SpawnTemplate spawnTemplate = visibleObject.getSpawn();
		int instanceId = visibleObject.getInstanceId();
        //For testing log in system WARN random spawn time. Comment or del if no need
             if (visibleObject.getSpawn().getRandomSpawntime() != 0){
                    log.info("[RND_SPAWN] "+interval+ " s, NpcID " +World.getInstance().getNpcByObjectId(visibleObject.getObjectId()));
              }
        return ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000);
	}

	/**
	 * @param spawnTemplate
	 * @param instanceId
	 */
	private static final VisibleObject respawn(SpawnTemplate spawnTemplate, final int instanceId) {
		if (spawnTemplate.isTemporarySpawn() && !spawnTemplate.getTemporarySpawn().canSpawn() && !spawnTemplate.getTemporarySpawn().isInSpawnTime())
			return null;

		int worldId = spawnTemplate.getWorldId();
		boolean instanceExists = InstanceService.isInstanceExist(worldId, instanceId);
		if (spawnTemplate.isNoRespawn() || !instanceExists) {
			return null;
		}

		if (spawnTemplate.hasPool()) {
			spawnTemplate = spawnTemplate.changeTemplate();
		}
		return SpawnEngine.spawnObject(spawnTemplate, instanceId);
	}

	private static class DecayTask implements Runnable {

		private final int npcId;

		DecayTask(int npcId) {
			this.npcId = npcId;
		}

		@Override
		public void run() {
			VisibleObject visibleObject = World.getInstance().findVisibleObject(npcId);
			if (visibleObject != null) {
				visibleObject.getController().onDelete();
			}
		}

	}

	private static class RespawnTask implements Runnable {

		private final SpawnTemplate spawn;
		private final int instanceId;

		RespawnTask(SpawnTemplate spawn, int instanceId) {
			this.spawn = spawn;
			this.instanceId = instanceId;
		}

		@Override
		public void run() {
			VisibleObject visibleObject = spawn.getVisibleObject();
			if (visibleObject != null && visibleObject instanceof Npc) {
				((Npc) visibleObject).getController().cancelTask(TaskId.RESPAWN);
			}
			respawn(spawn, instanceId);
		}

	}

}
