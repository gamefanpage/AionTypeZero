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

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.NpcObjectType;
import org.typezero.gameserver.model.gameobjects.Servant;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.skillengine.properties.FirstTargetAttribute;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonServantEffect")
public class SummonServantEffect extends SummonEffect {

	private static final Logger log = LoggerFactory.getLogger(SummonServantEffect.class);

	@XmlAttribute(name = "skill_id", required = true)
	protected int skillId;

	@Override
	public void applyEffect(Effect effect) {
		Creature effector = effect.getEffector();
		float x = effector.getX();
		float y = effector.getY();
		float z = effector.getZ();
		spawnServant(effect, time, NpcObjectType.SERVANT, x, y, z);
	}

	/**
	 * @param effect
	 * @param time
	 */
	protected Servant spawnServant(Effect effect, int time, NpcObjectType npcObjectType, float x, float y, float z) {
		Creature effector = effect.getEffector();
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();

		final Creature target = (Creature) effector.getTarget();
		final Creature effected = (Creature) effect.getEffected();

		SkillTemplate template = effect.getSkillTemplate();

		if (template.getProperties().getFirstTarget() != FirstTargetAttribute.ME && target == null) {
			log.warn("Servant trying to attack null target!!");
			return null;
		}

		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final Servant servant = VisibleObjectSpawner.spawnServant(spawn, instanceId, effector, skillId,
			effect.getSkillLevel(), npcObjectType);

		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				servant.getController().onDelete();
			}
		}, time * 1000);
		servant.getController().addTask(TaskId.DESPAWN, task);
		if (servant.getNpcObjectType() != NpcObjectType.TOTEM)
			servant.getAi2().onCreatureEvent(AIEventType.ATTACK, (target != null ? target: effected));
		return servant;
	}
}
