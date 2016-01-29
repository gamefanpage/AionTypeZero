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
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.skillengine.properties.Properties.CastState;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class FirstTargetRangeProperty {

	/**
	 * @param skill
	 * @param properties
	 */
	public static boolean set(Skill skill, Properties properties, CastState castState) {
		float firstTargetRange = properties.getFirstTargetRange();
		if (!skill.isFirstTargetRangeCheck())
			return true;

		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();

		if (firstTarget == null)
			return false;

		// Add Weapon Range to distance
		if (properties.isAddWeaponRange()) {
			firstTargetRange += (float) skill.getEffector().getGameStats().getAttackRange().getCurrent() / 1000f;
		}

		//on end cast check add revision distance value
		if(!castState.isCastStart())
			firstTargetRange += properties.getRevisionDistance();

		if (firstTarget.getObjectId() == effector.getObjectId()) {
			return true;
		}

		if (!MathUtil.isInAttackRange(effector, firstTarget, firstTargetRange + 2)) {
			if (effector instanceof Player) {
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_ATTACK_TOO_FAR_FROM_TARGET);
			}
			return false;
		}

		// TODO check for all targets too
		// Summon Group Member exception
		if (skill.getSkillTemplate().getSkillId() != 3777)
		{
			if (!GeoService.getInstance().canSee(effector, firstTarget)) {
				if (effector instanceof Player) {
					PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE);
				}
				return false;
			}
		}
		return true;
	}

}
