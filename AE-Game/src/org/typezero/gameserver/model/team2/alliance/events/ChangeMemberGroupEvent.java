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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceGroup;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceMember;
import org.typezero.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ChangeMemberGroupEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember> {

	private final PlayerAlliance alliance;
	private final int firstMemberId;
	private final int secondMemberId;
	private final int allianceGroupId;

	private PlayerAllianceMember firstMember;
	private PlayerAllianceMember secondMember;

	public ChangeMemberGroupEvent(PlayerAlliance alliance, int firstMemberId, int secondMemberId, int allianceGroupId) {
		this.alliance = alliance;
		this.firstMemberId = firstMemberId;
		this.secondMemberId = secondMemberId;
		this.allianceGroupId = allianceGroupId;
	}

	@Override
	public void handleEvent() {
		firstMember = alliance.getMember(firstMemberId);
		secondMember = alliance.getMember(secondMemberId);
		Preconditions.checkNotNull(firstMember, "First member should not be null");
		Preconditions.checkArgument(secondMemberId == 0 || secondMember != null, "Second member should not be null");
		if (secondMember != null) {
			swapMembersInGroup(firstMember, secondMember);
		}
		else {
			moveMemberToGroup(firstMember, allianceGroupId);
		}
		alliance.apply(this);
	}

	@Override
	public boolean apply(PlayerAllianceMember member) {
		PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(firstMember,
			PlayerAllianceEvent.MEMBER_GROUP_CHANGE));
		if (secondMember != null) {
			PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(secondMember,
				PlayerAllianceEvent.MEMBER_GROUP_CHANGE));
		}
		return true;
	}

	private void swapMembersInGroup(PlayerAllianceMember firstMember, PlayerAllianceMember secondMember) {
		PlayerAllianceGroup firstAllianceGroup = firstMember.getPlayerAllianceGroup();
		PlayerAllianceGroup secondAllianceGroup = secondMember.getPlayerAllianceGroup();
		firstAllianceGroup.removeMember(firstMember);
		secondAllianceGroup.removeMember(secondMember);
		firstAllianceGroup.addMember(secondMember);
		secondAllianceGroup.addMember(firstMember);
	}

	private void moveMemberToGroup(PlayerAllianceMember firstMember, int allianceGroupId) {
		PlayerAllianceGroup firstAllianceGroup = firstMember.getPlayerAllianceGroup();
		firstAllianceGroup.removeMember(firstMember);
		PlayerAllianceGroup newAllianceGroup = alliance.getAllianceGroup(allianceGroupId);
		newAllianceGroup.addMember(firstMember);
	}
}
