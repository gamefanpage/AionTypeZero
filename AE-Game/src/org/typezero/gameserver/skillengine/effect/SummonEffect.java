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

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Simple
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonEffect")
public class SummonEffect extends EffectTemplate {

	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	@XmlAttribute(name = "time", required = true)
	protected int time; // in seconds

	@Override
	public void applyEffect(Effect effect) {
		Player effected = (Player) effect.getEffected();
		SummonsService.createSummon(effected, npcId,  effect.getSkillId(), effect.getSkillLevel(), time);
		if ((time > 0) && (effect.getEffected() instanceof Player)) {
			final Player effector = (Player)effect.getEffected();
			final Summon summon = effector.getSummon();
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if ((summon != null) && (summon.isSpawned())) {
						SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
					}
				}
			}, time * 1000);
			summon.getController().addTask(TaskId.DESPAWN, task);
		}
	}

	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}
}
