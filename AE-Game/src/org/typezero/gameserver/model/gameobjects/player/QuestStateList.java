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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.QuestsData;
import org.typezero.gameserver.model.templates.quest.QuestCategory;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import javolution.util.FastList;

/**
 * @author MrPoke
 */
public class QuestStateList {

	private static final Logger log = LoggerFactory.getLogger(QuestStateList.class);

	private final SortedMap<Integer, QuestState> _quests;
	private QuestsData _questData = DataManager.QUEST_DATA;

	/**
	 * Creates an empty quests list
	 */
	public QuestStateList() {
		_quests = new TreeMap<Integer, QuestState>();
	}

	public synchronized boolean addQuest(int questId, QuestState questState) {
		if (_quests.containsKey(questId)) {
			log.warn("Duplicate quest. ");
			return false;
		}
		_quests.put(questId, questState);
		return true;
	}

	public synchronized boolean removeQuest(int questId) {
		if (_quests.containsKey(questId)) {
			_quests.remove(questId);
			return true;
		}
		return false;
	}

	public QuestState getQuestState(int questId) {
		return _quests.get(questId);
	}

	public Collection<QuestState> getAllQuestState() {
		return _quests.values();
	}

	public FastList<QuestState> getAllFinishedQuests() {
		FastList<QuestState> completeQuestList = FastList.newInstance();
		for (QuestState qs : _quests.values()) {
			if (qs.getStatus() == QuestStatus.COMPLETE) {
				completeQuestList.add(qs);
			}
		}
		return completeQuestList;
	}

	/*
	 * Issue #13 fix Used by the QuestService to check the amount of normal quests in the player's list
	 * @author vlog
	 */
	public int getNormalQuestListSize() {
		return this.getNormalQuests().size();
	}

	/*
	 * Issue #13 fix Returns the list of normal quests
	 * @author vlog
	 */
	public Collection<QuestState> getNormalQuests() {
		Collection<QuestState> l = new ArrayList<QuestState>();

		for (QuestState qs : this.getAllQuestState()) {
			QuestCategory qc = _questData.getQuestById(qs.getQuestId()).getCategory();
			QuestStatus s = qs.getStatus();

			if (s != QuestStatus.COMPLETE && s != QuestStatus.LOCKED && s != QuestStatus.NONE && qc == QuestCategory.QUEST) {
				l.add(qs);
			}
		}
		return l;
	}

	/*
	 * Returns true if there is a quest in the list with this id Used by the QuestService
	 * @author vlog
	 */
	public boolean hasQuest(int questId) {
		return _quests.containsKey(questId);
	}

	/*
	 * Change the old value of the quest status to the new one Used by the QuestService
	 * @author vlog
	 */
	public void changeQuestStatus(Integer key, QuestStatus newStatus) {
		_quests.get(key).setStatus(newStatus);
	}

	public int size() {
		return this._quests.size();
	}

	public SortedMap<Integer, QuestState> getQuests() {
		return this._quests;
	}
}
