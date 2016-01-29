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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.QuestService;

/**
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quests")
public class QuestsData {

	@XmlElement(name = "quest", required = true)
	protected List<QuestTemplate> questsData;
	private TIntObjectHashMap<QuestTemplate> questData = new TIntObjectHashMap<QuestTemplate>();
	private TIntObjectHashMap<List<QuestTemplate>> sortedByFactionId = new TIntObjectHashMap<List<QuestTemplate>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		questData.clear();
		sortedByFactionId.clear();
		for (QuestTemplate quest : questsData) {
			questData.put(quest.getId(), quest);
			int npcFactionId = quest.getNpcFactionId();
			if (npcFactionId == 0 || quest.isTimeBased())
				continue;
			if (!sortedByFactionId.containsKey(npcFactionId)) {
				List<QuestTemplate> factionQuests = new ArrayList<QuestTemplate>();
				factionQuests.add(quest);
				sortedByFactionId.put(npcFactionId, factionQuests);
			}
			else {
				sortedByFactionId.get(npcFactionId).add(quest);
			}
		}
	}

	public QuestTemplate getQuestById(int id) {
		return questData.get(id);
	}

	public List<QuestTemplate> getQuestsByNpcFaction(int npcFactionId, Player player) {
		List<QuestTemplate> factionQuests = sortedByFactionId.get(npcFactionId);
		List<QuestTemplate> quests = new ArrayList<QuestTemplate>();
		QuestEnv questEnv = new QuestEnv(null, player, 0, 0);
		for (QuestTemplate questTemplate : factionQuests){
			if (!QuestEngine.getInstance().isHaveHandler(questTemplate.getId()))
				continue;
			if (questTemplate.getMinlevelPermitted() != 0 && player.getLevel() < questTemplate.getMinlevelPermitted())
				continue;
			questEnv.setQuestId(questTemplate.getId());
			if (QuestService.checkStartConditions(questEnv, false))
				quests.add(questTemplate);
		}
		return quests;
	}

	public int size() {
		return questData.size();
	}

	/**
	 * @return the questsData
	 */
	public List<QuestTemplate> getQuestsData() {
		return questsData;
	}

	/**
	 * @param questsData
	 *          the questsData to set
	 */
	public void setQuestsData(List<QuestTemplate> questsData) {
		this.questsData = questsData;
		afterUnmarshal(null, null);
	}
}
