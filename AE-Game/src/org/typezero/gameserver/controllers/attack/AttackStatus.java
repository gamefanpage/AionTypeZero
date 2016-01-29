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

/**
 * @author ATracer
 */
public enum AttackStatus {
	DODGE(0, true, false),
	OFFHAND_DODGE(1, true, false),
	PARRY(2, true, false),
	OFFHAND_PARRY(3, true, false),
	BLOCK(4, true, false),
	OFFHAND_BLOCK(5, true, false),
	RESIST(6),
	OFFHAND_RESIST(7),
	BUF(8), // ??
	OFFHAND_BUF(9),
	NORMALHIT(10),
	OFFHAND_NORMALHIT(11),
	CRITICAL_DODGE(-64, true, true),
	CRITICAL_PARRY(-624, true, true),
	CRITICAL_BLOCK(-604, true, true),
	PHYSICAL_CRITICAL_RESIST(-58, false, true),
	CRITICAL(-54, false, true),
	OFFHAND_CRITICAL_DODGE(-474, true, true),
	OFFHAND_CRITICAL_PARRY(-454, true, true),
	OFFHAND_CRITICAL_BLOCK(-434, true, true),
	OFFHAND_CRITICAL_RESIST(-41, false, true),
	OFFHAND_CRITICAL(-37, false, true);

	private final int type;
	private final boolean counterSkill;
	private final boolean isCritical;

	private AttackStatus(int type) {
		this(type, false, false);
	}

	private AttackStatus(int type, boolean counterSkill, boolean isCritical) {
		this.type = type;
		this.counterSkill = counterSkill;
		this.isCritical = isCritical;
	}

	public final int getId() {
		return type;
	}
	public final boolean isCounterSkill() {
		return counterSkill;
	}
	public final boolean isCritical() {
		return isCritical;
	}

	public static final AttackStatus getOffHandStats(AttackStatus mainHandStatus) {
		switch (mainHandStatus) {
			case DODGE:
				return OFFHAND_DODGE;
			case PARRY:
				return OFFHAND_PARRY;
			case BLOCK:
				return OFFHAND_BLOCK;
			case RESIST:
				return OFFHAND_RESIST;
			case BUF:
				return OFFHAND_BUF;
			case NORMALHIT:
				return OFFHAND_NORMALHIT;
			case CRITICAL:
				return OFFHAND_CRITICAL;
			case CRITICAL_DODGE:
				return OFFHAND_CRITICAL_DODGE;
			case CRITICAL_PARRY:
				return OFFHAND_CRITICAL_PARRY;
			case CRITICAL_BLOCK:
				return OFFHAND_CRITICAL_BLOCK;
			case PHYSICAL_CRITICAL_RESIST:
				return OFFHAND_CRITICAL_RESIST;
			default:
				break;
		}
		throw new IllegalArgumentException("Invalid mainHandStatus " + mainHandStatus);
	}

	public static final AttackStatus getBaseStatus(AttackStatus status) {
		switch (status) {
			case DODGE:
			case CRITICAL_DODGE:
			case OFFHAND_DODGE:
			case OFFHAND_CRITICAL_DODGE:
				return AttackStatus.DODGE;
			case PARRY:
			case CRITICAL_PARRY:
			case OFFHAND_PARRY:
			case OFFHAND_CRITICAL_PARRY:
				return AttackStatus.PARRY;
			case BLOCK:
			case CRITICAL_BLOCK:
			case OFFHAND_BLOCK:
			case OFFHAND_CRITICAL_BLOCK:
				return AttackStatus.BLOCK;
			default:
				return status;
		}
	}

	public static final AttackStatus getCriticalStatusFor(AttackStatus status) {
		switch(status) {
			case DODGE:
				return AttackStatus.CRITICAL_DODGE;
			case OFFHAND_DODGE:
				return AttackStatus.OFFHAND_CRITICAL_DODGE;
			case PARRY:
				return AttackStatus.CRITICAL_PARRY;
			case OFFHAND_PARRY:
				return AttackStatus.OFFHAND_CRITICAL_PARRY;
			case BLOCK:
				return AttackStatus.CRITICAL_BLOCK;
			case OFFHAND_BLOCK:
				return AttackStatus.OFFHAND_CRITICAL_BLOCK;
			case NORMALHIT:
				return AttackStatus.CRITICAL;
			case OFFHAND_NORMALHIT:
				return AttackStatus.OFFHAND_CRITICAL;
		default:
			return status;
		}
	}
}
