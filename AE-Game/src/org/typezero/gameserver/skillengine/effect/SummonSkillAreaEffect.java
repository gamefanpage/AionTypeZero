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
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.NpcObjectType;
import org.typezero.gameserver.model.gameobjects.Servant;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonSkillAreaEffect")
public class SummonSkillAreaEffect extends SummonServantEffect {

	@Override
	public void applyEffect(Effect effect) {
		// should only be set if player has no target to avoid errors
		if (effect.getEffector().getTarget() == null)
			effect.getEffector().setTarget(effect.getEffector());
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if(x == 0 && y == 0){
			Creature effected = effect.getEffected();
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		// fix for summon whirlwind
		// TODO revisit later and find better fix - kecimis
		int useTime = time;
		switch (effect.getSkillId()) {
			case 2291:
			case 2292:
			case 2293:
			case 2294:
				useTime = 7;
				break;
		}

		final Servant servant = spawnServant(effect, useTime, NpcObjectType.SKILLAREA, x, y, z);
		Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				servant.getController().useSkill(skillId);
			}
		}, 0, 3000);
		servant.getController().addTask(TaskId.SKILL_USE, task);
	}
}
