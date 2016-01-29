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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.model.gameobjects.player.DeniedStatus;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.team2.league.LeagueService;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.ChatUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;

/**
 * @author Lyahim, ATracer Modified by Simple
 */
public class CM_INVITE_TO_GROUP extends AionClientPacket {

	private String name;
	private int inviteType;

	public CM_INVITE_TO_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		inviteType = readC();
		name = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {

		name = ChatUtil.getRealAdminName(name);
		//Account Premium & VIP
		name = name.replace("\uE0AD", "");
		name = name.replace("\uE0AE", "");
                name = name.replace("\uE0AF", "");
                name = name.replace("\uE0B0", "");
		final String playerName = Util.convertName(name);

		final Player inviter = getConnection().getActivePlayer();
		if (inviter.getLifeStats().isAlreadyDead()) {
			// You cannot issue an invitation while you are dead.
			PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1300163));
			return;
		}

		final Player invited = World.getInstance().findPlayer(playerName);
		if (invited != null) {
			if (invited.getPlayerSettings().isInDeniedStatus(DeniedStatus.GROUP)) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_INVITE_PARTY(invited.getName()));
				return;
			}
			switch (inviteType) {
				case 0:
					PlayerGroupService.inviteToGroup(inviter, invited);
					break;
				case 12: // 2.5
					PlayerAllianceService.inviteToAlliance(inviter, invited);
					break;
				case 28:
					LeagueService.inviteToLeague(inviter, invited);
					break;
				default:
					PacketSendUtility.sendMessage(inviter, "You used an unknown invite type: " + inviteType);
					break;
			}
		}
		else
			inviter.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(name));
	}
}
