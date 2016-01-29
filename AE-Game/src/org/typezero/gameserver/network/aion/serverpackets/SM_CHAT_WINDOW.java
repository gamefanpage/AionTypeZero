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

package org.typezero.gameserver.network.aion.serverpackets;

import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author ginho1
 * @edit Cheatkiller
 */
public class SM_CHAT_WINDOW extends AionServerPacket {

	private Player target;
	private boolean isGroup;

	public SM_CHAT_WINDOW(Player target, boolean isGroup) {
		this.target = target;
		this.isGroup = isGroup;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (target == null)
			return;

		if(isGroup) {
			if (target.isInGroup2()) {
				writeC(2); // group
				writeS(target.getName());

				PlayerGroup group = target.getPlayerGroup2();

				writeD(group.getTeamId());
				writeS(group.getLeader().getName());

				Collection<Player> members = group.getMembers();
				for (Player groupMember : members)
					writeC(groupMember.getLevel());

				for (int i = group.size(); i < 6; i++)
					writeC(0);

				for (Player groupMember : members)
					writeC(groupMember.getPlayerClass().getClassId());

				for (int i = group.size(); i < 6; i++)
					writeC(0);
			}
			else if (target.isInAlliance2()) {
				writeC(2); // alliance
				writeS(target.getName());

				PlayerAlliance alliance = target.getPlayerAlliance2();

				writeD(alliance.getTeamId());
				writeS(alliance.getLeader().getName());

				Collection<Player> members = alliance.getMembers();
				for (Player groupMember : members)
					writeC(groupMember.getLevel());

				for (int i = alliance.size(); i < 24; i++)
					writeC(0);

				for (Player groupMember : members)
					writeC(groupMember.getPlayerClass().getClassId());

				for (int i = alliance.size(); i < 24; i++)
					writeC(0);
			}
			else {
				writeC(4); // no group
				writeS(target.getName());
				writeD(0); // no group yet
				writeC(target.getPlayerClass().getClassId());
				writeC(target.getLevel());
				writeC(0); // unk
			}
		}
		else {
			writeC(1);
			writeS(target.getName());
			writeS(target.getLegion() != null ? target.getLegion().getLegionName() : "");
			writeC(target.getLevel());
			writeH(target.getPlayerClass().getClassId());
			writeS(target.getCommonData().getNote());
			writeD(1);
		}
	}
}
