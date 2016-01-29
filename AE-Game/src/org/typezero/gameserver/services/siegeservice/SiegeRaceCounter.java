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

package org.typezero.gameserver.services.siegeservice;

import com.aionemu.commons.utils.GenericValidator;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.world.World;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import javolution.util.FastMap;

/**
 * A class that contains all the counters for the siege. One SiegeCounter per race should be used.
 *
 * @author SoulKeeper
 */
public class SiegeRaceCounter implements Comparable<SiegeRaceCounter> {

	private final AtomicLong totalDamage = new AtomicLong();

	private final Map<Integer, AtomicLong> playerDamageCounter = new FastMap<Integer, AtomicLong>().shared();

	private final Map<Integer, AtomicLong> playerAPCounter = new FastMap<Integer, AtomicLong>().shared();

	private final SiegeRace siegeRace;

	public SiegeRaceCounter(SiegeRace siegeRace) {
		this.siegeRace = siegeRace;
	}

	public void addPoints(Creature creature, int damage) {

		addTotalDamage(damage);

		if (creature instanceof Player) {
			addPlayerDamage((Player) creature, damage);
		}
	}

	public void addTotalDamage(int damage) {
		totalDamage.addAndGet(damage);
	}

	public void addPlayerDamage(Player player, int damage) {
		addToCounter(player.getObjectId(), damage, playerDamageCounter);
	}

	public void addAbyssPoints(Player player, int abyssPoints) {
		addToCounter(player.getObjectId(), abyssPoints, playerAPCounter);
	}

	protected <K> void addToCounter(K key, int value, Map<K, AtomicLong> counterMap) {

		// Get the counter for specific key
		AtomicLong counter = counterMap.get(key);

		// Counter was not registered, need to create it
		if (counter == null) {

			// synchronize here, it may happen that there will be attempt to increment
			// same counter from different threads
			synchronized (this) {
				if (counterMap.containsKey(key)) {
					counter = counterMap.get(key);
				} else {
					counter = new AtomicLong();
					counterMap.put(key, counter);
				}
			}
		}

		counter.addAndGet(value);
	}

	public long getTotalDamage() {
		return totalDamage.get();
	}

	/**
	 * Returns "playerId to damage" map.
	 * Map is ordered by damage in "descending" order
	 *
	 * @return map with player damages
	 */
	public Map<Integer, Long> getPlayerDamageCounter() {
		return getOrderedCounterMap(playerDamageCounter);
	}

	/**
	 * Returns "player to abyss points" map.
	 * Map is ordered by abyssPoints in descending order
	 *
	 * @return map with player abyss points
	 */
	public Map<Integer, Long> getPlayerAbyssPoints() {
		return getOrderedCounterMap(playerAPCounter);
	}

	protected <K> Map<K, Long> getOrderedCounterMap(Map<K, AtomicLong> unorderedMap) {
		if (GenericValidator.isBlankOrNull(unorderedMap)) {
			return Collections.emptyMap();
		}

		LinkedList<Map.Entry<K, AtomicLong>> tempList = Lists.newLinkedList(unorderedMap.entrySet());
		Collections.sort(tempList, new Comparator<Map.Entry<K, AtomicLong>>() {
			@Override
			public int compare(Map.Entry<K, AtomicLong> o1, Map.Entry<K, AtomicLong> o2) {
				return new Long(o2.getValue().get()).compareTo(o1.getValue().get());
			}
		});

		Map<K, Long> result = Maps.newLinkedHashMap();
		for (Map.Entry<K, AtomicLong> entry : tempList) {
			if (entry.getValue().get() > 0) {
				result.put(entry.getKey(), entry.getValue().get());
			}
		}
		return result;
	}

	@Override
	public int compareTo(SiegeRaceCounter o) {
		return new Long(o.getTotalDamage()).compareTo(getTotalDamage());
	}

	public SiegeRace getSiegeRace() {
		return siegeRace;
	}

	/**
	 * Returns Legion of the Leader of the strongest Team
	 *
	 * @return legion id or null if none
	 */
	public Integer getWinnerLegionId() {
		Map<Player, AtomicLong> teamDamageMap = new HashMap<Player, AtomicLong>();
		for (Integer id : playerDamageCounter.keySet()) {
			Player player = World.getInstance().findPlayer(id);

			if (player != null) {
				if (player.getCurrentTeam() != null) {
					Player teamLeader = player.getCurrentTeam().getLeaderObject();
					long damage = playerDamageCounter.get(id).get();
					if (teamLeader != null) {
						if (!teamDamageMap.containsKey(teamLeader)) {
							teamDamageMap.put(teamLeader, new AtomicLong());
						}
						teamDamageMap.get(teamLeader).addAndGet(damage);
					}
				}
			}
		}
		if (teamDamageMap.isEmpty()) {
			return null;
		}

		Player topTeamLeader = getOrderedCounterMap(teamDamageMap).keySet().iterator().next();
		Legion legion = topTeamLeader.getLegion();

		return legion != null ? legion.getLegionId() : null;
	}
}
