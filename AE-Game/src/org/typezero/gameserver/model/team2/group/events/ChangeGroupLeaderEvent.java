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

import com.google.common.base.Predicate;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.common.events.ChangeLeaderEvent;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ChangeGroupLeaderEvent extends ChangeLeaderEvent<PlayerGroup> {

	public ChangeGroupLeaderEvent(PlayerGroup team, Player eventPlayer) {
		super(team, eventPlayer);
	}

	public ChangeGroupLeaderEvent(PlayerGroup team) {
		super(team, null);
	}

	@Override
	public void handleEvent() {
		Player oldLeader = team.getLeaderObject();
		if (eventPlayer == null) {
			team.applyOnMembers(this);
		}
		else {
			changeLeaderTo(eventPlayer);
		}
		checkLeaderChanged(oldLeader);
	}

	@Override
	protected void changeLeaderTo(final Player player) {
		team.changeLeader(team.getMember(player.getObjectId()));
		team.applyOnMembers(new Predicate<Player>() {

			@Override
			public boolean apply(Player member) {
				PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(team));
				if (!player.equals(member)) {
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_HE_IS_NEW_LEADER(player.getName()));
				}
				else {
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_YOU_BECOME_NEW_LEADER);
				}
				return true;
			}

		});
	}

}
