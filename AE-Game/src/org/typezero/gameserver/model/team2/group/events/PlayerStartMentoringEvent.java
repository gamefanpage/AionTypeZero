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
import org.typezero.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import org.typezero.gameserver.model.team2.common.legacy.GroupEvent;
import org.typezero.gameserver.model.team2.group.PlayerFilters.MentorSuiteFilter;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class PlayerStartMentoringEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

	private final PlayerGroup group;
	private final Player player;

	public PlayerStartMentoringEvent(PlayerGroup group, Player player) {
		this.group = group;
		this.player = player;
	}

	@Override
	public void handleEvent() {
		if (group.filterMembers(new MentorSuiteFilter(player)).size() == 0) {
			AuditLogger.info(player, "Send fake start mentoring packet");
			return;
		}
		player.setMentor(true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START);
		group.applyOnMembers(this);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(2, player));
	}

	@Override
	public boolean apply(Player member) {
		if (!player.equals(member)) {
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START_PARTYMSG(player.getName()));
		}
		PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.MOVEMENT));
		return true;
	}
}
