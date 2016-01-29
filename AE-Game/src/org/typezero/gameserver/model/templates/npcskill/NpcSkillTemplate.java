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

package org.typezero.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author AionChs Master, nrg
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcskill")
public class NpcSkillTemplate {

	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "skillid")
	protected int skillid;
	@XmlAttribute(name = "skilllevel")
	protected int skilllevel;
	@XmlAttribute(name = "probability")
	protected int probability;
	@XmlAttribute(name = "minhp")
	protected int minhp = 0;
	@XmlAttribute(name = "maxhp")
	protected int maxhp = 0;
	@XmlAttribute(name="maxtime")
	protected int maxtime = 0;
	@XmlAttribute(name="mintime")
	protected int mintime = 0;
	@XmlAttribute(name="conjunction")
	protected ConjunctionType conjunction = ConjunctionType.AND;
	@XmlAttribute(name="cooldown")
	protected int cooldown = 0;
	@XmlAttribute(name="useinspawned")
	protected boolean useinspawned = false;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the skillid
	 */
	public int getSkillid() {
		return skillid;
	}

	/**
	 * @return the skilllevel
	 */
	public int getSkillLevel() {
		return skilllevel;
	}

	/**
	 * @return the probability
	 */
	public int getProbability() {
		return probability;
	}

	/**
	 * @return the minhp
	 */
	public int getMinhp() {
		return minhp;
	}

	/**
	 * @return the maxhp
	 */
	public int getMaxhp() {
		return maxhp;
	}

	/**
	 * @return the mintime
	 */
	public int getMinTime() {
		return mintime;
	}

	/**
	 * @return the maxtime
	 */
	public int getMaxTime() {
		return maxtime;
	}

	/**
	 * Gets the value of the conjunction property.
	 *
	 * @return possible object is {@link ConjunctionType }
	 */
	public ConjunctionType getConjunctionType() {
		return conjunction;
	}

	/**
	 * @return the cooldown
	 */
	public int getCooldown() {
		return cooldown;
	}

	/**
	 * @return the useinspawned
	 */
	public boolean getUseInSpawned() {
		return useinspawned;
	}
}
