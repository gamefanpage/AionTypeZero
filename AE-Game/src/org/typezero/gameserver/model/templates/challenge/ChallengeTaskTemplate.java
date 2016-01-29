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

package org.typezero.gameserver.model.templates.challenge;

import org.typezero.gameserver.model.Race;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChallengeTask", propOrder = { "quest", "contrib", "reward" })
public class ChallengeTaskTemplate {

	@XmlElement(required = true)
	protected List<ChallengeQuestTemplate> quest;
	protected List<ContributionReward> contrib;

	@XmlElement(required = true)
	protected ChallengeReward reward;

	@XmlAttribute
	protected Boolean repeat;

	@XmlAttribute(name = "town_residence")
	protected Boolean townResidence;

	@XmlAttribute(name = "name_id")
	protected Integer nameId;

	@XmlAttribute(name = "max_level", required = true)
	protected int maxLevel;

	@XmlAttribute(name = "min_level", required = true)
	protected int minLevel;

	@XmlAttribute(name = "prev_task")
	protected Integer prevTask;

	@XmlAttribute(required = true)
	protected Race race;

	@XmlAttribute(required = true)
	protected ChallengeType type;

	@XmlAttribute(required = true)
	protected int id;

	public List<ChallengeQuestTemplate> getQuests() {
		return this.quest;
	}

	public List<ContributionReward> getContrib() {
		return this.contrib;
	}

	public ChallengeReward getReward() {
		return this.reward;
	}

	public boolean isRepeatable() {
		return this.repeat != null && this.repeat == true;
	}

	public boolean isTownResidence() {
		return this.townResidence != null && this.townResidence == true;
	}

	public Integer getNameId() {
		return this.nameId;
	}

	public int getMaxLevel() {
		return this.maxLevel;
	}

	public int getMinLevel() {
		return this.minLevel;
	}

	public Integer getPrevTask() {
		return this.prevTask;
	}

	public Race getRace() {
		return this.race;
	}

	public ChallengeType getType() {
		return this.type;
	}

	public int getId() {
		return this.id;
	}
}
