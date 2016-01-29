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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import org.typezero.gameserver.model.templates.challenge.ChallengeTaskTemplate;

/**
 *
 * @author ViAl
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "task" })
@XmlRootElement(name = "challenge_tasks")
public class ChallengeData {

	protected List<ChallengeTaskTemplate> task;

	@XmlTransient
	protected Map<Integer, ChallengeTaskTemplate> tasksById = new HashMap<Integer, ChallengeTaskTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (ChallengeTaskTemplate t : task) {
			tasksById.put(t.getId(), t);
		}
		task.clear();
		task = null;
	}

	public Map<Integer, ChallengeTaskTemplate> getTasks() {
		return this.tasksById;
	}

	public ChallengeTaskTemplate getTaskByTaskId(int taskId) {
		return tasksById.get(taskId);
	}

	public ChallengeTaskTemplate getTaskByQuestId(int questId) {
		for (ChallengeTaskTemplate ct : tasksById.values()) {
			for (ChallengeQuestTemplate cq : ct.getQuests())
				if (cq.getId() == questId)
					return ct;
		}
		return null;
	}

	public ChallengeQuestTemplate getQuestByQuestId(int questId) {
		for (ChallengeTaskTemplate ct : tasksById.values()) {
			for (ChallengeQuestTemplate cq : ct.getQuests())
				if (cq.getId() == questId)
					return cq;
		}
		return null;
	}

	public int size() {
		return this.tasksById.size();
	}
}
