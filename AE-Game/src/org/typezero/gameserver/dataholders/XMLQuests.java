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

package org.typezero.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.questEngine.handlers.models.CraftingRewardsData;
import org.typezero.gameserver.questEngine.handlers.models.ItemCollectingData;
import org.typezero.gameserver.questEngine.handlers.models.KillInWorldData;
import org.typezero.gameserver.questEngine.handlers.models.KillSpawnedData;
import org.typezero.gameserver.questEngine.handlers.models.MentorMonsterHuntData;
import org.typezero.gameserver.questEngine.handlers.models.MonsterHuntData;
import org.typezero.gameserver.questEngine.handlers.models.RelicRewardsData;
import org.typezero.gameserver.questEngine.handlers.models.ReportToData;
import org.typezero.gameserver.questEngine.handlers.models.ReportToManyData;
import org.typezero.gameserver.questEngine.handlers.models.FountainRewardsData;
import org.typezero.gameserver.questEngine.handlers.models.ItemOrdersData;
import org.typezero.gameserver.questEngine.handlers.models.SkillUseData;
import org.typezero.gameserver.questEngine.handlers.models.WorkOrdersData;
import org.typezero.gameserver.questEngine.handlers.models.XMLQuest;
import org.typezero.gameserver.questEngine.handlers.models.XmlQuestData;

/**
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quest_scripts")
public class XMLQuests {

	@XmlElements({ @XmlElement(name = "report_to", type = ReportToData.class),
		@XmlElement(name = "monster_hunt", type = MonsterHuntData.class),
		@XmlElement(name = "xml_quest", type = XmlQuestData.class),
		@XmlElement(name = "item_collecting", type = ItemCollectingData.class),
		@XmlElement(name = "relic_rewards", type = RelicRewardsData.class),
		@XmlElement(name = "crafting_rewards", type = CraftingRewardsData.class),
		@XmlElement(name = "report_to_many", type = ReportToManyData.class),
		@XmlElement(name = "kill_in_world", type = KillInWorldData.class),
		@XmlElement(name = "skill_use", type = SkillUseData.class),
		@XmlElement(name = "kill_spawned", type = KillSpawnedData.class),
		@XmlElement(name = "mentor_monster_hunt", type = MentorMonsterHuntData.class),
		@XmlElement(name = "fountain_rewards", type = FountainRewardsData.class),
		@XmlElement(name = "item_order", type = ItemOrdersData.class),
		@XmlElement(name = "work_order", type = WorkOrdersData.class) })
	protected List<XMLQuest> data;

	/**
	 * @return the data
	 */
	public List<XMLQuest> getQuest() {
		return data;
	}

	/**
	 * @param data
	 *          the data to set
	 */
	public void setData(List<XMLQuest> data) {
		this.data = data;
	}
}
