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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.LeftHandSlot;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Cheatkiller
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LeftHandCondition")
public class LeftHandCondition extends Condition {

	@XmlAttribute(name = "type")
	private LeftHandSlot type;


	@Override
	public boolean validate(Skill env) {
		if (env.getEffector() instanceof Player) {
			Player player = (Player) env.getEffector();
			switch (type) {
                case DUAL: {
                    if (player.getEquipment().getOffHandWeapon() != null && player.getEquipment().getOffHandWeapon().getItemTemplate().isWeapon()) {
                        return true;
                    } else if (player.getEquipment().getMainHandWeapon() != null && player.getEquipment().isSlotEquipped(1)) {
                        return true;
                    } else {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_NEED_DUAL_WEAPON);
                        return false;
                    }
                }
                case SHIELD: {
                    if (player.getEquipment().isShieldEquipped())
                        return true;
                    else {
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_NEED_SHIELD);
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
