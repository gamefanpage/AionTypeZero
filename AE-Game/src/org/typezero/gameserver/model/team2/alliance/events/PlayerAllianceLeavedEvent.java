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

package org.typezero.gameserver.model.team2.alliance.events;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceMember;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.common.events.PlayerLeavedEvent;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author ATracer
 */
public class PlayerAllianceLeavedEvent extends PlayerLeavedEvent<PlayerAllianceMember, PlayerAlliance> {

	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player) {
		super(alliance, player);
	}

	public PlayerAllianceLeavedEvent(PlayerAlliance team, Player player, PlayerLeavedEvent.LeaveReson reason,
		String banPersonName) {
		super(team, player, reason, banPersonName);
	}

	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player, PlayerLeavedEvent.LeaveReson reason) {
		super(alliance, player, reason);
	}

	@Override
	public void handleEvent() {
		team.removeMember(leavedPlayer.getObjectId());
		team.getViceCaptainIds().remove(leavedPlayer.getObjectId());

		if (leavedPlayer.isOnline()) {
			PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		}

		team.apply(this);

		switch (reason) {
			case BAN:
			case LEAVE:
			case LEAVE_TIMEOUT:
				if (team.onlineMembers() <= 1) {
					PlayerAllianceService.disband(team);
				}
				else {
					if (leavedPlayer.equals(team.getLeader().getObject())) {
						team.onEvent(new ChangeAllianceLeaderEvent(team));
					}
				}
				if (reason == LeaveReson.BAN) {
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_ME(banPersonName));
				}

				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
				break;
		}

		if (leavedPlayer.isInInstance()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!leavedPlayer.isInAlliance2()) {
						WorldMapInstance instance = leavedPlayer.getPosition().getWorldMapInstance();
						if (instance.getRegistredAlliance() != null || instance.getRegistredLeague() != null) {
							InstanceService.moveToExitPoint(leavedPlayer);
						}
					}
				}

			}, 10000);
		}
	}

	@Override
	public boolean apply(PlayerAllianceMember member) {
		Player player = member.getObject();

		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(leavedTeamMember, PlayerAllianceEvent.LEAVE));
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_INFO(team));

		switch (reason) {
			case LEAVE_TIMEOUT:
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case LEAVE:
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
				break;
			case BAN:
				PacketSendUtility
					.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_HIM(banPersonName, leavedPlayer.getName()));
				break;
		}

		return true;
	}

}
