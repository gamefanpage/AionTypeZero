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

import java.util.List;

import org.apache.commons.lang.math.FloatRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.Trap;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PositionUtil;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.model.templates.zone.ZoneType;

/**
 * @author ATracer
 */
public class TargetRangeProperty {

	private static final Logger log = LoggerFactory.getLogger(TargetRangeProperty.class);

	/**
	 * @param skill
	 * @param properties
	 * @return
	 */
	public static final boolean set(final Skill skill, Properties properties) {

		TargetRangeAttribute value = properties.getTargetType();
		int distanceToTarget = properties.getTargetDistance();
		int maxcount = properties.getTargetMaxCount();
		int effectiveRange = properties.getEffectiveRange();
		int altitude = properties.getEffectiveAltitude() != 0 ? properties.getEffectiveAltitude() : 1;


		final List<Creature> effectedList = skill.getEffectedList();
		skill.setTargetRangeAttribute(value);
		switch (value) {
			case ONLYONE:
				break;
			case AREA:
				final Creature firstTarget = skill.getFirstTarget();

				if (firstTarget == null) {
					log.warn("CHECKPOINT: first target is null for skillid " + skill.getSkillTemplate().getSkillId());
					return false;
				}

				// Create a sorted map of the objects in knownlist
				// and filter them properly
				for (VisibleObject nextCreature : firstTarget.getKnownList().getKnownObjects().values()) {
					if (!(nextCreature instanceof Creature))
						continue;
					if (((Creature) nextCreature).getLifeStats() == null)
						continue;
					if (((Creature) nextCreature).getLifeStats().isAlreadyDead())
						continue;

					//if (nextCreature instanceof Kisk && isInsideDisablePvpZone((Creature) nextCreature))
					//	continue;

					if (Math.abs(firstTarget.getZ() - nextCreature.getZ()) > altitude
						|| ((nextCreature instanceof Player) && ((Player) nextCreature).isInPlayerMode(PlayerMode.WINDSTREAM))) {
						continue;
					}


					// TODO this is a temporary hack for traps
					if (skill.getEffector() instanceof Trap && ((Trap) skill.getEffector()).getCreator() == nextCreature)
						continue;

					// Players in blinking state must not be counted
					if ((nextCreature instanceof Player) && (((Player) nextCreature).isProtectionActive()))
						continue;

					if (skill.isPointSkill()) {
						if (MathUtil.isIn3dRange(skill.getX(), skill.getY(), skill.getZ(), nextCreature.getX(),
							nextCreature.getY(), nextCreature.getZ(), distanceToTarget + 1)) {
							skill.getEffectedList().add((Creature) nextCreature);
						}
					}
					if (properties.getEffectiveAngle() > 0) {
						// Fire Storm; only positive angles
						float angle = properties.getEffectiveAngle() / 2f;
						FloatRange range = new FloatRange(angle - 180, -angle);
						if (range.containsFloat(PositionUtil.getAngleToTarget(skill.getEffector(), nextCreature)))
							continue;
						if (!MathUtil.isIn3dRange(skill.getEffector(), nextCreature, effectiveRange))
							continue;
						if (!skill.shouldAffectTarget(nextCreature))
							continue;
						skill.getEffectedList().add((Creature) nextCreature);
					}
					else if (properties.getEffectiveDist() > 0) {
						// Lightning bolt
						if (MathUtil.isInsideAttackCylinder(skill.getEffector(), nextCreature, distanceToTarget, properties.getEffectiveDist(),
							properties.getDirection()) || MathUtil.isIn3dRange(firstTarget, nextCreature, effectiveRange
								+ firstTarget.getObjectTemplate().getBoundRadius().getCollision())) {
							if (!skill.shouldAffectTarget(nextCreature))
								continue;
							skill.getEffectedList().add((Creature) nextCreature);
						}
					}
					else if (MathUtil.isIn3dRange(firstTarget, nextCreature, effectiveRange
						+ firstTarget.getObjectTemplate().getBoundRadius().getCollision())) {
						if (!skill.shouldAffectTarget(nextCreature))
							continue;
						skill.getEffectedList().add((Creature) nextCreature);
					}
				}

				break;
			case ONLYONEANDME:
				Player self = (Player) skill.getEffector();
				if (skill.getEffector() instanceof Player) {
					effectedList.clear();
					effectedList.add(self);
					if (self.getTarget() != self)
					effectedList.add((Creature) self.getTarget());
				}
				break;
			case PARTY:
				// fix for Bodyguard(417)
				if (maxcount == 1)
					break;
				int partyCount = 0;
				if (skill.getEffector() instanceof Player) {
					Player effector = (Player) skill.getEffector();
					// TODO merge groups ?
					if (effector.isInAlliance2()) {
						effectedList.clear();
						for (Player player : effector.getPlayerAllianceGroup2().getMembers()) {
							if (partyCount >= 6 || partyCount >= maxcount)
								break;
							if (!player.isOnline())
								continue;
							if (MathUtil.isIn3dRange(effector, player, effectiveRange + 1)) {
								effectedList.add(player);
								partyCount++;
							}
						}
					}
					else if (effector.isInGroup2()) {
						effectedList.clear();
						for (Player member : effector.getPlayerGroup2().getMembers()) {
							if (partyCount >= maxcount)
								break;
							// TODO: here value +4 till better move controller developed
							if (member != null && MathUtil.isIn3dRange(effector, member, effectiveRange + 1)) {
								effectedList.add(member);
								partyCount++;
							}
						}
					}
				}
				break;
			case PARTY_WITHPET:
				if (skill.getEffector() instanceof Player) {
					final Player effector = (Player) skill.getEffector();
					 if (effector.isInGroup2()) {
						effectedList.clear();
						for (Player member : effector.getPlayerGroup2().getMembers()) {
							if (!member.isOnline())
								continue;
							if (member.getLifeStats().isAlreadyDead())
								continue;
							if (member != null && MathUtil.isIn3dRange(effector, member, effectiveRange + 1)) {
								effectedList.add(member);
								Summon aMemberSummon = member.getSummon();
								if (aMemberSummon != null)
									effectedList.add(aMemberSummon);
							}
						}
					}
				}
				break;
			case POINT:
				for (VisibleObject nextCreature : skill.getEffector().getKnownList().getKnownObjects().values()) {
					if (!(nextCreature instanceof Creature))
						continue;
					if (((Creature) nextCreature).getLifeStats().isAlreadyDead())
						continue;

					//if (nextCreature instanceof Kisk && isInsideDisablePvpZone((Creature) nextCreature))
					//	continue;

					// Players in blinking state must not be counted
					if ((nextCreature instanceof Player) && (((Player) nextCreature).isProtectionActive()))
						continue;

					if (MathUtil.getDistance(skill.getX(), skill.getY(), skill.getZ(), nextCreature.getX(), nextCreature.getY(),
						nextCreature.getZ()) <= distanceToTarget + 1) {
						effectedList.add((Creature) nextCreature);
					}
				}
			case NONE:
				break;

		// TODO other enum values
		}
		return true;
	}

	@SuppressWarnings("unused")
	private static final boolean isInsideDisablePvpZone(Creature creature) {
		for (ZoneInstance zone : creature.getPosition().getMapRegion().getZones(creature)) {
			if (creature.isInsideZoneType(ZoneType.PVP) && zone.getZoneTemplate().getFlags() == 0)
				return true;
		}
		return false;
	}
}
