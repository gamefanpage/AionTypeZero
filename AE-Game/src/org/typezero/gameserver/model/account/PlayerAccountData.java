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

package org.typezero.gameserver.model.account;

import java.sql.Timestamp;
import java.util.List;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionMember;

/**
 * This class is holding information about player, that is displayed on char selection screen, such as: player
 * commondata, player's appearance and creation/deletion time.
 *
 * @see PlayerCommonData
 * @see PlayerAppearance
 * @author Luno
 */
public class PlayerAccountData {

	private CharacterBanInfo cbi;
	private PlayerCommonData playerCommonData;
	private PlayerAppearance appereance;
	private List<Item> equipment;
	private Timestamp creationDate;
	private Timestamp deletionDate;
	private LegionMember legionMember;

	public PlayerAccountData(PlayerCommonData playerCommonData, CharacterBanInfo cbi, PlayerAppearance appereance, List<Item> equipment,
		LegionMember legionMember) {
		this.playerCommonData = playerCommonData;
		this.cbi = cbi;
		this.appereance = appereance;
		this.equipment = equipment;
		this.legionMember = legionMember;
	}

	public CharacterBanInfo getCharBanInfo() {
		return cbi;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets deletion date.
	 *
	 * @param deletionDate
	 */
	public void setDeletionDate(Timestamp deletionDate) {
		this.deletionDate = deletionDate;
	}

	/**
	 * Get deletion date.
	 *
	 * @return Timestamp date when char should be deleted.
	 */
	public Timestamp getDeletionDate() {
		return deletionDate;
	}

	/**
	 * Get time in seconds when this player will be deleted ( 0 if player was not set to be deleted )
	 *
	 * @return deletion time in seconds
	 */
	public int getDeletionTimeInSeconds() {
		return deletionDate == null ? 0 : (int) (deletionDate.getTime() / 1000);
	}

	/**
	 * @return the playerCommonData
	 */
	public PlayerCommonData getPlayerCommonData() {
		return playerCommonData;
	}

	/**
	 * @param playerCommonData
	 *          the playerCommonData to set
	 */
	public void setPlayerCommonData(PlayerCommonData playerCommonData) {
		this.playerCommonData = playerCommonData;
	}

	public PlayerAppearance getAppereance() {
		return appereance;
	}

	/**
	 * @param timestamp
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the legionMember
	 */
	public Legion getLegion() {
		return legionMember.getLegion();
	}

	/**
	 * Returns true if player is a legion member
	 *
	 * @return true or false
	 */
	public boolean isLegionMember() {
		return legionMember != null;
	}

	/**
	 * @return the equipment
	 */
	public List<Item> getEquipment() {
		return equipment;
	}

	/**
	 * @param equipment
	 *          the equipment to set
	 */
	public void setEquipment(List<Item> equipment) {
		this.equipment = equipment;
	}
}
