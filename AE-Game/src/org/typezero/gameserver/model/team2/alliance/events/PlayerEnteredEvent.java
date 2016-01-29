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

import com.google.common.base.Predicate;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamEvent;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceMember;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PlayerEnteredEvent implements Predicate<PlayerAllianceMember>, TeamEvent {

	private final PlayerAlliance alliance;
	private final Player invited;
	private PlayerAllianceMember invitedMember;

	public PlayerEnteredEvent(PlayerAlliance alliance, Player player) {
		this.alliance = alliance;
		this.invited = player;
	}

	/**
	 * Entered player should not be in group yet
	 */
	@Override
	public boolean checkCondition() {
		return !alliance.hasMember(invited.getObjectId());
	}

	@Override
	public void handleEvent() {
		PlayerAllianceService.addPlayerToAlliance(alliance, invited);

		invitedMember = alliance.getMember(invited.getObjectId());

		PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_INFO(alliance));
		PacketSendUtility.sendPacket(invited, new SM_SHOW_BRAND(0, 0));
		PacketSendUtility.sendPacket(invited, SM_SYSTEM_MESSAGE.STR_FORCE_ENTERED_FORCE);
		PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_MEMBER_INFO(invitedMember, PlayerAllianceEvent.JOIN));

		alliance.apply(this);
	}

	@Override
	public boolean apply(PlayerAllianceMember member) {
		Player player = member.getObject();
		if (!invited.getObjectId().equals(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(invitedMember, PlayerAllianceEvent.JOIN));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(invited, false));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FORCE_HE_ENTERED_FORCE(invited.getName()));

			PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_MEMBER_INFO(member, PlayerAllianceEvent.ENTER));
		}
		return true;
	}

}
