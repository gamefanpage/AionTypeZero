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

package org.typezero.gameserver.model.templates.npcshout;

import javax.xml.bind.annotation.*;

import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author Rolandas
 */

/**
 * <p>
 * Java class for NpcShout complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NpcShout">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="string_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="when" use="required" type="{}ShoutEventType" />
 *       &lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{}ShoutType" default="BROADCAST" />
 *       &lt;attribute name="skill_no" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="poll_delay" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcShout")
public class NpcShout {

	@XmlAttribute(name = "string_id", required = true)
	protected int stringId;

	@XmlAttribute(name = "when", required = true)
	protected ShoutEventType when;

	@XmlAttribute(name = "pattern")
	protected String pattern;

	@XmlAttribute(name = "param")
	protected String param;

	@XmlAttribute(name = "type")
	protected ShoutType type;

	@XmlAttribute(name = "skill_no")
	protected Integer skillNo;

	@XmlAttribute(name = "poll_delay")
	protected Integer pollDelay;

	/**
	 * Gets the value of the stringId property.
	 */
	public int getStringId() {
		return stringId;
	}

	/**
	 * Gets the value of the when property.
	 *
	 * @return possible object is {@link ShoutEventType }
	 */
	public ShoutEventType getWhen() {
		return when;
	}

	/**
	 * Gets the value of the pattern property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Gets the value of the param property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Gets the value of the type property.
	 *
	 * @return possible object is {@link ShoutType }
	 */
	public ShoutType getShoutType() {
		if (type == null)
			return ShoutType.BROADCAST;
		return type;
	}

	/**
	 * Gets the value of the skillNo property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getSkillNo() {
		if (skillNo == null)
			return 0;
		return skillNo;
	}

	public int getPollDelay() {
		if (pollDelay == null)
			return 0;
		return pollDelay;
	}

	public int getShoutRange(Npc npc) {
		return npc.getObjectTemplate().getMinimumShoutRange();
	}

}
