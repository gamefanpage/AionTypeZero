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

package org.typezero.gameserver.model.autogroup;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.List;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class LookingForParty extends AbstractLockManager {

	private List<SearchInstance> searchInstances = new ArrayList<SearchInstance>();
	private Player player;
	private long startEnterTime;
	private long penaltyTime;

	public LookingForParty(Player player, int instanceMaskId, EntryRequestType ert) {
		this.player = player;
		searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry()
				? player.getPlayerGroup2().getOnlineMembers() : null));
	}

	public int unregisterInstance(int instanceMaskId) {
		super.writeLock();
		try {
			for (SearchInstance si : searchInstances) {
				if (si.getInstanceMaskId() == instanceMaskId) {
					searchInstances.remove(si);
					return searchInstances.size();
				}
			}
			return searchInstances.size();
		}
		finally {
			super.writeUnlock();
		}
	}

	public List<SearchInstance> getSearchInstances() {
		FastList<SearchInstance> tempList = FastList.newInstance();
		for (SearchInstance si : searchInstances) {
			tempList.add(si);
		}
		return tempList;
	}

	public void addInstanceMaskId(int instanceMaskId, EntryRequestType ert) {
		super.writeLock();
		try {
			searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry()
					? player.getPlayerGroup2().getOnlineMembers() : null));
		}
		finally {
			super.writeUnlock();
		}
	}

	public SearchInstance getSearchInstance(int instanceMaskId) {
		super.readLock();
		try {
			for (SearchInstance si : searchInstances) {
				if (si.getInstanceMaskId() == instanceMaskId) {
					return si;
				}
			}
			return null;
		}
		finally {
			super.readUnlock();
		}
	}

	public boolean isRegistredInstance(int instanceMaskId) {
		for (SearchInstance si : searchInstances) {
			if (si.getInstanceMaskId() == instanceMaskId) {
				return true;
			}
		}
		return false;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setPenaltyTime() {
		penaltyTime = System.currentTimeMillis();
	}

	public boolean hasPenalty() {
		return System.currentTimeMillis() - penaltyTime <= 10000;
	}

	public void setStartEnterTime() {
		startEnterTime = System.currentTimeMillis();
	}

	public boolean isOnStartEnterTask() {
		return System.currentTimeMillis() - startEnterTime <= 120000;
	}

}
