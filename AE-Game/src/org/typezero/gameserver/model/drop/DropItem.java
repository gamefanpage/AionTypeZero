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

package org.typezero.gameserver.model.drop;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public class DropItem {

	private int index = 0;
	private long count = 0;
	private Drop dropTemplate;
	private int playerObjId = 0;
	private boolean isFreeForAll = false;
	private long highestValue = 0;
	private Player winningPlayer = null;
	private boolean isItemWonNotCollected = false;
	private boolean isDistributeItem = false;
	private int npcObj;

	public DropItem(Drop dropTemplate) {
		this.dropTemplate = dropTemplate;
	}

	/**
	 * Regenerates item count upon each call // TODO input parameters - based on attacker stats // TODO more precise
	 * calculations (non-linear)
	 */
	public void calculateCount() {
		count = Rnd.get(dropTemplate.getMinAmount(), dropTemplate.getMaxAmount());
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *          the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * @return the dropTemplate
	 */
	public Drop getDropTemplate() {
		return dropTemplate;
	}

	/**
	 * @return the playerObjId
	 */
	public int getPlayerObjId() {
		return playerObjId;
	}

	/**
	 * @param playerObjId
	 *          the playerObjId to set
	 */
	public void setPlayerObjId(int playerObjId) {
		this.playerObjId = playerObjId;
	}

	/**
	 * @param isFreeForAll
	 *          to set
	 */
	public void isFreeForAll(boolean isFreeForAll) {
		this.isFreeForAll = isFreeForAll;
	}

	/**
	 * @return isFreeForAll
	 */
	public boolean isFreeForAll() {
		return isFreeForAll;
	}

	/**
	 * @return highestValue
	 */
	public long getHighestValue() {
		return highestValue;
	}

	/**
	 * @param highestValue
	 *          to set
	 */
	public void setHighestValue(long highestValue) {
		this.highestValue = highestValue;
	}

	/**
	 * @param WinningPlayer
	 *          to set
	 */
	public void setWinningPlayer(Player winningPlayer) {
		this.winningPlayer = winningPlayer;

	}

	/**
	 * @return winningPlayer
	 */
	public Player getWinningPlayer() {
		return winningPlayer;
	}

	/**
	 * @param isItemWonNotCollected
	 *          to set
	 */
	public void isItemWonNotCollected(boolean isItemWonNotCollected) {
		this.isItemWonNotCollected = isItemWonNotCollected;
	}

	/**
	 * @return isItemWonNotCollected
	 */
	public boolean isItemWonNotCollected() {
		return isItemWonNotCollected;
	}

	/**
	 * @param isDistributeItem
	 *          to set
	 */
	public void isDistributeItem(boolean isDistributeItem) {
		this.isDistributeItem = isDistributeItem;
	}

	/**
	 * @return isDistributeItem
	 */
	public boolean isDistributeItem() {
		return isDistributeItem;
	}

	public int getNpcObj() {
		return npcObj;
	}

	public void setNpcObj(int npcObj) {
		this.npcObj = npcObj;
	}

}
