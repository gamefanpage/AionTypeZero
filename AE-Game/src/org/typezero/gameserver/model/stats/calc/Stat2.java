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

package org.typezero.gameserver.model.stats.calc;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public abstract class Stat2 {

	float bonusRate = 1f;
	int base;
	int bonus;
	private final Creature owner;
	protected final StatEnum stat;

	public Stat2(StatEnum stat, int base, Creature owner) {
		this(stat, base, owner, 1);
	}

	public Stat2(StatEnum stat, int base, Creature owner, float bonusRate) {
		this.stat = stat;
		this.base = base;
		this.owner = owner;
		this.bonusRate = bonusRate;
	}

	public final StatEnum getStat() {
		return stat;
	}

	public final int getBase() {
		return base;
	}

	public final void setBase(int base) {
		this.base = base;
	}

	public abstract void addToBase(int base);

	public final int getBonus() {
		return bonus;
	}

	public final int getCurrent() {
		return this.base + this.bonus;

	}

	public final void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public final float getBonusRate() {
		return bonusRate;
	}

	public final void setBonusRate(float bonusRate) {
		this.bonusRate = bonusRate;
	}

	public abstract void addToBonus(int bonus);
	
	public abstract float calculatePercent(int delta);

	public final Creature getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		return "[" + stat.name() + " base=" + base + ", bonus=" + bonus + "]";
	}

}
