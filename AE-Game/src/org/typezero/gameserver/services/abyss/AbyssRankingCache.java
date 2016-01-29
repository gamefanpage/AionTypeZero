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

package org.typezero.gameserver.services.abyss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.RankingConfig;
import org.typezero.gameserver.dao.AbyssRankDAO;
import org.typezero.gameserver.model.AbyssRankingResult;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_LEGIONS;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_PLAYERS;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author VladimirZ
 */
public class AbyssRankingCache {

	private static final Logger log = LoggerFactory.getLogger(AbyssRankingCache.class);
	private int lastUpdate;
	private final FastMap<Race, List<SM_ABYSS_RANKING_PLAYERS>> players = new FastMap<Race, List<SM_ABYSS_RANKING_PLAYERS>>();
	private final FastMap<Race, SM_ABYSS_RANKING_LEGIONS> legions = new FastMap<Race, SM_ABYSS_RANKING_LEGIONS>();

	public void reloadRankings() {
		log.info("Updating abyss ranking cache");
		this.lastUpdate = (int) (System.currentTimeMillis() / 1000);
		getDAO().updateRankList(RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);

		renewPlayerRanking(Race.ASMODIANS);
		renewPlayerRanking(Race.ELYOS);

		renewLegionRanking();

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.resetAbyssRankListUpdated();
			}
		});
	}

	/**
	 * Renews the legion's rank and SM_ABYSS_RANKING_LEGIONS
	 */
	private void renewLegionRanking() {
		Map<Integer, Integer> newLegionRankingCache = new HashMap<Integer, Integer>();
		ArrayList<AbyssRankingResult> elyosRanking = getDAO().getAbyssRankingLegions(Race.ELYOS), asmoRanking = getDAO().getAbyssRankingLegions(Race.ASMODIANS);

		legions.clear();
		legions.put(Race.ASMODIANS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, asmoRanking, Race.ASMODIANS));
		legions.put(Race.ELYOS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, elyosRanking, Race.ELYOS));

		for (AbyssRankingResult result : elyosRanking) {
			newLegionRankingCache.put(result.getLegionId(), result.getRankPos());
		}
		for (AbyssRankingResult result : asmoRanking) {
			newLegionRankingCache.put(result.getLegionId(), result.getRankPos());
		}

		LegionService.getInstance().performRankingUpdate(newLegionRankingCache);
	}

	/**
	 * Renews the player ranking by race
	 *
	 * @param race
	 */
	private void renewPlayerRanking(Race race) {
		//delete not before new list is created
		List<SM_ABYSS_RANKING_PLAYERS> newlyCalculated;
		newlyCalculated = generatePacketsForRace(race);
		players.remove(race);
		players.put(race, newlyCalculated);
	}

    private List<SM_ABYSS_RANKING_PLAYERS> generatePacketsForRace(Race race)
    {
        ArrayList<AbyssRankingResult> list = getDAO().getAbyssRankingPlayers(race, AbyssRankEnum.STAR1_OFFICER.getGPRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
        int page = 1;
        List<SM_ABYSS_RANKING_PLAYERS> playerPackets = new ArrayList();
        for (int i = 0; i < list.size(); i += 44)
        {
            if (list.size() > i + 44) {
                playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(this.lastUpdate, list.subList(i, i + 44), race, page, false));
            } else {
                playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(this.lastUpdate, list.subList(i, list.size()), race, page, true));
            }
            page++;
        }
        return playerPackets;
    }

	/**
	 * @return all players
	 */
	public List<SM_ABYSS_RANKING_PLAYERS> getPlayers(Race race) {
		return players.get(race);
	}

	/**
	 * @return all legions
	 */
	public SM_ABYSS_RANKING_LEGIONS getLegions(Race race) {
		return legions.get(race);
	}

	/**
	 * @return the lastUpdate
	 */
	public int getLastUpdate() {
		return lastUpdate;
	}

	private AbyssRankDAO getDAO() {
		return DAOManager.getDAO(AbyssRankDAO.class);
	}

	public static final AbyssRankingCache getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {

		protected static final AbyssRankingCache INSTANCE = new AbyssRankingCache();
	}
}
