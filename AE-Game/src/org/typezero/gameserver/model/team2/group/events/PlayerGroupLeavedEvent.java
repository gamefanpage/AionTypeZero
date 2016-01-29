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

package org.typezero.gameserver.model.team2.group.events;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.common.events.PlayerLeavedEvent;
import org.typezero.gameserver.model.team2.common.legacy.GroupEvent;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupMember;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class PlayerGroupLeavedEvent extends PlayerLeavedEvent<PlayerGroupMember, PlayerGroup> {

	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player) {
		super(alliance, player);
	}

	public PlayerGroupLeavedEvent(PlayerGroup team, Player player, PlayerLeavedEvent.LeaveReson reason,
		String banPersonName) {
		super(team, player, reason, banPersonName);
	}

	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player, PlayerLeavedEvent.LeaveReson reason) {
		super(alliance, player, reason);
	}

	@Override
	public void handleEvent() {
		team.removeMember(leavedPlayer.getObjectId());

		if (leavedPlayer.isMentor()) {
			team.onEvent(new PlayerGroupStopMentoringEvent(team, leavedPlayer));
		}

		team.apply(this);

		PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		switch (reason) {
			case BAN:
			case LEAVE:
				// PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_SECEDE); // client side?
				if (team.onlineMembers() <= 1) {
					PlayerGroupService.disband(team);
				}
				else {
					if (leavedPlayer.equals(team.getLeader().getObject())) {
						team.onEvent(new ChangeGroupLeaderEvent(team));
					}
				}
				if (reason == LeaveReson.BAN) {
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_YOU_ARE_BANISHED);
				}
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_IS_DISPERSED);
				break;
		default:
			break;
		}

		if (leavedPlayer.isInInstance()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!leavedPlayer.isInGroup2()) {
						if (leavedPlayer.getPosition().getWorldMapInstance().getRegisteredGroup() != null)
							InstanceService.moveToExitPoint(leavedPlayer);
					}
				}
			}, 10000);
		}
	}

	@Override
	public boolean apply(PlayerGroupMember member) {
		Player player = member.getObject();
		PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(team, leavedPlayer, GroupEvent.LEAVE));

		switch (reason) {
			case LEAVE:
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_LEAVE_PARTY(leavedPlayer.getName()));
				break;
			case BAN:
				// TODO find out empty strings (Retail has +2 empty strings
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_IS_BANISHED(leavedPlayer.getName()));
				break;
		default:
			break;
		}

		return true;
	}

}
