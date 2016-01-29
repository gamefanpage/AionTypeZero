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

import static ch.lambdaj.Lambda.*;
import com.aionemu.commons.taskmanager.AbstractLockManager;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xTz
 */
public abstract class AutoInstance extends AbstractLockManager implements AutoInstanceHandler {

	protected int instanceMaskId;
	public long startInstanceTime;
	public WorldMapInstance instance;
	public AutoGroupType agt;
	public Map<Integer, AGPlayer> players = new HashMap<Integer, AGPlayer>();

	protected boolean decrease(Player player, int itemId, long count) {
		long i = 0;
		List<Item> items = player.getInventory().getItemsByItemId(itemId);
		for (Item findedItem : items) {
			i += findedItem.getItemCount();
		}
		if (i < count) {
			return false;
		}
		items = sort(items, on(Item.class).getExpireTime());
		for (Item item : items) {
			long l = player.getInventory().decreaseItemCount(item, count);
			if (l == 0) {
				break;
			}
			else {
				count = l;
			}
		}
		return true;
	}

	@Override
	public void initialize(int instanceMaskId) {
		this.instanceMaskId = instanceMaskId;
		agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		this.instance = instance;
		startInstanceTime = System.currentTimeMillis();
	}

	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		return AGQuestion.FAILED;
	}

	@Override
	public void onEnterInstance(Player player) {
		players.get(player.getObjectId()).setInInstance(true);
		players.get(player.getObjectId()).setOnline(true);
	}

	@Override
	public void onLeaveInstance(Player player) {
	}

	@Override
	public void onPressEnter(Player player) {
		players.get(player.getObjectId()).setPressEnter(true);
	}

	@Override
	public void unregister(Player player) {
		Integer obj = player.getObjectId();
		if (players.containsKey(obj)) {
			players.remove(obj);
		}
	}

	@Override
	public void clear() {
		players.clear();
	}

	protected boolean satisfyTime(SearchInstance searchInstance) {
		if (instance != null) {
			InstanceReward<?> instanceReward = instance.getInstanceHandler().getInstanceReward();
			if ((instanceReward != null && instanceReward.getInstanceScoreType().isEndProgress())) {
				return false;
			}
		}

		if (!searchInstance.getEntryRequestType().isQuickGroupEntry()) {
			return startInstanceTime == 0;
		}

		int time = agt.getTime();
		if (time == 0 || startInstanceTime == 0) {
			return true;
		}
		return System.currentTimeMillis() - startInstanceTime < time;
	}

}
