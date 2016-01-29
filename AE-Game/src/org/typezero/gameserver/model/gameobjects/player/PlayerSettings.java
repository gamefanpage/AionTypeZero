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

import org.typezero.gameserver.model.gameobjects.PersistentState;

/**
 * @author ATracer
 */
public class PlayerSettings {

	private PersistentState persistentState;

	private byte[] uiSettings;
	private byte[] shortcuts;
	private byte[] houseBuddies;
	private int deny = 0;
	private int display = 0;

	public PlayerSettings() {

	}

	public PlayerSettings(byte[] uiSettings, byte[] shortcuts, byte[] houseBuddies, int deny, int display) {
		this.uiSettings = uiSettings;
		this.shortcuts = shortcuts;
		this.houseBuddies = houseBuddies;
		this.deny = deny;
		this.display = display;
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
		this.persistentState = persistentState;
	}

	/**
	 * @return the uiSettings
	 */
	public byte[] getUiSettings() {
		return uiSettings;
	}

	/**
	 * @param uiSettings
	 *          the uiSettings to set
	 */
	public void setUiSettings(byte[] uiSettings) {
		this.uiSettings = uiSettings;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}

	/**
	 * @return the shortcuts
	 */
	public byte[] getShortcuts() {
		return shortcuts;
	}

	/**
	 * @param shortcuts
	 *          the shortcuts to set
	 */
	public void setShortcuts(byte[] shortcuts) {
		this.shortcuts = shortcuts;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}

	/**
	 * @return the houseBuddies
	 */
	public byte[] getHouseBuddies() {
		return houseBuddies;
	}

	/**
	 * @param houseBuddies
	 *          the houseBuddies to set
	 */
	public void setHouseBuddies(byte[] houseBuddies) {
		this.houseBuddies = houseBuddies;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}

	/**
	 * @return the display
	 */
	public int getDisplay() {
		return display;
	}

	/**
	 * @param display
	 *          the display to set
	 */
	public void setDisplay(int display) {
		this.display = display;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}

	/**
	 * @return the deny
	 */
	public int getDeny() {
		return deny;
	}

	/**
	 * @param deny
	 *          the deny to set
	 */
	public void setDeny(int deny) {
		this.deny = deny;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}

	public boolean isInDeniedStatus(DeniedStatus deny) {
		int isDeniedStatus = this.deny & deny.getId();

		if (isDeniedStatus == deny.getId())
			return true;

		return false;
	}
}
