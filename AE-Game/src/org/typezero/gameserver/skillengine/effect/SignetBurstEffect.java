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
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignetBurstEffect")
public class SignetBurstEffect extends DamageEffect {

   @XmlAttribute
   protected int signetlvl;
   @XmlAttribute
   protected String signet;

   @Override
   public void calculate(Effect effect) {

	  Effect signetEffect = effect.getEffected().getEffectController().getAnormalEffect(signet);
	  if (!super.calculate(effect, DamageType.MAGICAL)) {
		 if (signetEffect != null)
			signetEffect.endEffect();
		 return;
	  }
	  int valueWithDelta = value + delta * effect.getSkillLevel();
	  int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * effect.getSkillLevel();

	  if (signetEffect == null) {
		 valueWithDelta *= 0.05f;
		 AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, null, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg, shared, false);
		 effect.setLaunchSubEffect(false);
	  }
	  else {

		 int level = signetEffect.getSkillLevel();
		 effect.setSignetBurstedCount(level);
		 switch (level) {
			case 1:
			   valueWithDelta *= 0.2f;
			   break;
			case 2:
			   valueWithDelta *= 0.5f;
			   break;
			case 3:
			   valueWithDelta *= 1.0f;
			   break;
			case 4:
			   valueWithDelta *= 1.2f;
			   break;
			case 5:
			   valueWithDelta *= 1.5f;
			   break;
		 }

		 /**
		  * custom bonuses for magical accurancy according to rune level and effector level follows same logic as damage
		  */
		 int accmod = 0;
		 int mAccurancy = effect.getEffector().getGameStats().getMainHandMAccuracy().getCurrent();
		 switch (level) {
			case 1:
			   accmod = (int) (-0.8f * mAccurancy);
			   break;
			case 2:
			   accmod = (int) (-0.5f * mAccurancy);
			   break;
			case 3:
			   accmod = 0;
			   break;
			case 4:
			   accmod = (int) (0.2f * mAccurancy);
			   break;
			case 5:
			   accmod = (int) (0.5f * mAccurancy);
			   break;
		 }
		 effect.setAccModBoost(accmod);

		 AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, null, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg, shared, false);

		 if (signetEffect != null) {
			signetEffect.endEffect();
		 }
	  }
   }
}
