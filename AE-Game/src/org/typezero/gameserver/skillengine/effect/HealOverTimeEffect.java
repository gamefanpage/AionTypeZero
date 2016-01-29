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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.HealType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealOverTimeEffect")
public abstract class HealOverTimeEffect extends AbstractOverTimeEffect {

	public void calculate(Effect effect, HealType healType) {
		if (!super.calculate(effect, null, null))
			return;

		Creature effector = effect.getEffector();
		if (effect.getEffected() instanceof Npc) {
			value = effector.getAi2().modifyHealValue(value);
		}
		int valueWithDelta = value + delta * effect.getSkillLevel();
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = 0;
		if (percent)
			possibleHealValue = maxCurValue * valueWithDelta / 100;
		else
			possibleHealValue = valueWithDelta;

		int finalHeal = possibleHealValue;

		if (healType == HealType.HP) {
			int baseHeal = possibleHealValue;
			if (effect.getItemTemplate() == null) {
				int boostHealAdd = effector.getGameStats().getHealBoost().getCurrent();
				// Apply percent Heal Boost bonus (ex. Passive skills)
				int boostHeal = (effector.getGameStats().getStat(StatEnum.HEAL_BOOST, baseHeal).getCurrent() - boostHealAdd);
				// Apply Add Heal Boost bonus (ex. Skills like Benevolence)
				if (boostHealAdd > 0)
					boostHeal += boostHeal * boostHealAdd / 1000;
				finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, boostHeal).getCurrent();
			}
			finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_DEBOOST, finalHeal).getCurrent();
		}
		effect.setReservedInt(position, finalHeal);
		effect.addSucessEffect(this);
	}

	public void onPeriodicAction(Effect effect, HealType healType) {
		Creature effected = effect.getEffected();

		int currentValue = getCurrentStatValue(effect);
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = effect.getReservedInt(position);

		int healValue = maxCurValue - currentValue < possibleHealValue ? (maxCurValue - currentValue) : possibleHealValue;

		if (healValue <= 0)
			return;

		switch (healType) {
			case HP:
				effected.getLifeStats().increaseHp(TYPE.HP, healValue, effect.getSkillId(), LOG.HEAL);
				break;
			case MP:
				effected.getLifeStats().increaseMp(TYPE.MP, healValue, effect.getSkillId(), LOG.MPHEAL);
				break;
			case FP:
				((Player)effected).getLifeStats().increaseFp(TYPE.FP, healValue, effect.getSkillId(), LOG.FPHEAL);
				break;
			case DP:
				((Player)effected).getCommonData().addDp(healValue);
				break;
		}

	}

	protected abstract int getCurrentStatValue(Effect effect);
	protected abstract int getMaxStatValue(Effect effect);
}
