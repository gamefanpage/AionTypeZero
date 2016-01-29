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

package org.typezero.gameserver.controllers.attack;

import org.typezero.gameserver.skillengine.model.HitType;

/**
 * @author ATracer modified by Sippolo, kecimis
 */
public class AttackResult {

	private int damage;

	private AttackStatus attackStatus;
	
	private HitType hitType = HitType.EVERYHIT;

	/**
	 * shield effects related
	 */
	private int shieldType;
	private int reflectedDamage = 0;
	private int reflectedSkillId = 0;
	private int protectedSkillId = 0;
	private int protectedDamage = 0;
	private int protectorId = 0;
	private int shieldMp = 0;
	
	private boolean launchSubEffect = true;

	public AttackResult(int damage, AttackStatus attackStatus) {
		this.damage = damage;
		this.attackStatus = attackStatus;
	}

	public AttackResult(int damage, AttackStatus attackStatus, HitType type) {
		this(damage, attackStatus);
		this.hitType = type;
	}

	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @param damage
	 *          the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * @return the attackStatus
	 */
	public AttackStatus getAttackStatus() {
		return attackStatus;
	}

	/**
	 * @return the Damage Type
	 */
	public HitType getDamageType() {
		return hitType;
	}

	/**
	 * @param type
	 *          the Damage Type to set
	 */
	public void setDamageType(HitType type) {
		this.hitType = type;
	}
	
	/**
	 * shield effects related
	 * 
	 */
	
	/**
	 * @return the shieldType
	 */
	public int getShieldType() {
		return shieldType;
	}

	/**
	 * @param shieldType
	 *          the shieldType to set
	 */
	public void setShieldType(int shieldType) {
		this.shieldType |= shieldType;
	}

	public int getReflectedDamage() {
		return this.reflectedDamage;
	}

	public void setReflectedDamage(int reflectedDamage) {
		this.reflectedDamage = reflectedDamage;
	}

	public int getReflectedSkillId() {
		return this.reflectedSkillId;
	}

	public void setReflectedSkillId(int skillId) {
		this.reflectedSkillId = skillId;
	}
	
	public int getProtectedSkillId() {
		return this.protectedSkillId;
	}

	public void setProtectedSkillId(int skillId) {
		this.protectedSkillId = skillId;
	}
	
	public int getProtectedDamage() {
		return this.protectedDamage;
	}

	public void setProtectedDamage(int protectedDamage) {
		this.protectedDamage = protectedDamage;
	}
	
	public int getProtectorId() {
		return this.protectorId;
	}

	public void setProtectorId(int protectorId) {
		this.protectorId = protectorId;
	}
	
	public boolean isLaunchSubEffect() {
		return launchSubEffect;
	}
	
	public void setLaunchSubEffect(boolean launchSubEffect) {
		this.launchSubEffect = launchSubEffect;
	}
	
	public int getShieldMp() {
		return shieldMp;
	}

	public void setShieldMp(int shieldMp) {
		this.shieldMp = shieldMp;
	}
	
}
