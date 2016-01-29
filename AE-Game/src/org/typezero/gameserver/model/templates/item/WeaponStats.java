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

package org.typezero.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author ATracer
 */
public class WeaponStats {

	@XmlAttribute(name = "min_damage")
	protected int minDamage;
	@XmlAttribute(name = "max_damage")
	protected int maxDamage;
	@XmlAttribute(name = "attack_speed")
	protected int attackSpeed;
	@XmlAttribute(name = "physical_critical")
	protected int physicalCritical;
	@XmlAttribute(name = "physical_accuracy")
	protected int physicalAccuracy;
	@XmlAttribute
	protected int parry;
	@XmlAttribute(name = "magical_accuracy")
	protected int magicalAccuracy;
	@XmlAttribute(name = "boost_magical_skill")
	protected int boostMagicalSkill;
	@XmlAttribute(name = "attack_range")
	protected int attackRange;
	@XmlAttribute(name = "hit_count")
	protected int hitCount;
	@XmlAttribute(name = "reduce_max")
	protected int reduceMax;

	public final int getMinDamage() {
		return minDamage;
	}

	public final int getMaxDamage() {
		return maxDamage;
	}

	public final int getMeanDamage() {
		return (minDamage + maxDamage) / 2;
	}

	public final int getAttackSpeed() {
		return attackSpeed;
	}

	public final int getPhysicalCritical() {
		return physicalCritical;
	}

	public final int getPhysicalAccuracy() {
		return physicalAccuracy;
	}

	public final int getParry() {
		return parry;
	}

	public final int getMagicalAccuracy() {
		return magicalAccuracy;
	}

	public final int getBoostMagicalSkill() {
		return boostMagicalSkill;
	}

	public final int getAttackRange() {
		return attackRange;
	}

	public final int getHitCount() {
		return hitCount;
	}

	public final int getReduceMax() {
		return reduceMax;
	}

}
