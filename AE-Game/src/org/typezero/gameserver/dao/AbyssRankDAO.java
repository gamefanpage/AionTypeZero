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

package org.typezero.gameserver.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import org.typezero.gameserver.model.AbyssRankingResult;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.abyss.AGPoint;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer
 */
public abstract class AbyssRankDAO implements DAO {

	@Override
	public final String getClassName() {
		return AbyssRankDAO.class.getName();
	}

    public abstract List<Integer> RankPlayers(final int rank);

    public abstract void updataGloryPoint(final int playerId, final int gp);

	public abstract void loadAbyssRank(Player player);

	public abstract AbyssRank loadAbyssRank(int playerId);

	public abstract boolean storeAbyssRank(Player player);

	public abstract ArrayList<AbyssRankingResult> getAbyssRankingPlayers(final Race race, final int lowerApLimit, final int maxOfflineDays);

	public abstract ArrayList<AbyssRankingResult> getAbyssRankingLegions(Race race);

    public abstract void loadPlayersAp(final Race race, final int lowerApLimit, final int maxOfflineDays, final Map<Integer, AGPoint> results);

    public abstract void loadPlayersGp(final Race race, final int lowerApLimit, final int maxOfflineDays, final Map<Integer, AGPoint> results); // 4.5.2

	public abstract void updateAbyssRank(int playerId, AbyssRankEnum rankEnum);

	public abstract void updateRankList(final int maxOfflineDays);
}
