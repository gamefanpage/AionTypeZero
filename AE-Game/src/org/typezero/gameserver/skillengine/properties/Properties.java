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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Properties")
public class Properties {

	@XmlAttribute(name = "first_target", required = true)
	protected FirstTargetAttribute firstTarget;

	@XmlAttribute(name = "first_target_range", required = true)
	protected int firstTargetRange;

	@XmlAttribute(name = "awr")
	protected boolean addWeaponRange;

	@XmlAttribute(name = "target_relation", required = true)
	protected TargetRelationAttribute targetRelation;

	@XmlAttribute(name = "target_type", required = true)
	protected TargetRangeAttribute targetType;

	@XmlAttribute(name = "target_distance")
	protected int targetDistance;

	@XmlAttribute(name = "target_maxcount")
	protected int targetMaxCount;

	@XmlAttribute(name = "target_status")
	private List<String> targetStatus;

	@XmlAttribute(name = "revision_distance")
	protected int revisionDistance;

	@XmlAttribute(name = "effective_range")
	private int effectiveRange;

	@XmlAttribute(name = "effective_altitude")
	private int effectiveAltitude;

	@XmlAttribute(name = "effective_angle")
	private int effectiveAngle;

	@XmlAttribute(name = "effective_dist")
	private int effectiveDist;

	@XmlAttribute(name = "direction")
	protected AreaDirections direction = AreaDirections.NONE;

	@XmlAttribute(name = "target_species")
	protected TargetSpeciesAttribute targetSpecies = TargetSpeciesAttribute.ALL;

	/**
	 * @param skill
	 */
	public boolean validate(Skill skill) {
		if (firstTarget != null) {
			if (!FirstTargetProperty.set(skill, this)) {
				return false;
			}
		}
		if (firstTargetRange != 0 || addWeaponRange) {
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_START)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!TargetRangeProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetRelation != null) {
			if (!TargetRelationProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!MaxCountProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetStatus != null) {
			if (!TargetStatusProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetSpecies != TargetSpeciesAttribute.ALL) {
			if (!TargetSpeciesProperty.set(skill, this)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param skill
	 */
	public boolean endCastValidate(Skill skill) {
		Creature firstTarget = skill.getFirstTarget();
		skill.getEffectedList().clear();
		skill.getEffectedList().add(firstTarget);

		if (firstTargetRange != 0) {
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_END)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!TargetRangeProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetRelation != null) {
			if (!TargetRelationProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!MaxCountProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetStatus != null) {
			if (!TargetStatusProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetSpecies != TargetSpeciesAttribute.ALL) {
			if (!TargetSpeciesProperty.set(skill, this)) {
				return false;
			}
		}
		return true;
	}

	public FirstTargetAttribute getFirstTarget() {
		return firstTarget;
	}

	public int getFirstTargetRange() {
		return firstTargetRange;
	}

	public boolean isAddWeaponRange() {
		return addWeaponRange;
	}

	public TargetRelationAttribute getTargetRelation() {
		return targetRelation;
	}

	public TargetRangeAttribute getTargetType() {
		return targetType;
	}

	public int getTargetDistance() {
		return targetDistance;
	}

	public int getTargetMaxCount() {
		return targetMaxCount;
	}

	public List<String> getTargetStatus() {
		return targetStatus;
	}

	public int getRevisionDistance() {
		return revisionDistance;
	}

	public int getEffectiveRange() {
		return effectiveRange;
	}

	public int getEffectiveAltitude() {
		return effectiveAltitude;
	}

	public int getEffectiveDist() {
		return effectiveDist;
	}

	public int getEffectiveAngle() {
		return effectiveAngle;
	}

	public AreaDirections getDirection() {
		return direction;
	}

	public TargetSpeciesAttribute getTargetSpecies() {
		return targetSpecies;
	}

	public enum CastState {
		CAST_START(true),
		CAST_END(false);

		private final boolean isCastStart;

		CastState(boolean isCastStart) {
			this.isCastStart = isCastStart;
		}

		public boolean isCastStart() {
			return isCastStart;
		}
	}
}
