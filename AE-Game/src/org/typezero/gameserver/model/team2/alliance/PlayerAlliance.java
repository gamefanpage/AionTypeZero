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

package org.typezero.gameserver.model.team2.alliance;

import com.google.common.base.Preconditions;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamType;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.model.team2.league.League;
import org.typezero.gameserver.utils.idfactory.IDFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ATracer
 */
public class PlayerAlliance extends TemporaryPlayerTeam<PlayerAllianceMember> {

	private final Map<Integer, PlayerAllianceGroup> groups = new HashMap<Integer, PlayerAllianceGroup>();
	private final Collection<Integer> viceCaptainIds = new CopyOnWriteArrayList<Integer>();
	private int allianceReadyStatus;
	private TeamType type;
	private League league;

	public PlayerAlliance(PlayerAllianceMember leader, TeamType type) {
		super(IDFactory.getInstance().nextId());
		this.type = type;
		initializeTeam(leader);
		for (int groupId = 1000; groupId <= 1003; groupId++) {
			groups.put(groupId, new PlayerAllianceGroup(this, groupId));
		}
	}

	@Override
	public void addMember(PlayerAllianceMember member) {
		super.addMember(member);
		PlayerAllianceGroup openAllianceGroup = getOpenAllianceGroup();
		openAllianceGroup.addMember(member);
	}

	@Override
	public void removeMember(PlayerAllianceMember member) {
		super.removeMember(member);
		member.getPlayerAllianceGroup().removeMember(member);
	}

	@Override
	public boolean isFull() {
		return size() == 24;
	}

	@Override
	public int getMinExpPlayerLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxExpPlayerLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public PlayerAllianceGroup getOpenAllianceGroup() {
		lock();
		try {
			for (int groupId = 1000; groupId <= 1003; groupId++) {
				PlayerAllianceGroup playerAllianceGroup = groups.get(groupId);
				if (!playerAllianceGroup.isFull()) {
					return playerAllianceGroup;
				}
			}
		}
		finally {
			unlock();
		}
		throw new IllegalStateException("All alliance groups are full.");
	}

	public PlayerAllianceGroup getAllianceGroup(Integer allianceGroupId) {
		PlayerAllianceGroup allianceGroup = groups.get(allianceGroupId);
		Preconditions.checkNotNull(allianceGroup, "No such alliance group " + allianceGroupId);
		return allianceGroup;
	}

	public final Collection<Integer> getViceCaptainIds() {
		return viceCaptainIds;
	}

	public final boolean isViceCaptain(Player player) {
		return viceCaptainIds.contains(player.getObjectId());
	}

	public final boolean isSomeCaptain(Player player) {
		return isLeader(player) || isViceCaptain(player);
	}

	public int getAllianceReadyStatus() {
		return allianceReadyStatus;
	}

	public void setAllianceReadyStatus(int allianceReadyStatus) {
		this.allianceReadyStatus = allianceReadyStatus;
	}

	public final League getLeague() {
		return league;
	}

	public final void setLeague(League league) {
		this.league = league;
	}

	public final boolean isInLeague() {
		return this.league != null;
	}

	public final int groupSize() {
		return groups.size();
	}

	public final Collection<PlayerAllianceGroup> getGroups() {
		return groups.values();
	}

	public TeamType getTeamType() {
		return type;
	}

}
