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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.model.gameobjects.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Simple
 */
public class DropNpc {

	private Collection<Integer> allowedList = new ArrayList<Integer>();
	private Collection<Player> inRangePlayers = new ArrayList<Player>();
	private Collection<Player> playerStatus = new ArrayList<Player>();
	private Player lootingPlayer = null;
	private int distributionId = 0;
	private boolean distributionType;
	private int currentIndex = 0;
	private int groupSize = 0;
	private boolean isFreeForAll = false;
	private final int npcUniqueId;
	private long reamingDecayTime;

	public DropNpc(int npcUniqueId) {
		this.npcUniqueId = npcUniqueId;
	}

	public void setPlayersObjectId(List<Integer> allowedList) {
		this.allowedList = allowedList;
	}

	public void setPlayerObjectId(Integer object) {
		if (!allowedList.contains(object)) {
			allowedList.add(object);
		}
	}

	public Collection<Integer> getPlayersObjectId() {
		return allowedList;
	}

	/**
	 * @return true if playerObjId is found in list
	 */
	public boolean containsKey(int playerObjId) {
		return allowedList.contains(playerObjId);
	}

	/**
	 * @param player
	 *          the lootingPlayer to set
	 */
	public void setBeingLooted(Player player) {
		this.lootingPlayer = player;
	}

	/**
	 * @return lootingPlayer
	 */
	public Player getBeingLooted() {
		return lootingPlayer;
	}

	/**
	 * @return the beingLooted
	 */
	public boolean isBeingLooted() {
		return lootingPlayer != null;
	}

	/**
	 * @param distributionId
	 */
	public void setDistributionId(int distributionId) {
		this.distributionId = distributionId;
	}

	/**
	 * @return the DistributionId
	 */
	public int getDistributionId() {
		return distributionId;
	}

	/**
	 * @param distributionType
	 */
	public void setDistributionType(boolean distributionType) {
		this.distributionType = distributionType;
	}

	/**
	 * @return the DistributionType
	 */
	public boolean getDistributionType() {
		return distributionType;
	}

	/**
	 * @param currentIndex
	 */
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	/**
	 * @return currentIndex
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * @param groupSize
	 */
	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}

	/**
	 * @return groupSize
	 */
	public int getGroupSize() {
		return groupSize;
	}

	/**
	 * @param inRangePlayers
	 */
	public void setInRangePlayers(Collection<Player> inRangePlayers) {
		this.inRangePlayers = inRangePlayers;
	}

	/**
	 * @return the inRangePlayers
	 */
	public Collection<Player> getInRangePlayers() {
		return inRangePlayers;
	}

	/**
	 * @param addPlayerStatus
	 */
	public void addPlayerStatus(Player player) {
		playerStatus.add(player);
	}

	/**
	 * @param delPlayerStatus
	 */
	public void delPlayerStatus(Player player) {
		playerStatus.remove(player);
	}

	/**
	 * @return the playerStatus
	 */
	public Collection<Player> getPlayerStatus() {
		return playerStatus;
	}

	/**
	 * @return true if player is found in list
	 */
	public boolean containsPlayerStatus(Player player) {
		return playerStatus.contains(player);
	}

	/**
	 * @return isFreeForAll.
	 */
	public boolean isFreeForAll() {
		return isFreeForAll;
	}

	public void startFreeForAll() {
		isFreeForAll = true;
		distributionId = 0;
		allowedList.clear();
	}

	public final int getNpcUniqueId() {
		return npcUniqueId;
	}

	public long getReamingDecayTime() {
		return reamingDecayTime;
	}

	public void setReamingDecayTime(long reamingDecayTime) {
		this.reamingDecayTime = reamingDecayTime;
	}
}
