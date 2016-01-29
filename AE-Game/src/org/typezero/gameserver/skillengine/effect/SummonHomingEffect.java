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

package org.typezero.gameserver.skillengine.effect;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Homing;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonHomingEffect")
public class SummonHomingEffect extends SummonEffect {

	@XmlAttribute(name = "npc_count", required = true)
	protected int npcCount;
	@XmlAttribute(name = "attack_count", required = true)
	protected int attackCount;
	@XmlAttribute(name = "skill_id", required = false)
	protected int skillId;

	@Override
	public void applyEffect(Effect effect) {
		Creature effector = effect.getEffector();
		float x = effector.getX();
		float y = effector.getY();
		float z = effector.getZ();
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();

		for (int i = 0; i < npcCount; i++) {
			SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
			final Homing homing = VisibleObjectSpawner.spawnHoming(spawn, instanceId, effector, attackCount,
				effect.getSkillId(), effect.getSkillLevel(), skillId);

			if (attackCount > 0) {
				ActionObserver observer = new ActionObserver(ObserverType.ATTACK) {

					@Override
					public void attack(Creature creature) {
						homing.setAttackCount(homing.getAttackCount() - 1);
						if (homing.getAttackCount() <= 0)
							homing.getController().onDelete();
					}
				};

				homing.getObserveController().addObserver(observer);
				effect.setActionObserver(observer, position);
			}
			// Schedule a despawn just in case
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if ((homing != null) && (homing.isSpawned()))
						homing.getController().onDelete();
				}
			}, 15 * 1000);
			homing.getController().addTask(TaskId.DESPAWN, task);
			homing.getAi2().onCreatureEvent(AIEventType.ATTACK, effect.getEffected());
		}
	}

}
