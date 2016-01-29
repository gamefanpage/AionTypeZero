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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.model.team2.group.events.ChangeGroupLeaderEvent;

/**
 * @author ATracer
 */
public abstract class ChangeLeaderEvent<T extends TemporaryPlayerTeam<?>> extends AbstractTeamPlayerEvent<T> {

	private static final Logger log = LoggerFactory.getLogger(ChangeGroupLeaderEvent.class);

	public ChangeLeaderEvent(T team, Player eventPlayer) {
		super(team, eventPlayer);
	}

	/**
	 * New leader either is null or should be online
	 */
	@Override
	public boolean checkCondition() {
		return eventPlayer == null || eventPlayer.isOnline();
	}

	@Override
	public boolean apply(Player player) {
		if (!player.getObjectId().equals(team.getLeader().getObjectId()) && player.isOnline()) {
			changeLeaderTo(player);
			return false;
		}
		return true;
	}

	/**
	 * @param oldLeader
	 */
	protected void checkLeaderChanged(Player oldLeader) {
		if (team.isLeader(oldLeader)) {
			log.info("TEAM2: leader is not changed, total: {}, online: {}", team.size(), team.onlineMembers());
		}
	}

	protected abstract void changeLeaderTo(Player player);

}
