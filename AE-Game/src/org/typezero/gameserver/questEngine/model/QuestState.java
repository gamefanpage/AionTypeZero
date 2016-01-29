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

package org.typezero.gameserver.questEngine.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.templates.QuestTemplate;

/**
 * @author MrPoke
 * @modified vlog, Rolandas
 */
public class QuestState {

	private final int questId;
	private QuestVars questVars;
	private QuestStatus status;
	private int completeCount;
	private Timestamp completeTime;
	private Timestamp nextRepeatTime;
	private Integer reward;
	private PersistentState persistentState;

	private static final Logger log = LoggerFactory.getLogger(QuestState.class);

	public QuestState(int questId, QuestStatus status, int questVars, int completeCount, Timestamp nextRepeatTime,
		Integer reward, Timestamp completeTime) {
		this.questId = questId;
		this.status = status;
		this.questVars = new QuestVars(questVars);
		this.completeCount = completeCount;
		this.nextRepeatTime = nextRepeatTime;
		this.reward = reward;
		this.completeTime = completeTime;
		this.persistentState = PersistentState.NEW;
	}

	public QuestVars getQuestVars() {
		return questVars;
	}

	/**
	 * @param id
	 * @param var
	 */
	public void setQuestVarById(int id, int var) {
		questVars.setVarById(id, var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param id
	 * @return Quest var by id.
	 */
	public int getQuestVarById(int id) {
		return questVars.getVarById(id);
	}

	public void setQuestVar(int var) {
		questVars.setVar(var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public QuestStatus getStatus() {
		return status;
	}

	public void setStatus(QuestStatus status) {
		if (status == QuestStatus.COMPLETE && this.status != QuestStatus.COMPLETE)
			updateCompleteTime();
		this.status = status;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public Timestamp getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Timestamp time) {
		completeTime = time;
	}

	public void updateCompleteTime() {
		completeTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public int getQuestId() {
		return questId;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public int getCompleteCount() {
		return completeCount;
	}

	public void setNextRepeatTime(Timestamp nextRepeatTime) {
		this.nextRepeatTime = nextRepeatTime;
	}

	public Timestamp getNextRepeatTime() {
		return nextRepeatTime;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public Integer getReward() {
		if (reward == null) {
			log.warn("No reward for the quest " + String.valueOf(questId));
		}
		else {
			return reward;
		}
		return 0;
	}

	public boolean canRepeat() {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (status != QuestStatus.NONE
			&& (status != QuestStatus.COMPLETE || (completeCount >= template.getMaxRepeatCount() && template
				.getMaxRepeatCount() != 255))) {
			return false;
		}
		if (questVars.getQuestVars() != 0) {
			return false;
		}
		if (template.isTimeBased() && nextRepeatTime != null) {
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			if (currentTime.before(nextRepeatTime)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the pState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 *          the pState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
	}
}
