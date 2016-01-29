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

package org.typezero.gameserver.spawnengine;

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.TemporarySpawn;
import javolution.util.FastList;

/**
 * @author xTz
 */
public class TemporarySpawnEngine {

	private static final FastList<SpawnGroup2> temporarySpawns = new FastList<SpawnGroup2>();

	public static void spawnAll() {
		spwan(true);
	}

	public static void onHourChange() {
		despawn();
		spwan(false);
	}

	private static void despawn() {
		for (SpawnGroup2 spawn : temporarySpawns) {
			for (SpawnTemplate template : spawn.getSpawnTemplates()) {
				if (template.getTemporarySpawn().canDespawn()) {
					VisibleObject object = template.getVisibleObject();
					if (object == null) {
						continue;
					}
					if (object instanceof Npc) {
						Npc npc = (Npc) object;
						if (!npc.getLifeStats().isAlreadyDead() && template.hasPool()) {
							template.setUse(false);
						}
						npc.getController().cancelTask(TaskId.RESPAWN);
					}
					if (object.isSpawned()) {
						object.getController().onDelete();
					}
				}
			}
		}
	}

	private static void spwan(boolean startCheck) {
		for (SpawnGroup2 spawn : temporarySpawns) {
			if (spawn.hasPool()) {
				TemporarySpawn temporarySpawn = spawn.geTemporarySpawn();
				if (temporarySpawn.canSpawn() || (startCheck && spawn.getRespawnTime() != 0
						&& temporarySpawn.isInSpawnTime())) {
					for (int pool = 0; pool < spawn.getPool(); pool++) {
						SpawnTemplate template = spawn.getRndTemplate();
						SpawnEngine.spawnObject(template, 1);
					}
				}
			}
			else {
				for (SpawnTemplate template : spawn.getSpawnTemplates()) {
					TemporarySpawn temporarySpawn = template.getTemporarySpawn();
					if (temporarySpawn.canSpawn() || (startCheck && !template.isNoRespawn()
							&& temporarySpawn.isInSpawnTime())) {
						SpawnEngine.spawnObject(template, 1);
					}
				}
			}
		}
	}

	/**
	 * @param spawnTemplate
	 */
	public static void addSpawnGroup(SpawnGroup2 spawn) {
		temporarySpawns.add(spawn);
	}
}
