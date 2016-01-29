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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.typezero.gameserver.model.AbyssRankingResult;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author zdead, LokiReborn
 */
public class SM_ABYSS_RANKING_LEGIONS extends AionServerPacket {

	private List<AbyssRankingResult> data;
	private Race race;
	private int updateTime;
	private int sendData = 0;

	public SM_ABYSS_RANKING_LEGIONS(int updateTime, ArrayList<AbyssRankingResult> data, Race race) {
		this.updateTime = updateTime;
		this.data = data;
		this.race = race;
		this.sendData = 1;
	}

	public SM_ABYSS_RANKING_LEGIONS(int updateTime, Race race) {
		this.updateTime = updateTime;
		this.data = Collections.emptyList();
		this.race = race;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(race.getRaceId());// 0:Elyos 1:Asmo
		writeD(updateTime);// Date
		writeD(sendData);// 0:Nothing 1:Update Table
		writeD(sendData);// 0:Nothing 1:Update Table
		writeH(data.size());// list size
		for (AbyssRankingResult rs : data) {
			writeD(rs.getRankPos());// Current Rank
			writeD((rs.getOldRankPos() == 0) ? 76 : rs.getOldRankPos());// Old Rank
			writeD(rs.getLegionId());// Legion Id
			writeD(race.getRaceId());// 0:Elyos 1:Asmo
			writeC(rs.getLegionLevel());// Legion Level
			writeD(rs.getLegionMembers());// Legion Members
			writeQ(rs.getLegionCP());// Contribution Points
			writeS(rs.getLegionName(), 82);// Legion Name
		}
	}
}
