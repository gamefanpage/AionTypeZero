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

import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public class StatFunctionProxy implements IStatFunction, Comparable<IStatFunction> {

	private final StatOwner owner;
	private final IStatFunction proxiedFunction;
	private final StatEnum stat;

	public StatFunctionProxy(StatOwner owner, IStatFunction statFunction) {
		this.owner = owner;
		this.proxiedFunction = statFunction;
		this.stat = statFunction.getName();
	}

	public StatFunctionProxy(StatOwner owner, IStatFunction statFunction, StatEnum statEnum) {
		this.owner = owner;
		this.proxiedFunction = statFunction;
		this.stat = statEnum;
	}

	public IStatFunction getProxiedFunction() {
		return proxiedFunction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatFunctionProxy other = (StatFunctionProxy) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		}
		else if (!owner.equals(other.owner))
			return false;

		// if (other.isBonus() != this.isBonus())
		//	return false;
		// return other.getRandomNumber() == this.getRandomNumber();
		return true;
	}

	@Override
	public int compareTo(IStatFunction o) {
		return proxiedFunction.compareTo(o);
	}

	@Override
	public StatOwner getOwner() {
		return owner;
	}

	@Override
	public StatEnum getName() {
		return stat;
	}

	@Override
	public boolean isBonus() {
		return proxiedFunction.isBonus();
	}

	@Override
	public int getRandomNumber() {
		return proxiedFunction.getRandomNumber();
	}

	@Override
	public int getPriority() {
		return proxiedFunction.getPriority();
	}

	@Override
	public int getValue() {
		return proxiedFunction.getValue();
	}

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return proxiedFunction.validate(stat, statFunction);
	}

	@Override
	public void apply(Stat2 stat) {
		proxiedFunction.apply(stat);
	}

	@Override
	public boolean hasConditions() {
		return proxiedFunction.hasConditions();
	}

	@Override
	public String toString() {
		return "Proxy [name=" + proxiedFunction.getName() + ", bonus=" + isBonus() + ", value=" + getValue() + ", priority=" + getPriority()
			+ ", owner=" + owner + "]";
	}
}
