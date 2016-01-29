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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.skillengine.model.TransformType;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;



/**
 * @author kecimis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormCondition")
public class FormCondition extends Condition {

	@XmlAttribute(required = true)
	protected TransformType value;

	/* (non-Javadoc)
	 * @see org.typezero.gameserver.skillengine.condition.Condition#validate(org.typezero.gameserver.skillengine.model.Skill)
	 *
	 * defines tranformtype in which player can cast given skill
	 */
	@Override
	public boolean validate(Skill env) {
		if (env.getEffector() instanceof Player) {
			if (env.getEffector().getTransformModel().isActive() && env.getEffector().getTransformModel().getType() == value)
				return true;
			else {
				PacketSendUtility.sendPacket((Player)env.getEffector(), SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_CAST_IN_THIS_FORM);
				return false;
			}
		}
		else
			return true;
	}
}
