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

package org.typezero.gameserver.model.gameobjects.player.npcFaction;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;

/**
 * @author MrPoke
 */
public class NpcFaction {

	private int id;
	private int time;
	private boolean active;
	private boolean mentor;
	private ENpcFactionQuestState state;
	private int questId;
	private PersistentState persistentState;

	/**
	 * @param id
	 * @param time
	 * @param active
	 * @param persistentState
	 * @param mentor
	 * @param state
	 */
	public NpcFaction(int id, int time, boolean active, ENpcFactionQuestState state, int questId) {
		this.id = id;
		this.time = time;
		this.active = active;
		this.state = state;
		this.mentor = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(id).isMentor();
		this.questId = questId;
		this.persistentState = PersistentState.NEW;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return the mentor
	 */
	public boolean isMentor() {
		return mentor;
	}

	/**
	 * @return the state
	 */
	public ENpcFactionQuestState getState() {
		return state;
	}

	/**
	 * @param time
	 *          the time to set
	 */
	public void setTime(int time) {
		this.time = time;
		this.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param active
	 *          the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
		this.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param state
	 *          the state to set
	 */
	public void setState(ENpcFactionQuestState state) {
		this.setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.state = state;
	}

	/**
	 * @return the questId
	 */
	public int getQuestId() {
		return questId;
	}

	/**
	 * @param questId
	 *          the questId to set
	 */
	public void setQuestId(int questId) {
		this.questId = questId;
		this.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 *          the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
					this.persistentState = PersistentState.NOACTION;
				else
					this.persistentState = PersistentState.DELETED;
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState != PersistentState.NEW)
					this.persistentState = PersistentState.UPDATE_REQUIRED;
				break;
			case NOACTION:
				break;
			default:
				this.persistentState = persistentState;
		}
	}

}
