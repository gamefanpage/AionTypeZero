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

import org.typezero.gameserver.model.team2.TeamEvent;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.league.League;
import org.typezero.gameserver.model.team2.league.LeagueMember;
import org.typezero.gameserver.model.team2.league.LeagueService;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class LeagueEnteredEvent implements Predicate<LeagueMember>, TeamEvent {

	private final League league;
	private final PlayerAlliance invitedAlliance;

	public LeagueEnteredEvent(League league, PlayerAlliance alliance) {
		this.league = league;
		this.invitedAlliance = alliance;
	}

	/**
	 * Entered alliance should not be in league yet
	 */
	@Override
	public boolean checkCondition() {
		return !league.hasMember(invitedAlliance.getObjectId());
	}

	@Override
	public void handleEvent() {
		LeagueService.addAllianceToLeague(league, invitedAlliance);
		league.apply(this);
	}

	@Override
	public boolean apply(LeagueMember member) {
		PlayerAlliance alliance = member.getObject();
		alliance.sendPacket(new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.LEAGUE_ENTERED, league.getLeaderObject()
			.getLeader().getName()));
		alliance.sendPacket(new SM_SHOW_BRAND(0, 0));
		return true;
	}

}
