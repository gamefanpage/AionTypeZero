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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UseLimits")
public class ItemUseLimits {

	@XmlAttribute(name = "usedelay")
	private int useDelay;

	@XmlAttribute(name = "usedelayid")
	private int useDelayId;

	@XmlAttribute(name = "ownership_world")
	private int ownershipWorldId;

	@XmlAttribute
	private String usearea;

	@XmlAttribute(name = "gender")
	private Gender genderPermitted;

	@XmlAttribute(name = "ride_usable")
	private Boolean rideUsable;

	@XmlAttribute(name = "rank_min")
	private int minRank;

	@XmlAttribute(name = "rank_max")
	private int maxRank = AbyssRankEnum.SUPREME_COMMANDER.getId();

	@XmlAttribute(name = "guild_level")
	private int guildLevel;

	public int getDelayId() {
		return useDelayId;
	}

	public void setDelayId(int delayId) {
		useDelayId = delayId;
	}

	public int getDelayTime() {
		return useDelay;
	}

	public void setDelayTime(int useDelay) {
		this.useDelay = useDelay;
	}

	public ZoneName getUseArea() {
		if (this.usearea == null)
			return null;

		try {
			return ZoneName.get(this.usearea);
		}
		catch (Exception e) {
			return null;
		}
	}

	public int getOwnershipWorld() {
		return ownershipWorldId;
	}

	public Gender getGenderPermitted() {
		return genderPermitted;
	}

	public boolean isRideUsable() {
		if (rideUsable == null)
			return false;
		return rideUsable;
	}

	public int getMinRank() {
		return minRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public boolean verifyRank(int rank) {
		return minRank <= rank && maxRank >= rank;
	}

	public int getGuildLevelPermitted() {
		return guildLevel;
	}
}
