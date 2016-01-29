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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.QuestKill;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.template.MonsterHunt;
import java.util.HashSet;
import java.util.Set;

/**
 * @author MrPoke, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonsterHuntData", propOrder = { "monster" })
@XmlSeeAlso({ KillSpawnedData.class, MentorMonsterHuntData.class })
public class MonsterHuntData extends XMLQuest {

	@XmlElement(name = "monster", required = true)
	protected List<Monster> monster;
	@XmlAttribute(name = "start_npc_ids", required = true)
	protected List<Integer> startNpcIds;
	@XmlAttribute(name = "end_npc_ids")
	protected List<Integer> endNpcIds;
	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;
	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;
	@XmlAttribute(name = "aggro_start_npcs")
	protected List<Integer> aggroNpcs;
	@XmlAttribute(name = "invasion_world")
	protected int invasionWorld;

	@Override
	public void register(QuestEngine questEngine) {
		FastMap<Monster, Set<Integer>> monsterNpcs = new FastMap<Monster, Set<Integer>>();
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(id);
		for (Monster m : monster) {
			if (CustomConfig.QUESTDATA_MONSTER_KILLS) {
				// if sequence numbers specified use it
				if (m.getNpcSequence() != null && questTemplate.getQuestKill() != null) {
					QuestKill killNpcs = null;
					for (int index = 0; index < questTemplate.getQuestKill().size(); index++) {
						if (questTemplate.getQuestKill().get(index).getSequenceNumber() == m.getNpcSequence()) {
							killNpcs = questTemplate.getQuestKill().get(index);
							break;
						}
					}
					if (killNpcs != null)
						monsterNpcs.put(m, killNpcs.getNpcIds());
				}
				// if no sequence was specified, check all npc ids to match quest data
				else if (m.getNpcSequence() == null && questTemplate.getQuestKill() != null) {
					Set<Integer> npcSet = new HashSet<Integer>(m.getNpcIds());
					QuestKill matchedKillNpcs = null;
					int maxMatchCount = 0;
					for (int index = 0; index < questTemplate.getQuestKill().size(); index++) {
						QuestKill killNpcs = questTemplate.getQuestKill().get(index);
						int matchCount = 0;
						for (int npcId : killNpcs.getNpcIds()) {
							if (!npcSet.contains(npcId))
								continue;
							matchCount++;
						}
						if (matchCount > maxMatchCount) {
							maxMatchCount = matchCount;
							matchedKillNpcs = killNpcs;
						}
					}
					if (matchedKillNpcs != null) {
						// add npcs not present in quest data (weird!)
						npcSet.addAll(matchedKillNpcs.getNpcIds());
						monsterNpcs.put(m, npcSet);
					}
				}
				else {
					monsterNpcs.put(m, new HashSet<Integer>(m.getNpcIds()));
				}
			}
			else {
				monsterNpcs.put(m, new HashSet<Integer>(m.getNpcIds()));
			}
		}
		MonsterHunt template = new MonsterHunt(id, startNpcIds, endNpcIds, monsterNpcs, startDialog, endDialog, aggroNpcs, invasionWorld);
		questEngine.addQuestHandler(template);
	}

}
