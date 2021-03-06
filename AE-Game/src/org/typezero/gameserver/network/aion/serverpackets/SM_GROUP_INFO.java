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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamType;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.apache.commons.lang.StringUtils;

/**
 * @author Lyahim, ATracer, xTz
 */
public class SM_GROUP_INFO extends AionServerPacket {
	private LootGroupRules lootRules;
	private int groupId;
	private int leaderId;
	private TeamType type;

	public SM_GROUP_INFO(PlayerGroup group) {
		groupId = group.getObjectId();
		leaderId = group.getLeader().getObjectId();
		lootRules = group.getLootGroupRules();
		type = group.getTeamType();

	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		writeD(groupId);
		writeD(leaderId);
		writeD(player.getWorldId());//mapId
		writeD(lootRules.getLootRule().getId());
		writeD(lootRules.getMisc());
		writeD(lootRules.getCommonItemAbove());
		writeD(lootRules.getSuperiorItemAbove());
		writeD(lootRules.getHeroicItemAbove());
		writeD(lootRules.getFabledItemAbove());
		writeD(lootRules.getEthernalItemAbove());
		writeD(lootRules.getAutodistribution().getId());
		writeD(0x02);
		writeC(0x00);
		writeD(type.getType());
		writeD(type.getSubType());
		writeD(0x00); // message id
		writeS(StringUtils.EMPTY); // name
	}
}
