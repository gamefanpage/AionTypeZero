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

package org.typezero.gameserver.skillengine.properties;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.model.DispelCategoryType;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class FirstTargetProperty {

	/**
	 * @param skill
	 * @param properties
	 * @return
	 */
	public static final boolean set(Skill skill, Properties properties) {

		FirstTargetAttribute value = properties.getFirstTarget();
		skill.setFirstTargetAttribute(value);
		switch (value) {
			case ME:
				skill.setFirstTargetRangeCheck(false);
				skill.setFirstTarget(skill.getEffector());
				break;
			case TARGETORME:
				boolean changeTargetToMe = false;
				if (skill.getFirstTarget() == null) {
					skill.setFirstTarget(skill.getEffector());
				}
				else if (skill.getFirstTarget().isAttackableNpc()) {
					Player playerEffector = (Player) skill.getEffector();
					if (skill.getFirstTarget().isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if ((skill.getFirstTarget() instanceof Player) && (skill.getEffector() instanceof Player)) {
					Player playerEffected = (Player) skill.getFirstTarget();
					Player playerEffector = (Player) skill.getEffector();
					if (!playerEffected.getRace().equals(playerEffector.getRace()) || playerEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if (skill.getFirstTarget() instanceof Npc) {
					Npc npcEffected = (Npc) skill.getFirstTarget();
					Player playerEffector = (Player) skill.getEffector();
					if (npcEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if ((skill.getFirstTarget() instanceof Summon) && (skill.getEffector() instanceof Player)) {
					Summon summon = (Summon) skill.getFirstTarget();
					Player playerEffected = summon.getMaster();
					Player playerEffector = (Player) skill.getEffector();
					if (playerEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				if (changeTargetToMe) {
					if (skill.getEffector() instanceof Player)
						PacketSendUtility.sendPacket((Player) skill.getEffector(),
							SM_SYSTEM_MESSAGE.STR_SKILL_AUTO_CHANGE_TARGET_TO_MY);
					skill.setFirstTarget(skill.getEffector());
				}
				break;
			case TARGET:
				// Exception for effect skills which are not used directly
				if (skill.getSkillId() > 8000 && skill.getSkillId() < 9000)
					break;
				// Exception for NPC skills which applied on players
				if (skill.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_BUFF
					|| skill.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_DEBUFF_PHYSICAL)
					break;

				if (skill.getFirstTarget() == null || skill.getFirstTarget().equals(skill.getEffector())) {
					if (skill.getEffector() instanceof Player) {
						if (skill.getSkillTemplate().getProperties().getTargetType() == TargetRangeAttribute.AREA)
							return skill.getFirstTarget() != null;

						TargetRelationAttribute relation = skill.getSkillTemplate().getProperties().getTargetRelation();
						TargetRangeAttribute type = skill.getSkillTemplate().getProperties().getTargetType();
						if ((relation != TargetRelationAttribute.ALL && relation != TargetRelationAttribute.MYPARTY)
										|| type == TargetRangeAttribute.PARTY || skill.getSkillId() == 3069) { //TODO: Remove ID, find logic!
							PacketSendUtility.sendPacket((Player) skill.getEffector(),
								SM_SYSTEM_MESSAGE.STR_SKILL_TARGET_IS_NOT_VALID);
							return false;
						}
					}
				}
				break;
			case MYPET:
				Creature effector = skill.getEffector();
				if (effector instanceof Player) {
					Summon summon = ((Player) effector).getSummon();
					if (summon != null)
						skill.setFirstTarget(summon);
					else
						return false;
				}
				else {
					return false;
				}
				break;
			case MYMASTER:
				Creature peteffector = skill.getEffector();
				if (peteffector instanceof Summon) {
					Player player = ((Summon) peteffector).getMaster();
					if (player != null)
						skill.setFirstTarget(player);
					else
						return false;
				}
				else {
					return false;
				}
				break;
			case PASSIVE:
				skill.setFirstTarget(skill.getEffector());
				break;
			case TARGET_MYPARTY_NONVISIBLE:
				Creature effected = skill.getFirstTarget();
				if (effected == null || skill.getEffector() == null)
					return false;
				if (!(effected instanceof Player) || !(skill.getEffector() instanceof Player) || !((Player) skill.getEffector()).isInGroup2())
					return false;
				boolean myParty = false;
				for (Player member : ((Player)skill.getEffector()).getPlayerGroup2().getMembers()) {
					if (member == skill.getEffector())
						continue;
					if (member == effected) {
						myParty = true;
						break;
					}
				}
				if (!myParty)
					return false;

				skill.setFirstTargetRangeCheck(false);
				break;
			case POINT:
				skill.setFirstTarget(skill.getEffector());
				skill.setFirstTargetRangeCheck(false);
				return true;
			default:
				break;
		}

		if (skill.getFirstTarget() != null)
			skill.getEffectedList().add(skill.getFirstTarget());
		return true;
	}
}
