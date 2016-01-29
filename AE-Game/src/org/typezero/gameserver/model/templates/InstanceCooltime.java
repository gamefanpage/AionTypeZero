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

package org.typezero.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.instance.InstanceCoolTimeType;

/**
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceCooltime")
public class InstanceCooltime {
	@XmlElement(name = "type")
	protected InstanceCoolTimeType coolTimeType;
	@XmlElement(name = "typevalue")
	protected String typevalue;
	@XmlElement(name = "ent_cool_time")
	protected Integer entCoolTime;
	@XmlElement(name = "max_member_light")
	protected Integer maxMemberLight;
	@XmlElement(name = "max_member_dark")
	protected Integer maxMemberDark;
	@XmlElement(name = "enter_min_level_light")
	protected Integer enterMinLevelLight;
	@XmlElement(name = "enter_max_level_light")
	protected Integer enterMaxLevelLight;
	@XmlElement(name = "enter_min_level_dark")
	protected Integer enterMinLevelDark;
	@XmlElement(name = "enter_max_level_dark")
	protected Integer enterMaxLevelDark;
	@XmlElement(name = "can_enter_mentor")
	protected boolean can_enter_mentor;
	@XmlAttribute(required = true)
	protected int id;
    @XmlAttribute(name = "sync_id", required = true)
    protected int syncId;
	@XmlAttribute(required = true)
	protected int worldId;
	@XmlAttribute(required = true)
	protected Race race;
    @XmlAttribute(required=true)
    protected int count;

	public InstanceCoolTimeType getCoolTimeType() {
		return coolTimeType;
	}

	public String getTypeValue() {
		return typevalue;
	}

	/**
	 * Gets the value of the entCoolTime property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEntCoolTime() {
		return entCoolTime;
	}

	/**
	 * Gets the value of the maxMemberLight property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getMaxMemberLight() {
		return maxMemberLight;
	}

	/**
	 * Gets the value of the maxMemberDark property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getMaxMemberDark() {
		return maxMemberDark;
	}

	/**
	 * Gets the value of the enterMinLevelLight property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEnterMinLevelLight() {
		return enterMinLevelLight;
	}

	/**
	 * Gets the value of the enterMaxLevelLight property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEnterMaxLevelLight() {
		return enterMaxLevelLight;
	}

	/**
	 * Gets the value of the enterMinLevelDark property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEnterMinLevelDark() {
		return enterMinLevelDark;
	}

	/**
	 * Gets the value of the enterMaxLevelDark property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEnterMaxLevelDark() {
		return enterMaxLevelDark;
	}

	/**
	 * Gets the value of the can_enter_mentor property.
	 *
	 * @return possible object is {@link boolean }
	 */
	public boolean getCanEnterMentor() {
		return can_enter_mentor;
	}

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the value of the worldId property.
	 */
	public int getWorldId() {
		return worldId;
	}

	/**
	 * Gets the value of the race property.
	 *
	 * @return possible object is {@link Race }
	 */
	public Race getRace() {
		return race;
	}

	/**
	 * @return the syncId
	 */
	public int getSyncId() {
		return syncId;
	}
    public int getCount() {
        return count;
    }
}
