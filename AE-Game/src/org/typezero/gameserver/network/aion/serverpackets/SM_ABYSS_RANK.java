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


import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author Nemiroff
 */
public class SM_ABYSS_RANK extends AionServerPacket {

	private AbyssRank rank;
	private int currentRankId;

	public SM_ABYSS_RANK(AbyssRank rank) {
		this.rank = rank;
		this.currentRankId = rank.getRank().getId();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeQ(rank.getAp()); // curAP
		writeD(rank.getGp()); // curGP 4.5.2
		writeD(currentRankId); // curRank
		writeD(rank.getTopRanking()); // curRating

		int nextRankId = currentRankId < AbyssRankEnum.values().length ? currentRankId + 1 : currentRankId;
		if (currentRankId >= 1 && currentRankId < 9)
			writeD(100 * rank.getAp() / AbyssRankEnum.getRankById(nextRankId).getAPRequired()); // exp %
		else if (currentRankId >= 9)
			writeD(100 * rank.getGp() / AbyssRankEnum.getRankById(nextRankId).getGPRequired()); // exp %

		writeD(rank.getAllKill()); // allKill
		writeD(rank.getMaxRank()); // maxRank

		writeD(rank.getDailyKill()); // dayKill
		writeQ(rank.getDailyAP()); // dayAP
		writeD(rank.getDailyGP()); // dayGP 4.5

		writeD(rank.getWeeklyKill()); // weekKill
		writeQ(rank.getWeeklyAP()); // weekAP
		writeD(rank.getWeeklyGP()); // weekGP 4.5

		writeD(rank.getLastKill()); // laterKill
		writeQ(rank.getLastAP()); // laterAP
		writeD(rank.getLastGP()); // laterGP 4.5

		writeC(0x00); // unk
	}
}
