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

package org.typezero.gameserver.model.instance.instancereward;

import org.typezero.gameserver.model.instance.InstanceScoreType;
import org.typezero.gameserver.model.instance.playerreward.InstancePlayerReward;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class InstanceReward<T extends InstancePlayerReward> {

	protected FastList<T> instanceRewards = new FastList<T>();
	private InstanceScoreType instanceScoreType = InstanceScoreType.START_PROGRESS;
	protected Integer mapId;
	protected int instanceId;

	public InstanceReward(Integer mapId, int instanceId) {
		this.mapId = mapId;
		this.instanceId = instanceId;
	}

	public FastList<T> getInstanceRewards() {
		return instanceRewards;
	}

	public boolean containPlayer(Integer object) {
		for (InstancePlayerReward instanceReward : instanceRewards) {
			if (instanceReward.getOwner().equals(object)) {
				return true;
			}
		}
		return false;
	}

	public void removePlayerReward(T reward) {
		if (instanceRewards.contains(reward)) {
			instanceRewards.remove(reward);
		}
	}

	public InstancePlayerReward getPlayerReward(Integer object) {
		for (InstancePlayerReward instanceReward : instanceRewards) {
			if (instanceReward.getOwner().equals(object)) {
				return instanceReward;
			}
		}
		return null;
	}

	public void addPlayerReward(T reward) {
		instanceRewards.add(reward);
	}

	public void setInstanceScoreType(InstanceScoreType instanceScoreType) {
		this.instanceScoreType = instanceScoreType;
	}

	public InstanceScoreType getInstanceScoreType() {
		return instanceScoreType;
	}

	public Integer getMapId() {
		return mapId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public boolean isRewarded() {
		return instanceScoreType.isEndProgress();
	}

	public boolean isPreparing() {
		return instanceScoreType.isPreparing();
	}

	public boolean isStartProgress() {
		return instanceScoreType.isStartProgress();
	}

	public void clear() {
		instanceRewards.clear();
	}

	protected InstanceReward<?> getInstanceReward()  {
		return this;
	}
}
