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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.common.legacy.GroupEvent;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.taskmanager.AbstractIterativePeriodicTaskManager;

/**
 * @author Sarynth Supports PlayerGroup and PlayerAlliance movement updating.
 */
public final class TeamEffectUpdater extends AbstractIterativePeriodicTaskManager<Player> {

	private static final class SingletonHolder {

		private static final TeamEffectUpdater INSTANCE = new TeamEffectUpdater();
	}

	public static TeamEffectUpdater getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public TeamEffectUpdater() {
		super(500);
	}

	@Override
	protected void callTask(Player player) {
		if (player.isOnline()) {
			if (player.isInGroup2()) {
				PlayerGroupService.updateGroup(player, GroupEvent.UPDATE);
			}
			if (player.isInAlliance2()) {
				PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.UPDATE);
			}
		}
		// Remove task from list. It will be re-added if player effect changes again.
		this.stopTask(player);
	}

	@Override
	protected String getCalledMethodName() {
		return "teamEffectUpdate()";
	}

}
