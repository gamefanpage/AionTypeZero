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
import org.typezero.gameserver.model.team2.common.events.AbstractTeamPlayerEvent;
import org.typezero.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class AssignViceCaptainEvent extends AbstractTeamPlayerEvent<PlayerAlliance> {

	public static enum AssignType {
		PROMOTE,
		DEMOTE_CAPTAIN_TO_VICECAPTAIN,
		DEMOTE
	}

	private final AssignType assignType;

	public AssignViceCaptainEvent(PlayerAlliance team, Player eventPlayer, AssignType assignType) {
		super(team, eventPlayer);
		this.assignType = assignType;
	}

	@Override
	public boolean checkCondition() {
		return eventPlayer != null && eventPlayer.isOnline();
	}

	@Override
	public void handleEvent() {
		switch (assignType) {
			case DEMOTE:
				team.getViceCaptainIds().remove(eventPlayer.getObjectId());
				break;
			case PROMOTE:
				if (team.getViceCaptainIds().size() == 4) {
					PacketSendUtility.sendPacket(team.getLeaderObject(), SM_SYSTEM_MESSAGE.STR_FORCE_CANNOT_PROMOTE_MANAGER);
					return;
				}
				team.getViceCaptainIds().add(eventPlayer.getObjectId());
				break;
			case DEMOTE_CAPTAIN_TO_VICECAPTAIN:
				team.getViceCaptainIds().add(eventPlayer.getObjectId());
				break;
		}

		team.applyOnMembers(this);
	}

	@Override
	public boolean apply(Player player) {
		int messageId = 0;
		switch (assignType) {
			case PROMOTE:
				messageId = SM_ALLIANCE_INFO.VICECAPTAIN_PROMOTE;
				break;
			case DEMOTE:
				messageId = SM_ALLIANCE_INFO.VICECAPTAIN_DEMOTE;
				break;
		}
		// TODO check whether same is sent to eventPlayer
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_INFO(team, messageId, eventPlayer.getName()));
		return true;
	}

}
