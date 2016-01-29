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

import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.team.legion.LegionMemberEx;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.HousingService;

import java.util.List;

/**
 * @author Simple
 */
public class SM_LEGION_MEMBERLIST extends AionServerPacket {

	private static final int OFFLINE = 0x00, ONLINE = 0x01;
	private boolean isFirst, result;
	private List<LegionMemberEx> legionMembers;

	/**
	 * This constructor will handle legion member info when a List of members is given
	 *
	 * @param ArrayList
	 *          <LegionMemberEx> legionMembers
	 */
	public SM_LEGION_MEMBERLIST(List<LegionMemberEx> legionMembers, boolean result , boolean isFirst) {
		this.legionMembers = legionMembers;
		this.isFirst = isFirst;
		this.result = result;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		int size = legionMembers.size();
		writeC(isFirst ? 1 : 0);
		writeH(result ? size : -size);
		for (LegionMemberEx legionMember : legionMembers) {
			writeD(legionMember.getObjectId());
			writeS(legionMember.getName());
			writeC(legionMember.getPlayerClass().getClassId());
			writeD(legionMember.getLevel());
			writeC(legionMember.getRank().getRankId());
			writeD(legionMember.getWorldId());
			writeC(legionMember.isOnline() ? ONLINE : OFFLINE);
			writeS(legionMember.getSelfIntro());
			writeS(legionMember.getNickname());
			writeD(legionMember.getLastOnline());

			int address = HousingService.getInstance().getPlayerAddress(legionMember.getObjectId());
			if (address > 0) {
				House house = HousingService.getInstance().getPlayerStudio(legionMember.getObjectId());
				if (house == null)
					house = HousingService.getInstance().getHouseByAddress(address);
				writeD(address);
				writeD(house.getSettingFlags() >> 8);
			}
			else {
				writeD(0);
				writeD(0);
			}
			writeC(1); // TODO: house settings
			writeC(0);
			writeC(0);
			writeC(0);
		}
	}
}
