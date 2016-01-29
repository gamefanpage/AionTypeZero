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

import java.util.Collections;
import java.util.List;

import org.typezero.gameserver.model.AbyssRankingResult;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Rhys2002, zdead, LokiReborn
 */
public class SM_ABYSS_RANKING_PLAYERS extends AionServerPacket {

	private List<AbyssRankingResult> data;
	private int lastUpdate;
	private int race;
	private int page;
	private boolean isEndPacket;

	public SM_ABYSS_RANKING_PLAYERS(int lastUpdate, List<AbyssRankingResult> data, Race race, int page, boolean isEndPacket) {
		this.lastUpdate = lastUpdate;
		this.data = data;
		this.race = race.getRaceId();
		this.page = page;
		this.isEndPacket = isEndPacket;
	}

	public SM_ABYSS_RANKING_PLAYERS(int lastUpdate, Race race) {
		this.lastUpdate = lastUpdate;
		this.data = Collections.emptyList();
		this.race = race.getRaceId();
		this.page = 0;
		this.isEndPacket = false;
	}

	@Override
	protected void writeImpl(AionConnection con){
		writeD(race);// 0:Elyos 1:Asmo
		writeD(lastUpdate);// Date
		writeD(page);
		writeD(isEndPacket ? 0x7F : 0);// 0:Nothing 1:Update Table
		writeH(data.size());// list size

		for (AbyssRankingResult rs : data) {
			writeD(rs.getRankPos());// Current Rank
			writeD(rs.getPlayerAbyssRank());// Abyss rank
			writeD((rs.getOldRankPos() == 0) ? 501 : rs.getOldRankPos());// Old Rank
			writeD(rs.getPlayerId()); // PlayerID
			writeD(race);
			writeD(rs.getPlayerClass().getClassId());// Class Id
			if (rs.getGender() != null) {
				writeC(rs.getGender().equals(Gender.FEMALE) ? 1 : 0);
 			} else {
 				// fallback if getGender() returns null
				writeC(0);
 			}
			writeC(0);// Unk
			writeC(0);// Unk
			writeC(0);// Unk
			writeQ(rs.getPlayerAP());// Abyss Points
			writeD(rs.getPlayerGP());// Glory Points 4.5.2
			writeH(rs.getPlayerLevel());
			writeS(rs.getPlayerName(), 52);// Player Name
			writeS(rs.getLegionName(), 82);// Legion Name
			writeD(0);//unk 3.5
		}
	}
}
