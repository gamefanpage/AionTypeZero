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

package org.typezero.gameserver.model.team2.league.events;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import org.typezero.gameserver.model.team2.league.League;
import org.typezero.gameserver.model.team2.league.LeagueMember;
import org.typezero.gameserver.model.team2.league.LeagueService;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class LeagueLeftEvent extends AlwaysTrueTeamEvent implements Predicate<LeagueMember> {

	private final League league;
	private final PlayerAlliance alliance;
	private final LeaveReson reason;

	public static enum LeaveReson {
		LEAVE,
		EXPEL,
		DISBAND;
	}

	public LeagueLeftEvent(League league, PlayerAlliance alliance) {
		this(league, alliance, LeaveReson.LEAVE);
	}

	public LeagueLeftEvent(League league, PlayerAlliance alliance, LeaveReson reason) {
		this.league = league;
		this.alliance = alliance;
		this.reason = reason;
	}

	@Override
	public void handleEvent() {
		league.removeMember(alliance.getTeamId());
		league.apply(this);

		switch (reason) {
			case LEAVE:
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance));
				checkDisband();
				break;
			case EXPEL:
				// TODO getLeaderName in team2
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.LEAGUE_EXPELLED, league.getLeaderObject()
					.getLeader().getName()));
				checkDisband();
				break;
			case DISBAND:
				alliance.sendPacket(new SM_ALLIANCE_INFO(alliance));
				break;
		}
	}

	private final void checkDisband() {
		if (league.onlineMembers() <= 1) {
			LeagueService.disband(league);
		}
	}

	@Override
	public boolean apply(LeagueMember member) {

		PlayerAlliance leagueAlliance = member.getObject();
		leagueAlliance.applyOnMembers(new Predicate<Player>() {

			@Override
			public boolean apply(Player member) {
				switch (reason) {
					case LEAVE:
						PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.LEAGUE_LEFT, alliance
							.getLeader().getName()));
						break;
					case EXPEL:
						//TODO may be EXPEL message only to leader
						PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.LEAGUE_EXPEL, alliance
							.getLeader().getName()));
						break;
				}
				return true;
			}

		});

		return true;
	}
}
