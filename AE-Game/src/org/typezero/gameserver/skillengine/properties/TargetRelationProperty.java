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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Servant;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.skillengine.model.Skill;
import java.util.Iterator;
import java.util.List;

/**
 * @author ATracer
 */
public class TargetRelationProperty {

	/**
	 * @param skill
	 * @param properties
	 * @return
	 */
	public static boolean set(final Skill skill, Properties properties) {

		TargetRelationAttribute value = properties.getTargetRelation();

		final List<Creature> effectedList = skill.getEffectedList();
		boolean isMaterialSkill = DataManager.MATERIAL_DATA.isMaterialSkill(skill.getSkillId());
		Creature effector = skill.getEffector();

		switch (value) {
			case ALL:
				break;
			case ENEMY:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();) {
					Creature nextEffected = iter.next();

					if (effector.isEnemy(nextEffected) || isMaterialSkill)
						continue;

					iter.remove();
				}
				break;
			case FRIEND:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();) {
					Creature nextEffected = iter.next();

					if (!effector.isEnemy(nextEffected) && isBuffAllowed(nextEffected) || isMaterialSkill)
						continue;

					iter.remove();
				}

				if (effectedList.isEmpty()) {
					skill.setFirstTarget(skill.getEffector());
					effectedList.add(skill.getEffector());
				}
				else {
					skill.setFirstTarget(effectedList.get(0));
				}
				break;
			case MYPARTY:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();) {
					Creature nextEffected = iter.next();

					Player player = null;
					if (nextEffected instanceof Player) {
						player = (Player) nextEffected;
					}
					else if (nextEffected instanceof Summon) {
						Summon playerSummon = (Summon) nextEffected;
						if (playerSummon.getMaster() != null)
							player = playerSummon.getMaster();
					}
					if (player != null) {
						if (effector instanceof Servant)
							effector = ((Servant) effector).getMaster();

						Player playerEffector = (Player) effector;
						if (playerEffector.isInAlliance2() && player.isInAlliance2()) {
							if (playerEffector.getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId()))
								continue;
						}
						else if (playerEffector.isInGroup2() && player.isInGroup2()) {
							if (playerEffector.getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId()))
								continue;
						}
					}
					iter.remove();
				}

				if (effectedList.isEmpty()) {
					skill.setFirstTarget(effector);
					effectedList.add(effector);
				}
				else {
					skill.setFirstTarget(effectedList.get(0));
				}
				break;
		}

		return true;
	}

	/**
	 * @param effected
	 * @return true = allow buff, false = deny buff
	 */
	public static boolean isBuffAllowed(Creature effected) {
		if (effected instanceof SiegeNpc)
			switch (((SiegeNpc) effected).getObjectTemplate().getAbyssNpcType()) {
				case ARTIFACT:
				case ARTIFACT_EFFECT_CORE:
				case DOOR:
				case DOORREPAIR:
					return false;
			}
		return true;
	}

}
