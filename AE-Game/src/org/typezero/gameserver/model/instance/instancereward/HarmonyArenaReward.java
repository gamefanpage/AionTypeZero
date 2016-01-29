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

import static ch.lambdaj.Lambda.*;
import org.typezero.gameserver.model.autogroup.AGPlayer;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.playerreward.HarmonyGroupReward;
import org.typezero.gameserver.model.instance.playerreward.InstancePlayerReward;
import org.typezero.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.Comparator;
import java.util.List;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class HarmonyArenaReward extends PvPArenaReward {

	private FastList<HarmonyGroupReward> groups = new FastList<HarmonyGroupReward>();
	public HarmonyArenaReward(Integer mapId, int instanceId, WorldMapInstance instance) {
		super(mapId, instanceId, instance);
	}

	public HarmonyGroupReward getHarmonyGroupReward(Integer object) {
		for (InstancePlayerReward reward : groups) {
			HarmonyGroupReward harmonyReward = (HarmonyGroupReward) reward;
			if (harmonyReward.containPlayer(object)) {
				return harmonyReward;
			}
		}
		return null;
	}

	public FastList<HarmonyGroupReward> getHarmonyGroupInside() {
		FastList<HarmonyGroupReward> harmonyGroups = new FastList<HarmonyGroupReward>();
		for (HarmonyGroupReward group : groups) {
			for (AGPlayer agp : group.getAGPlayers()) {
				if (agp.isInInstance()) {
					harmonyGroups.add(group);
					break;
				}
			}
		}
		return harmonyGroups;
	}

	public FastList<Player> getPlayersInside(HarmonyGroupReward group) {
		FastList<Player> players = new FastList<Player>();
		for (Player playerInside : instance.getPlayersInside()) {
			if (group.containPlayer(playerInside.getObjectId())) {
				players.add(playerInside);
			}
		}
		return players;
	}

	public void addHarmonyGroup(HarmonyGroupReward reward) {
		groups.add(reward);
	}

	public FastList<HarmonyGroupReward> getGroups() {
		return groups;
	}

	public void sendPacket(final int type, final Integer object) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(type, getTime(), getInstanceReward(), object));
			}

		});
	}

	@Override
	public int getRank(int points) {
		int rank = -1;
		for (HarmonyGroupReward reward : sortGroupPoints()) {
			if (reward.getPoints() >= points) {
				rank++;
			}
		}
		return rank;
	}

	public List<HarmonyGroupReward> sortGroupPoints() {
		return sort(groups, on(HarmonyGroupReward.class).getPoints(), new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 != null ? o2.compareTo(o1) : -o1.compareTo(o2);
			}

		});
	}

	@Override
	public int getTotalPoints() {
		return sum(groups, on(HarmonyGroupReward.class).getPoints());
	}

	@Override
	public void clear() {
		groups.clear();
		super.clear();
	}

}
