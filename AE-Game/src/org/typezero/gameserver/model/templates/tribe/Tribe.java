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

package org.typezero.gameserver.model.templates.tribe;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.TribeClass;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tribe")
public class Tribe {

	@XmlList
	protected List<TribeClass> aggro;
	@XmlList
	protected List<TribeClass> hostile;
	@XmlList
	protected List<TribeClass> friend;
	@XmlList
	protected List<TribeClass> neutral;
	@XmlList
	protected List<TribeClass> none;
	@XmlList
	protected List<TribeClass> support;

	@XmlAttribute
	protected TribeClass base = TribeClass.NONE;

	@XmlAttribute(required = true)
	protected TribeClass name;

	public List<TribeClass> getAggro() {
		if (aggro == null) {
			aggro = Collections.emptyList();
		}
		return this.aggro;
	}

	public List<TribeClass> getHostile() {
		if (hostile == null) {
			hostile = Collections.emptyList();
		}
		return this.hostile;
	}

	public List<TribeClass> getFriend() {
		if (friend == null) {
			friend = Collections.emptyList();
		}
		return this.friend;
	}

	public List<TribeClass> getNeutral() {
		if (neutral == null) {
			neutral = Collections.emptyList();
		}
		return this.neutral;
	}

	public List<TribeClass> getNone() {
		if (none == null) {
			none = Collections.emptyList();
		}
		return this.none;
	}

	public List<TribeClass> getSupport() {
		if (support == null) {
			support = Collections.emptyList();
		}
		return this.support;
	}

	public TribeClass getBase() {
		return base == TribeClass.NONE ? name : base;
	}

	public TribeClass getName() {
		return name;
	}

	public final boolean isGuard() {
		return name.isGuard();
	}

	public final boolean isBasic() {
		return name.isBasicClass();
	}

	@Override
	public String toString() {
		return name + " (" + base + ")";
	}
}
