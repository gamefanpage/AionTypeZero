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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.team2.TeamType;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import com.google.common.base.Preconditions;

/**
 * @author ATracer
 */
public class PlayerAllianceInvite extends RequestResponseHandler {

	private final Player inviter;
	private final Player invited;

	public PlayerAllianceInvite(Player inviter, Player invited) {
		super(inviter);
		this.inviter = inviter;
		this.invited = invited;
	}

	@Override
	public void acceptRequest(Creature requester, Player responder) {
		if (PlayerAllianceService.canInvite(inviter, invited)) {

			PlayerAlliance alliance = inviter.getPlayerAlliance2();

			if (alliance != null) {
				if (alliance.size() == 24) {
					PacketSendUtility.sendMessage(invited, "That alliance is already full.");
					PacketSendUtility.sendMessage(inviter, "Your alliance is already full.");
					return;
				}
				else if (invited.isInGroup2() && invited.getPlayerGroup2().size() + alliance.size() > 24) {
					PacketSendUtility.sendMessage(invited, "That alliance is now too full for your group to join.");
					PacketSendUtility.sendMessage(inviter, "Your alliance is now too full for that group to join.");
					return;
				}
			}

			List<Player> playersToAdd = new ArrayList<Player>();
			collectPlayersToAdd(playersToAdd, alliance);

			if (alliance == null) {
				alliance = PlayerAllianceService.createAlliance(inviter, invited, TeamType.ALLIANCE);
			}

			for (Player member : playersToAdd) {
				PlayerAllianceService.addPlayer(alliance, member);
			}
		}
	}

	private final void collectPlayersToAdd(List<Player> playersToAdd, PlayerAlliance alliance) {
		// Collect Inviter Group without leader
		if (inviter.isInGroup2()) {
			Preconditions.checkState(alliance == null, "If inviter is in group - alliance should be null");
			PlayerGroup group = inviter.getPlayerGroup2();
			playersToAdd.addAll(group.filterMembers(new ExcludePlayerFilter(inviter)));

			Iterator<Player> pIter = group.getMembers().iterator();
			while (pIter.hasNext()) {
				PlayerGroupService.removePlayer(pIter.next());
			}
		}

		// Collect full Invited Group
		if (invited.isInGroup2()) {
			PlayerGroup group = invited.getPlayerGroup2();
			playersToAdd.addAll(group.getMembers());
			Iterator<Player> pIter = group.getMembers().iterator();
			while (pIter.hasNext()) {
				PlayerGroupService.removePlayer(pIter.next());
			}
		}
		// or just single player
		else {
			playersToAdd.add(invited);
		}
	}

	@Override
	public void denyRequest(Creature requester, Player responder) {
		PacketSendUtility.sendPacket(inviter,
				SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_REJECT_INVITATION(responder.getName()));
	}

}
