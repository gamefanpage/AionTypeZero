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

package org.typezero.gameserver.model.stats.calc.functions;

import javax.xml.bind.annotation.*;

import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.skillengine.condition.Conditions;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleModifier")
public class StatFunction implements IStatFunction {

	@XmlAttribute(name = "name")
	protected StatEnum stat;
	@XmlAttribute
	private boolean bonus;
	@XmlAttribute
	protected int value;
	@XmlElement(name = "conditions")
	private Conditions conditions;
	@XmlTransient
	private int rndNumber;

	public StatFunction() {
	}

	public StatFunction(StatEnum stat, int value, boolean bonus) {
		this.stat = stat;
		this.value = value;
		this.bonus = bonus;
	}

	@Override
	public int compareTo(IStatFunction o) {
		int result = getPriority() - o.getPriority();
		if (result == 0)
			return this.hashCode() - o.hashCode();
		return result;
	}

	@Override
	public StatOwner getOwner() {
		return null;
	}

	@Override
	public final StatEnum getName() {
		return stat;
	}

	@Override
	public final boolean isBonus() {
		return bonus;
	}

	@Override
	public int getPriority() {
		return 0x10;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return conditions != null ? conditions.validate(stat, statFunction) : true;
	}

	@Override
	public void apply(Stat2 stat) {
	}

	@Override
	public String toString() {
		return "stat=" + stat + ", bonus=" + bonus + ", value=" + value + ", priority=" + getPriority();
	}

	public StatFunction withConditions(Conditions conditions) {
		this.conditions = conditions;
		return this;
	}

	public boolean hasConditions() {
		return conditions != null;
	}

	public int getRandomNumber() {
		return rndNumber;
	}

	public void setRandomNumber(int rndNumber) {
		this.rndNumber = rndNumber;
	}

}
