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

package org.typezero.gameserver.taskmanager.tasks;

import java.util.Collection;
import java.util.Map;

import javolution.util.FastMap;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.taskmanager.AbstractPeriodicTaskManager;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author Mr. Poke
 */
public class TemporaryTradeTimeTask extends AbstractPeriodicTaskManager {

	private final FastMap<Item, Collection<Integer>> items = new FastMap<Item, Collection<Integer>>();
	private final FastMap<Integer, Item> itemById = new FastMap<Integer, Item>();

	/**
	 * @param period
	 */
	public TemporaryTradeTimeTask() {
		super(1000);
	}

	public static TemporaryTradeTimeTask getInstance() {
		return SingletonHolder._instance;
	}

	public void addTask(Item item, Collection<Integer> players) {
		writeLock();
		try {
			items.put(item, players);
			itemById.put(item.getObjectId(), item);
		}
		finally {
			writeUnlock();
		}
	}

	public boolean canTrade(Item item, int playerObjectId) {
		Collection<Integer> players = items.get(item);
		if (players == null)
			return false;
		return players.contains(playerObjectId);
	}

	public boolean hasItem(Item item) {
		readLock();
		try {
			return items.containsKey(item);
		}
		finally {
			readUnlock();
		}
	}

	public Item getItem(int objectId) {
		readLock();
		try {
			return itemById.get(objectId);
		}
		finally {
			readUnlock();
		}
	}

	@Override
	public void run() {
		writeLock();
		try {
			for (Map.Entry<Item, Collection<Integer>> entry : items.entrySet()) {
				Item item = entry.getKey();
				int time = (item.getTemporaryExchangeTime() - (int) (System.currentTimeMillis() / 1000));
				if (time == 60) {
					for (int playerId : entry.getValue()) {
						Player player = World.getInstance().findPlayer(playerId);
						if (player != null)
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_END_OF_EXCHANGE_TIME(item.getNameId(), time));
					}
				}
				else if (time <= 0) {
					for (int playerId : entry.getValue()) {
						Player player = World.getInstance().findPlayer(playerId);
						if (player != null)
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EXCHANGE_TIME_OVER(item.getNameId()));
					}
					item.setTemporaryExchangeTime(0);
					items.remove(item);
					itemById.remove(item.getObjectId());
				}
			}
		}
		finally {
			writeUnlock();
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final TemporaryTradeTimeTask _instance = new TemporaryTradeTimeTask();
	}
}
