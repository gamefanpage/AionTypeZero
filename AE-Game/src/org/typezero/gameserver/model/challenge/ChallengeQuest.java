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

import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.templates.challenge.ChallengeQuestTemplate;


/**
 * @author ViAl
 *
 */
public class ChallengeQuest {
	
	private final ChallengeQuestTemplate template;
	private int completeCount;
	private PersistentState persistentState;
	
	/**
	 * @param template
	 * @param completeCount
	 */
	public ChallengeQuest(ChallengeQuestTemplate template, int completeCount) {
		this.template = template;
		this.completeCount = completeCount;
	}

	public int getQuestId() {
		return template.getId();
	}
	
	public ChallengeQuestTemplate getQuestTemplate() {
		return template;
	}

	public int getMaxRepeats() {
		return template.getRepeatCount();
	}

	public int getScorePerQuest() {
		return template.getScore();
	}

	public int getCompleteCount() {
		return completeCount;
	}
	
	public synchronized void increaseCompleteCount() {
		this.completeCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public PersistentState getPersistentState() {
		return persistentState;
	}

	public void setPersistentState(PersistentState persistentState) {
		if(this.persistentState == PersistentState.NEW && persistentState == PersistentState.UPDATE_REQUIRED)
			return;
		this.persistentState = persistentState;
	}
}