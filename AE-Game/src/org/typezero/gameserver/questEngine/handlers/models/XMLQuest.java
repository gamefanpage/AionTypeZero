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

package org.typezero.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.questEngine.QuestEngine;

/**
 * @author MrPoke, Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestScriptData")
@XmlSeeAlso({ ReportToData.class, RelicRewardsData.class, CraftingRewardsData.class, ReportToManyData.class, MonsterHuntData.class,
	ItemCollectingData.class, WorkOrdersData.class, XmlQuestData.class, MentorMonsterHuntData.class, ItemOrdersData.class,
	FountainRewardsData.class, SkillUseData.class })
public abstract class XMLQuest {

	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "movie", required = false)
	protected int questMovie;
	@XmlAttribute(name = "mission", required = false)
	protected boolean mission;

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}

	public int getQuestMovie() {
		return questMovie;
	}

	/**
	 * @return the mission
	 */
	public boolean isMission() {
		return mission;
	}

	/**
	 * @param mission
	 *          the mission to set
	 */
	public void setMission(boolean mission) {
		this.mission = mission;
	}

	public abstract void register(QuestEngine questEngine);
}
