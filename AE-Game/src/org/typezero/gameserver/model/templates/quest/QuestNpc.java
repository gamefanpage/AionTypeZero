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

package org.typezero.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.questEngine.QuestEngine;

/**
 * @author MrPoke
 */
public class QuestNpc {

	private static final Logger log = LoggerFactory.getLogger(QuestNpc.class);

	private final List<Integer> onQuestStart;
	private final List<Integer> onKillEvent;
	private final List<Integer> onTalkEvent;
	private final List<Integer> onAttackEvent;
	private final List<Integer> onAddAggroListEvent;
	private final List<Integer> onAtDistanceEvent;
	private final int npcId;

	public QuestNpc(int npcId) {
		this.npcId = npcId;
		onQuestStart = new ArrayList<Integer>(0);
		onKillEvent = new ArrayList<Integer>(0);
		onTalkEvent = new ArrayList<Integer>(0);
		onAttackEvent = new ArrayList<Integer>(0);
		onAddAggroListEvent = new ArrayList<Integer>(0);
		onAtDistanceEvent = new ArrayList<Integer>(0);
	}

	private void registerCanAct(int questId, int npcId) {
		NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		if (template == null) {
			log.warn("[QuestEngine] No such NPC template for " + npcId + " in Q" + questId);
			return;
		}
		String aiName = DataManager.NPC_DATA.getNpcTemplate(npcId).getAi();
		if ("quest_use_item".equals(aiName)) {
			QuestEngine.getInstance().registerCanAct(questId, npcId);
		}
	}

	public void addOnQuestStart(int questId) {
		if (!onQuestStart.contains(questId)) {
			onQuestStart.add(questId);
		}
	}

	public List<Integer> getOnQuestStart() {
		return onQuestStart;
	}

	public void addOnAttackEvent(int questId) {
		if (!onAttackEvent.contains(questId)) {
			onAttackEvent.add(questId);
		}
	}

	public List<Integer> getOnAttackEvent() {
		return onAttackEvent;
	}

	public void addOnKillEvent(int questId) {
		if (!onKillEvent.contains(questId)) {
			onKillEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}

	public List<Integer> getOnKillEvent() {
		return onKillEvent;
	}

	public void addOnTalkEvent(int questId) {
		if (!onTalkEvent.contains(questId)) {
			onTalkEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}

	public List<Integer> getOnTalkEvent() {
		return onTalkEvent;
	}

	public void addOnAddAggroListEvent(int questId) {
		if (!onAddAggroListEvent.contains(questId)) {
			onAddAggroListEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}

	public List<Integer> getOnAddAggroListEvent() {
		return onAddAggroListEvent;
	}

	public void addOnAtDistanceEvent(int questId) {
		if (!onAtDistanceEvent.contains(questId)) {
			onAtDistanceEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}

	public List<Integer> getOnDistanceEvent() {
		return onAtDistanceEvent;
	}

	public int getNpcId() {
		return npcId;
	}
}
