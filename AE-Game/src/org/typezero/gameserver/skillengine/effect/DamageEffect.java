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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.controllers.attack.AttackUtil;
import org.typezero.gameserver.skillengine.action.DamageType;
import org.typezero.gameserver.skillengine.change.Func;
import org.typezero.gameserver.skillengine.effect.modifier.ActionModifier;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageEffect")
public abstract class DamageEffect extends EffectTemplate {

	@XmlAttribute
	protected Func mode = Func.ADD;
	@XmlAttribute
	protected boolean shared;

	@Override
	public void applyEffect(Effect effect) {
	  effect.getEffected().getController() .onAttack(effect.getEffector(), effect.getSkillId(), effect.getReserved1(), true);
	  effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}

	public boolean calculate(Effect effect, DamageType damageType) {
		if (!super.calculate(effect, null, null))
			return false;

		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		ActionModifier modifier = getActionModifiers(effect);
		int accMod = this.accMod2 + this.accMod1 * skillLvl;
		int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * skillLvl;
		switch (damageType) {
			case PHYSICAL:
				boolean cannotMiss = false;
				if (this instanceof SkillAttackInstantEffect) {
					cannotMiss = ((SkillAttackInstantEffect)this).isCannotmiss();
				}
				int rndDmg = (this instanceof SkillAttackInstantEffect ? ((SkillAttackInstantEffect)this).getRnddmg() : 0);
				AttackUtil.calculateSkillResult(effect, valueWithDelta, modifier, this.getMode(), rndDmg, accMod, this.critProbMod2, critAddDmg, cannotMiss, shared, false);
				break;
			case MAGICAL:
				boolean useKnowledge = true;

                if (this instanceof ProcAtkInstantEffect) {
					useKnowledge = false;
				}
				AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
	          //TODO Double damage of skill
              //   if (this instanceof SpellAttackInstantEffect && ((SpellAttackInstantEffect)this).isTwice()) {
              //  AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
              //}
                break;
			default:
				AttackUtil.calculateSkillResult(effect, 0, null, this.getMode(), 0, accMod, 100, 0, false, shared, false);
		}

		return true;
	}

	public Func getMode() {
		return mode;
	}

}
