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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import org.typezero.gameserver.configs.main.RankingConfig;
import org.typezero.gameserver.dao.AbyssRankDAO;
import org.typezero.gameserver.dao.ServerVariablesDAO;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author ATracer
 */
public class AbyssRankUpdateService {

    private static final String GP_UPDATA_TIME = "0 10 5 ? * *";
    private Map<Integer, AGPoint> playerAGpMaps = new HashMap();
    private List<Map.Entry<Integer, AGPoint>> playerAGPEntries;

	private static final Logger log = LoggerFactory.getLogger(AbyssRankUpdateService.class);
    private static final Logger debuglog = LoggerFactory.getLogger("ABYSSRANK_LOG");

	private AbyssRankUpdateService() {
	}

	public static AbyssRankUpdateService getInstance() {
		return SingletonHolder.instance;
	}

    public void GpointUpdata()
    {
        CronService.getInstance().schedule(new Runnable()
        {
            public void run()
            {
                AbyssRankUpdateService.this.loadGpRank();
            }
        }, GP_UPDATA_TIME);
    }

    private void loadGpRank()
    {
        List<Integer> rankPlayers = DAOManager.getDAO(AbyssRankDAO.class).RankPlayers(9);
        delGpoint(rankPlayers);
    }

    private void delGpoint(List<Integer> rankPlayers)
    {
        for (int playerId : rankPlayers)
        {
            AbyssRank rank = DAOManager.getDAO(AbyssRankDAO.class).loadAbyssRank(playerId);
            Player player = World.getInstance().findPlayer(playerId);
            int lostGp = rank.getRank().getDelGp();
            // списываем только с офицеров и выше
            if (rank.getRank().getId() < AbyssRankEnum.STAR1_OFFICER.getId())
                continue;
            if (player != null)
            {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082, new Object[0]));
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_LOSE_PERSONAL(AbyssRankEnum.getRankDescriptionId(player), rank.getRank().getDelGp()));
                AbyssPointsService.addAGp(player, 0, lostGp * -1);

            }
            else
            {
                int newGP = rank.getGp() - lostGp;
                if (newGP < 0)
                    newGP = 0;
                debuglog.info("[GP REWARD LOG] Scheduled delete. Player: " + playerId + ". Last: " + rank.getGp() +". New: "+ newGP);
                DAOManager.getDAO(AbyssRankDAO.class).updataGloryPoint(playerId, newGP);
            }
        }
    }

	public void scheduleUpdate() {
		ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		int nextTime = dao.load("abyssRankUpdate");
		if (nextTime < System.currentTimeMillis()/1000){
			performUpdate();
		}

		log.info("Starting ranking update task based on cron expression: " + RankingConfig.TOP_RANKING_UPDATE_RULE);
		CronService.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				performUpdate();
			}
		}, RankingConfig.TOP_RANKING_UPDATE_RULE, true);
	}

	/**
	 * Perform update of all ranks
	 */
	public void performUpdate() {
		log.info("AbyssRankUpdateService: executing rank update");
		long startTime = System.currentTimeMillis();

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				player.getAbyssRank().doUpdate(); // 4.5.2
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
			}
		});

		updateLimitedRanks(); // 4.5.2
		AbyssRankingCache.getInstance().reloadRankings();
		log.info("AbyssRankUpdateService: execution time: " + (System.currentTimeMillis() - startTime) / 1000);
	}

	/**
	 * Update player ranks based on quota for all players (online/offline)
	 */
	private void updateLimitedRanks() {
        DAOManager.getDAO(AbyssRankDAO.class).loadPlayersGp(Race.ASMODIANS, AbyssRankEnum.STAR1_OFFICER.getGPRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS, this.playerAGpMaps);
        DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(Race.ASMODIANS, AbyssRankEnum.GRADE8_SOLDIER.getAPRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS, this.playerAGpMaps);
        updateAllRanksForRace();
        DAOManager.getDAO(AbyssRankDAO.class).loadPlayersGp(Race.ELYOS, AbyssRankEnum.STAR1_OFFICER.getGPRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS, this.playerAGpMaps);
        DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(Race.ELYOS, AbyssRankEnum.GRADE8_SOLDIER.getAPRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS, this.playerAGpMaps);
        updateAllRanksForRace();
	}

    private void updateAllRanksForRace()
    {
        this.playerAGPEntries = new ArrayList(this.playerAGpMaps.entrySet());
        Collections.sort(this.playerAGPEntries, new PlayerGpComparator());
        selectRank(AbyssRankEnum.SUPREME_COMMANDER);
        selectRank(AbyssRankEnum.COMMANDER);
        selectRank(AbyssRankEnum.GREAT_GENERAL);
        selectRank(AbyssRankEnum.GENERAL);
        selectRank(AbyssRankEnum.STAR5_OFFICER);
        selectRank(AbyssRankEnum.STAR4_OFFICER);
        selectRank(AbyssRankEnum.STAR3_OFFICER);
        selectRank(AbyssRankEnum.STAR2_OFFICER);
        selectRank(AbyssRankEnum.STAR1_OFFICER);
        updateToNoQuotaRank();
        this.playerAGPEntries.clear();
        this.playerAGpMaps.clear();
    }

    private void selectRank(AbyssRankEnum rank)
    {
        int quota = rank.getId() < 18 ? rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota() : rank.getQuota();
        for (int i = 0; i < quota; i++)
        {
            if (this.playerAGPEntries.isEmpty()) {
              return;
            }
            Map.Entry<Integer, AGPoint> playerAGp = this.playerAGPEntries.get(0);
            if (playerAGp == null) {
                return;
            }

            int playerId = playerAGp.getKey();
            int agp = playerAGp.getValue().getGP();
            if (agp < rank.getGPRequired()) {
                return;
            }
            this.playerAGPEntries.remove(0);
            updateRankTo(rank, playerId);
        }
    }

    private void updateToNoQuotaRank()
    {
        for (Map.Entry<Integer, AGPoint> playerAGPEntry : this.playerAGPEntries)
        {
           AbyssRankEnum rank = AbyssRankEnum.getRankForAGp(playerAGPEntry.getValue().getAP(), 0);
           updateRankTo(rank, playerAGPEntry.getKey());
        }
    }

    protected void updateRankTo(AbyssRankEnum newRank, int playerId)
    {
        Player onlinePlayer = World.getInstance().findPlayer(playerId);
        if (onlinePlayer != null)
        {
            AbyssRank abyssRank = onlinePlayer.getAbyssRank();
            AbyssRankEnum currentRank = abyssRank.getRank();
            if (currentRank != newRank)
            {
                abyssRank.setRank(newRank);
                AbyssPointsService.checkRankChanged(onlinePlayer, currentRank, newRank);
            }
        } else {
            DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
        }
   }

	private static class SingletonHolder {

		protected static final AbyssRankUpdateService instance = new AbyssRankUpdateService();
	}

    private static class PlayerGpComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K, AGPoint>> {

        @Override
        public int compare(Map.Entry<K, AGPoint> o1, Map.Entry<K, AGPoint> o2)
        {
          return -o1.getValue().getGP().compareTo(o2.getValue().getGP());
        }
    }

}
