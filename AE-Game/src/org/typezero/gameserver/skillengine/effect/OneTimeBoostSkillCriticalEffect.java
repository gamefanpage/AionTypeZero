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

import org.typezero.gameserver.controllers.attack.AttackStatus;
import org.typezero.gameserver.controllers.observer.AttackCalcObserver;
import org.typezero.gameserver.controllers.observer.AttackerCriticalStatus;
import org.typezero.gameserver.controllers.observer.AttackerCriticalStatusObserver;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillCriticalEffect")
public class OneTimeBoostSkillCriticalEffect extends EffectTemplate {

	@XmlAttribute
	private int count;
	@XmlAttribute
	private boolean percent;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		AttackerCriticalStatusObserver observer = new AttackerCriticalStatusObserver(AttackStatus.CRITICAL, count, value, percent) {

			@Override
			public AttackerCriticalStatus checkAttackerCriticalStatus(AttackStatus stat, boolean isSkill) {
				if (stat == this.status && isSkill) {
					if (this.getCount() <= 1)
						effect.endEffect();
					else
						this.decreaseCount();

					this.acStatus.setResult(true);
				}
				else
					this.acStatus.setResult(false);

				return this.acStatus;
			}
		};
		effect.getEffected().getObserveController().addAttackCalcObserver(observer);
		effect.setAttackStatusObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);

		AttackCalcObserver observer = effect.getAttackStatusObserver(position);
		effect.getEffected().getObserveController().removeAttackCalcObserver(observer);
	}

	/**
	 * @return the percent
	 */
	public boolean isPercent() {
		return percent;
	}


}
