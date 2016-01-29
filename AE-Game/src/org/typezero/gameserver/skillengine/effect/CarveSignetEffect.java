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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.skillengine.action.DamageType;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarveSignetEffect")
public class CarveSignetEffect extends DamageEffect {

	@XmlAttribute(required = true)
	protected int signetlvlstart;
	@XmlAttribute(required = true)
	protected int signetlvl;
	@XmlAttribute(required = true)
	protected int signetid;
	@XmlAttribute(required = true)
	protected String signet;
	@XmlAttribute(required = true)
	protected int prob = 100;

	private int nextSignetLevel = 1;

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);

		if (Rnd.get(0, 100) > prob)
			return;

        //если метка выше лвлом на цели, не обновляем
        if (nextSignetLevel < effect.getCarvedSignet()) {
            return;
        }

		Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);

		if (placedSignet != null)
			placedSignet.endEffect();

		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(signetid + nextSignetLevel - 1);
		Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, nextSignetLevel, 0);
		newEffect.initialize();
		newEffect.applyEffect();
	}

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, DamageType.PHYSICAL))
			return;
		Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		nextSignetLevel = signetlvlstart > 0 ? signetlvlstart : 1;
		effect.setCarvedSignet(nextSignetLevel);
		if (placedSignet != null) {
			nextSignetLevel = placedSignet.getSkillId() - this.signetid + 2;
            //for 1 skill with signet lvl
			if ((signetlvlstart > 0) && (effect.getCarvedSignet() < signetlvlstart))
				nextSignetLevel = signetlvlstart;

			effect.setCarvedSignet(nextSignetLevel);
			if (nextSignetLevel > signetlvl || nextSignetLevel > 5)
				nextSignetLevel--;
		}
	}
}
