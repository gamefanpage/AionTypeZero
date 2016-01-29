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

package org.typezero.gameserver.model.autogroup;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamType;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.templates.portal.PortalLoc;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.services.teleport.TeleportService2;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

/**
 *
 * @author xTz
 */
public class AutoGeneralInstance extends AutoInstance {

	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		super.writeLock();
		try {
			if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize())) {
				return AGQuestion.FAILED;
			}
			PlayerClass playerClass = player.getPlayerClass();
			int clericSize = getPlayersByClass(PlayerClass.CLERIC).size();
			int templarSize = getPlayersByClass(PlayerClass.TEMPLAR).size();
			if (playerClass.equals(PlayerClass.CLERIC)) {
				if (clericSize > 0) {
					return AGQuestion.FAILED;
				}
			}
			else if (playerClass.equals(PlayerClass.TEMPLAR)) {
				if (templarSize > 0) {
					return AGQuestion.FAILED;
				}
			}
			else {
				int size = players.size();
				size -= clericSize;
				size -= templarSize;
				if (size >= 4) {
					return AGQuestion.FAILED;
				}
			}
			players.put(player.getObjectId(), new AGPlayer(player));
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		finally {
			super.writeUnlock();
		}
	}

	@Override
	public void onEnterInstance(Player player) {
		super.onEnterInstance(player);
		List<Player> playersByRace = instance.getPlayersInside();
		if (playersByRace.size() == 1 && !playersByRace.get(0).isInGroup2()) {
			PlayerGroup newGroup = PlayerGroupService.createGroup(playersByRace.get(0), player, TeamType.AUTO_GROUP);
			int groupId = newGroup.getObjectId();
			if (!instance.isRegistered(groupId)) {
				instance.register(groupId);
			}
		}
		else if (!playersByRace.isEmpty() && playersByRace.get(0).isInGroup2()) {
			PlayerGroupService.addPlayer(playersByRace.get(0).getPlayerGroup2(), player);
		}
		Integer object = player.getObjectId();
		if (!instance.isRegistered(object)) {
			instance.register(object);
		}
	}

	@Override
	public void onPressEnter(Player player) {
		super.onPressEnter(player);
		int worldId = instance.getMapId();
		PortalPath portal = DataManager.PORTAL2_DATA.getPortalDialog(worldId, 10000, player.getRace());
		if (portal == null) {
			return;
		}
		PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portal.getLocId());
		if (loc == null) {
			return;
		}
		TeleportService2.teleportTo(player, worldId, instance.getInstanceId(), loc.getX(), loc.getY(), loc.getZ(), loc.getH());
		long instanceCoolTime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId);
			player.getPortalCooldownList().addPortalCooldown(worldId, instanceCoolTime);
	}

	@Override
	public void onLeaveInstance(Player player) {
		super.unregister(player);
		PlayerGroupService.removePlayer(player);
	}

	private List<AGPlayer> getPlayersByClass(PlayerClass playerClass) {
		return select(players, having(on(AGPlayer.class).getPlayerClass(), equalTo(playerClass)));
	}
}
