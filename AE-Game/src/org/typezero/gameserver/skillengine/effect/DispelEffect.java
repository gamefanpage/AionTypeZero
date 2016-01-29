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

import org.typezero.gameserver.skillengine.model.DispelType;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTargetSlot;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author ATracer
 */
public class DispelEffect extends EffectTemplate {

	@XmlElement(type = Integer.class)
	protected List<Integer> effectids;
	@XmlElement
	protected List<String> effecttype;
	@XmlElement
	protected List<String> slottype;
	@XmlAttribute
	protected DispelType dispeltype;
	@XmlAttribute
	protected Integer value;

	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffected() == null || effect.getEffected().getEffectController() == null)
			return;

		if (dispeltype == null)
			return;

		if ((dispeltype == DispelType.EFFECTID || dispeltype == DispelType.EFFECTIDRANGE) &&
			effectids == null)
			return;

		if (dispeltype == DispelType.EFFECTTYPE && effecttype == null)
			return;

		if (dispeltype == DispelType.SLOTTYPE && slottype == null)
			return;

		switch (dispeltype) {
			case EFFECTID:
				for (Integer effectId : effectids) {
					effect.getEffected().getEffectController().removeEffectByEffectId(effectId);
				}
				break;
			case EFFECTIDRANGE:
				for (int i = effectids.get(0); i <= effectids.get(1); i++) {
					effect.getEffected().getEffectController().removeEffectByEffectId(i);
				}
				break;
			case EFFECTTYPE:
				for (String type : effecttype) {
					EffectType temp = null;
					try {
						temp = EffectType.valueOf(type);
					} catch (Exception e) {
						log.error("wrong effecttype in dispeleffect "+type);
					}
					if (temp != null)
						effect.getEffected().getEffectController().removeEffectByEffectType(temp);
				}
				break;
			case SLOTTYPE:
				for (String type : slottype) {
					effect.getEffected().getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.valueOf(type));
				}
				break;
		}
	}
}
