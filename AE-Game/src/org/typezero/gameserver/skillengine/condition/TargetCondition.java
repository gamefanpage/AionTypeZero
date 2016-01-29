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

package org.typezero.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.skillengine.properties.FirstTargetAttribute;
import org.typezero.gameserver.skillengine.properties.TargetRangeAttribute;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetCondition")
public class TargetCondition extends Condition {

	@XmlAttribute(required = true)
	protected TargetAttribute value;

	/**
	 * Gets the value of the value property.
	 *
	 * @return possible object is {@link TargetAttribute }
	 */
	public TargetAttribute getValue() {
		return value;
	}

	@Override
	public boolean validate(Skill skill) {
		if (value == TargetAttribute.NONE || value == TargetAttribute.ALL)
			return true;
		if (skill.getSkillTemplate().getProperties().getTargetType().equals(TargetRangeAttribute.AREA))
			return true;
		if (skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGET &&
			skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGETORME)
			return true;
		if (skill.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.TARGETORME &&
			skill.getEffector() == skill.getFirstTarget())
			return true;

		boolean result = false;
		switch (value) {
			case NPC:
				result = (skill.getFirstTarget() instanceof Npc);
				break;
			case PC:
				result = (skill.getFirstTarget() instanceof Player);
				break;
		}

		if (!result && skill.getEffector() instanceof Player)
			PacketSendUtility.sendPacket((Player)skill.getEffector(), SM_SYSTEM_MESSAGE.STR_SKILL_TARGET_IS_NOT_VALID);

		return result;
	}
}
