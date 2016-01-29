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

package org.typezero.gameserver.model.challenge;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import org.typezero.gameserver.model.templates.challenge.ChallengeTaskTemplate;


/**
 * @author ViAl
 *
 */
public class ChallengeTask {

	private final int taskId;
	private final int ownerId;
	private Map<Integer, ChallengeQuest> quests;
	private Timestamp completeTime;
	private ChallengeTaskTemplate template;

	/**
	 * Used for loading tasks from DAO.
	 * @param header
	 * @param quests
	 * @param completeTime
	 */
	public ChallengeTask(int taskId, int ownerId, Map<Integer, ChallengeQuest> quests, Timestamp completeTime) {
		this.taskId = taskId;
		this.ownerId = ownerId;
		this.quests = quests;
		this.completeTime = completeTime;
		this.template = DataManager.CHALLENGE_DATA.getTaskByTaskId(taskId);
	}

	/**
	 * Used for creating new tasks in runtime.
	 * @param ownerId
	 * @param template
	 */
	public ChallengeTask(int ownerId, ChallengeTaskTemplate template) {
		this.taskId = template.getId();
		this.ownerId = ownerId;
		Map<Integer, ChallengeQuest> quests = new HashMap<Integer, ChallengeQuest>();
		for(ChallengeQuestTemplate qt : template.getQuests()) {
			ChallengeQuest quest = new ChallengeQuest(qt, 0);
			quest.setPersistentState(PersistentState.NEW);
			quests.put(qt.getId(), quest);
		}
		this.quests = quests;
		this.completeTime = new Timestamp(1000);
		this.template = template;
	}

	public int getTaskId() {
		return this.taskId;
	}

	public int getOwnerId() {
		return this.ownerId;
	}

	public int getQuestsCount() {
		return quests.size();
	}

	public Map<Integer, ChallengeQuest> getQuests() {
		return quests;
	}

	public ChallengeQuest getQuest(int questId) {
		return quests.get(questId);
	}

	public Timestamp getCompleteTime() {
		return completeTime;
	}

	public synchronized void updateCompleteTime() {
		completeTime.setTime(System.currentTimeMillis());
	}

	public ChallengeTaskTemplate getTemplate() {
		return this.template;
	}

	public boolean isCompleted() {
		boolean isCompleted = true;
		for(ChallengeQuest quest : quests.values()) {
		  if(quest.getCompleteCount() < quest.getMaxRepeats()) {
			  isCompleted = false;
			  break;
		  }
	   }
		return isCompleted;
	}
}
