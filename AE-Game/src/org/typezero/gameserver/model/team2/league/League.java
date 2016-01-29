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

package org.typezero.gameserver.model.team2.league;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.GeneralTeam;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceMember;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * @author ATracer
 */
public class League extends GeneralTeam<PlayerAlliance, LeagueMember> {

	private LootGroupRules lootGroupRules = new LootGroupRules();
	private static final LeagueMemberComparator MEMBER_COMPARATOR = new LeagueMemberComparator();

	public League(LeagueMember leader) {
		super(IDFactory.getInstance().nextId());
		initializeTeam(leader);
	}

	protected final void initializeTeam(LeagueMember leader) {
		setLeader(leader);
	}

	@Override
	public Collection<PlayerAlliance> getOnlineMembers() {
		return getMembers();
	}

	@Override
	public void addMember(LeagueMember member) {
		super.addMember(member);
		member.getObject().setLeague(this);
	}

	@Override
	public void removeMember(LeagueMember member) {
		super.removeMember(member);
		member.getObject().setLeague(null);
	}

	@Override
	public void sendPacket(AionServerPacket packet) {
		for (PlayerAlliance alliance : getMembers()) {
			alliance.sendPacket(packet);
		}
	}

	@Override
	public void sendPacket(AionServerPacket packet, Predicate<PlayerAlliance> predicate) {
		for (PlayerAlliance alliance : getMembers()) {
			if (predicate.apply(alliance)) {
				alliance.sendPacket(packet, Predicates.<Player> alwaysTrue());
			}
		}
	}

	@Override
	public int onlineMembers() {
		return getMembers().size();
	}

	@Override
	public Race getRace() {
		return getLeaderObject().getRace();
	}

	@Override
	public boolean isFull() {
		return size() == 8;
	}

	public LootGroupRules getLootGroupRules() {
		return lootGroupRules;
	}

	public void setLootGroupRules(LootGroupRules lootGroupRules) {
		this.lootGroupRules = lootGroupRules;
	}

	/**
	 * @return sorted alliances by position
	 */
	public Collection<LeagueMember> getSortedMembers() {
		ArrayList<LeagueMember> newArrayList = Lists.newArrayList(members.values());
		Collections.sort(newArrayList, MEMBER_COMPARATOR);
		return newArrayList;
	}

	/**
	 * Search for player member in all alliances
	 *
	 * @return player object
	 */
	public Player getPlayerMember(Integer playerObjId) {
		for (PlayerAlliance member : getMembers()) {
			PlayerAllianceMember playerMember = member.getMember(playerObjId);
			if (playerMember != null) {
				return playerMember.getObject();
			}
		}
		return null;
	}

	static class LeagueMemberComparator implements Comparator<LeagueMember> {

		@Override
		public int compare(LeagueMember o1, LeagueMember o2) {
			return o1.getLeaguePosition() > o2.getLeaguePosition() ? 1 : -1;
		}

	}

}
