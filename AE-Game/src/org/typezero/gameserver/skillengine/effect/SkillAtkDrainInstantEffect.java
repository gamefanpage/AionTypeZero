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

import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.skillengine.action.DamageType;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillAtkDrainInstantEffect")
public class SkillAtkDrainInstantEffect extends DamageEffect {

	@XmlAttribute(name = "hp_percent")
	protected int hp_percent;
	@XmlAttribute(name = "mp_percent")
	protected int mp_percent;

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		if (hp_percent != 0) {
			effect.getEffector().getLifeStats().increaseHp(TYPE.ABSORBED_HP, effect.getReserved1() * hp_percent / 100, effect.getSkillId(), LOG.SKILLLATKDRAININSTANT);
		}
		if (mp_percent != 0) {
			effect.getEffector().getLifeStats().increaseMp(TYPE.MP, effect.getReserved1() * mp_percent / 100, effect.getSkillId(), LOG.SKILLLATKDRAININSTANT);
		}
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, DamageType.PHYSICAL);
	}
}
