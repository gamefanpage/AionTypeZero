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

package org.typezero.gameserver.model.team2.common.events;

import gnu.trove.map.hash.TIntObjectHashMap;

import com.google.common.base.Preconditions;

/**
 * @author ATracer
 */
public enum TeamCommand {

	GROUP_BAN_MEMBER(2),
	GROUP_SET_LEADER(3),
	GROUP_REMOVE_MEMBER(6),
	GROUP_SET_LFG(9), // TODO confirm
	GROUP_START_MENTORING(10),
	GROUP_END_MENTORING(11),
	ALLIANCE_LEAVE(14),
	ALLIANCE_BAN_MEMBER(16),
	ALLIANCE_SET_CAPTAIN(17),
	ALLIANCE_CHECKREADY_CANCEL(20),
	ALLIANCE_CHECKREADY_START(21),
	ALLIANCE_CHECKREADY_AUTOCANCEL(22),
	ALLIANCE_CHECKREADY_READY(23),
	ALLIANCE_CHECKREADY_NOTREADY(24),
	ALLIANCE_SET_VICECAPTAIN(25),
	ALLIANCE_UNSET_VICECAPTAIN(26),
	ALLIANCE_CHANGE_GROUP(27),
	LEAGUE_LEAVE(29),
	LEAGUE_EXPEL(30);

	private static TIntObjectHashMap<TeamCommand> teamCommands;

	static {
		teamCommands = new TIntObjectHashMap<TeamCommand>();
		for (TeamCommand eventCode : values()) {
			teamCommands.put(eventCode.getCodeId(), eventCode);
		}
	}

	private final int commandCode;

	private TeamCommand(int commandCode) {
		this.commandCode = commandCode;
	}

	public int getCodeId() {
		return commandCode;
	}

	public static final TeamCommand getCommand(int commandCode) {
		TeamCommand command = teamCommands.get(commandCode);
		Preconditions.checkNotNull(command, "Invalid team command code " + commandCode);
		return command;
	}

}
