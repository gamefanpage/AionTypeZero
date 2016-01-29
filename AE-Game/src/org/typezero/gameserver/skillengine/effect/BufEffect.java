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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatAddFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatRateFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatSetFunction;
import org.typezero.gameserver.model.stats.container.CreatureGameStats;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.skillengine.change.Change;
import org.typezero.gameserver.skillengine.condition.Conditions;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BufEffect")
public abstract class BufEffect extends EffectTemplate {

	@XmlAttribute
	protected boolean maxstat;

	private static final Logger log = LoggerFactory.getLogger(BufEffect.class);

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	/**
	 * Will be called from effect controller when effect ends
	 */
	@Override
	public void endEffect(Effect effect) {
		Creature effected = effect.getEffected();
		effected.getGameStats().endEffect(effect);
	}

	/**
	 * Will be called from effect controller when effect starts
	 */
	@Override
	public void startEffect(Effect effect) {
		if (change == null)
			return;

		Creature effected = effect.getEffected();
		CreatureGameStats<? extends Creature> cgs = effected.getGameStats();

		List<IStatFunction> modifiers = getModifiers(effect);

		if (modifiers.size() > 0)
			cgs.addEffect(effect, modifiers);

		if (maxstat) {
			effected.getLifeStats().increaseHp(TYPE.HP, effected.getGameStats().getMaxHp().getCurrent());
			effected.getLifeStats().increaseMp(TYPE.HEAL_MP, effected.getGameStats().getMaxMp().getCurrent());
		}
	}

	/**
	 * @param effect
	 * @return
	 */
	protected List<IStatFunction> getModifiers(Effect effect) {
		int skillId = effect.getSkillId();
		int skillLvl = effect.getSkillLevel();

		List<IStatFunction> modifiers = new ArrayList<IStatFunction>();

		for (Change changeItem : change) {
			if (changeItem.getStat() == null) {
				log.warn("Skill stat has wrong name for skillid: " + skillId);
				continue;
			}

			int valueWithDelta = changeItem.getValue() + changeItem.getDelta() * skillLvl;

			Conditions conditions = changeItem.getConditions();
			switch (changeItem.getFunc()) {
				case ADD:
					modifiers.add(new StatAddFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
					break;
				case PERCENT:
					modifiers.add(new StatRateFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
					break;
				case REPLACE:
					modifiers.add(new StatSetFunction(changeItem.getStat(), valueWithDelta).withConditions(conditions));
					break;
			}
		}
		return modifiers;
	}

	@Override
	public void onPeriodicAction(Effect effect) {
		// TODO Auto-generated method stub
	}
}
