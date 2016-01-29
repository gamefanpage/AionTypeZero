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

package org.typezero.gameserver.model.team2.common.service;

import com.google.common.base.Preconditions;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamMember;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import org.typezero.gameserver.model.team2.common.events.TeamCommand;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.team2.league.LeagueMember;
import org.typezero.gameserver.model.team2.league.LeagueService;

/**
 * @author ATracer
 */
public class PlayerTeamCommandService {

	public static final void executeCommand(Player player, TeamCommand command, int playerObjId) {
		Player teamSubjective = getTeamSubjective(player, playerObjId);
		// if playerObjId is not 0 - subjective should not be active player
		Preconditions.checkArgument(playerObjId == 0 || teamSubjective.getObjectId().equals(playerObjId) || command == TeamCommand.LEAGUE_EXPEL,
			"Wrong command detected " + command);
		execute(player, command, teamSubjective);
	}

	private static final void execute(Player player, TeamCommand eventCode, Player teamSubjective) {
		switch (eventCode) {
			case GROUP_BAN_MEMBER:
				PlayerGroupService.banPlayer(teamSubjective, player);
				break;
			case GROUP_SET_LEADER:
				PlayerGroupService.changeLeader(teamSubjective);
				break;
			case GROUP_REMOVE_MEMBER:
				PlayerGroupService.removePlayer(teamSubjective);
				break;
			case GROUP_START_MENTORING:
				PlayerGroupService.startMentoring(player);
				break;
			case GROUP_END_MENTORING:
				PlayerGroupService.stopMentoring(player);
				break;
			case ALLIANCE_LEAVE:
				PlayerAllianceService.removePlayer(player);
				break;
			case ALLIANCE_BAN_MEMBER:
				PlayerAllianceService.banPlayer(teamSubjective, player);
				break;
			case ALLIANCE_SET_CAPTAIN:
				PlayerAllianceService.changeLeader(teamSubjective);
				break;
			case ALLIANCE_CHECKREADY_CANCEL:
			case ALLIANCE_CHECKREADY_START:
			case ALLIANCE_CHECKREADY_AUTOCANCEL:
			case ALLIANCE_CHECKREADY_NOTREADY:
			case ALLIANCE_CHECKREADY_READY:
				PlayerAllianceService.checkReady(player, eventCode);
				break;
			case ALLIANCE_SET_VICECAPTAIN:
				PlayerAllianceService.changeViceCaptain(teamSubjective, AssignType.PROMOTE);
				break;
			case ALLIANCE_UNSET_VICECAPTAIN:
				PlayerAllianceService.changeViceCaptain(teamSubjective, AssignType.DEMOTE);
				break;
			case LEAGUE_LEAVE:
				LeagueService.removeAlliance(player.getPlayerAlliance2());
				break;
			case LEAGUE_EXPEL:
				LeagueService.expelAlliance(teamSubjective, player);
				break;
		}
	}

	private static final Player getTeamSubjective(Player player, int playerObjId) {
		if (playerObjId == 0) {
			return player;
		}
		if (player.isInTeam()) {
			TeamMember<Player> member = player.getCurrentTeam().getMember(playerObjId);
			if (member != null) {
				return member.getObject();
			}
			if (player.isInLeague()) {
				LeagueMember subjective = player.getPlayerAlliance2().getLeague().getMember(playerObjId);
				if (subjective != null) {
					return subjective.getObject().getLeaderObject();
				}
			}
		}
		return player;
	}
}
