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

import org.typezero.gameserver.controllers.attack.AttackUtil;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.skillengine.effect.modifier.ActionModifier;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.ThreadPoolManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DelayedSpellAttackInstantEffect")
public class DelayedSpellAttackInstantEffect extends DamageEffect {

	@XmlAttribute
	protected int delay;

	@Override
	public void applyEffect(final Effect effect) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (effect.getEffector().isEnemy(effect.getEffected()))
					calculateAndApplyDamage(effect);
			}
		}, delay);
	}

	private void calculateAndApplyDamage(Effect effect) {
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		ActionModifier modifier = getActionModifiers(effect);
		int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * effect.getSkillLevel();
		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg, shared, false);
		effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), TYPE.DELAYDAMAGE, effect.getReserved1(), true, LOG.PROCATKINSTANT);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}
}
