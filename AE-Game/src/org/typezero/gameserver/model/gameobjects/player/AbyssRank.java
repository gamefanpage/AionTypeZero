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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.Calendar;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer, Divinity
 */
public class AbyssRank {

    private int dailyAP;
    private int dailyGP;
    private int weeklyAP;
    private int weeklyGP;
    private int currentAp;
    private int currentGP;
    private AbyssRankEnum rank;
    private int topRanking;
    private PersistentState persistentState;
    private int dailyKill;
    private int weeklyKill;
    private int allKill;
    private int maxRank;
    private int lastKill;
    private int lastAP;
    private int lastGP;
    private long lastUpdate;

    public AbyssRank(int dailyAP, int dailyGP, int weeklyAP, int weeklyGP, int ap, int gp, int rank, int topRanking, int dailyKill,
                     int weeklyKill, int allKill, int maxRank, int lastKill, int lastAP, int lastGP, long lastUpdate) {
        this.dailyAP = dailyAP;
        this.dailyGP = dailyGP;
        this.weeklyAP = weeklyAP;
        this.weeklyGP = weeklyGP;
        this.currentAp = ap;
        this.currentGP = gp;
        this.rank = AbyssRankEnum.getRankById(rank);
        this.topRanking = topRanking;
        this.dailyKill = dailyKill;
        this.weeklyKill = weeklyKill;
        this.allKill = allKill;
        this.maxRank = maxRank;
        this.lastKill = lastKill;
        this.lastAP = lastAP;
        this.lastGP = lastGP;
        this.lastUpdate = lastUpdate;

        doUpdate();
	}

	public enum AbyssRankUpdateType{
		PLAYER_ELYOS (1),
		PLAYER_ASMODIANS(2),
		LEGION_ELYOS(4),
		LEGION_ASMODIANS(8);

		private int id;
		AbyssRankUpdateType(int id){
			this.id = id;
		}

		public int value() {
			return id;
		}
	}

    public void addAGp(int additionalAp, int additionalGp)
    {
        this.dailyAP += additionalAp;
        this.dailyGP += additionalGp;
        if (this.dailyAP < 0) {
            this.dailyAP = 0;
        }

        if (this.dailyGP < 0) {
            this.dailyGP = 0;
        }
        this.weeklyAP += additionalAp;
        this.weeklyGP += additionalGp;
        if (this.weeklyAP < 0) {
            this.weeklyAP = 0;
        }

        if (this.weeklyGP < 0) {
            this.weeklyGP = 0;
        }

        int cappedCount = 0;
        if (CustomConfig.ENABLE_AP_CAP) {
            cappedCount = this.currentAp + additionalAp > CustomConfig.AP_CAP_VALUE ? (int)(CustomConfig.AP_CAP_VALUE - this.currentAp) : additionalAp;
        } else {
            cappedCount = additionalAp;
        }
        this.currentAp += cappedCount;
        this.currentGP += additionalGp;
        if (this.currentAp < 0) {
            this.currentAp = 0;
        }

        if (this.currentGP < 0) {
            this.currentGP = 0;
        }

        AbyssRankEnum newRank = AbyssRankEnum.getRankForAGp(this.currentAp, this.currentGP);
        if (newRank.getId() <= 9)
            setRank(newRank);

        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public int getDailyAP()
    {
      return dailyAP;
    }

    public int getDailyGP()
    {
      return dailyGP;
    }

    public int getWeeklyAP()
    {
      return weeklyAP;
    }

    public int getWeeklyGP()
    {
      return weeklyGP;
    }

    public int getAp()
    {
      return currentAp;
    }

    public int getGp()
    {
      return currentGP;
    }

    public AbyssRankEnum getRank()
    {
      return rank;
    }

    public int getTopRanking()
    {
      return topRanking;
    }

    public void setTopRanking(int topRanking)
    {
      this.topRanking = topRanking;
    }

    public int getDailyKill()
    {
      return dailyKill;
    }

    public int getWeeklyKill()
    {
      return weeklyKill;
    }

    public int getAllKill()
    {
      return allKill;
    }

    public void setAllKill()
    {
      dailyKill += 1;
      weeklyKill += 1;
      allKill += 1;
    }

    public int getMaxRank() { return maxRank; }

    public int getLastKill()
    {
      return lastKill;
    }

    public int getLastAP()
    {
      return lastAP;
    }

    public int getLastGP()
    {
      return lastGP;
    }

    public void setRank(AbyssRankEnum rank)
    {
        if (rank.getId() > maxRank) {
            maxRank = rank.getId();
        }
        this.rank = rank;


        topRanking = rank.getQuota();
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public PersistentState getPersistentState()
    {
        return persistentState;
    }

    public void setPersistentState(PersistentState persistentState)
    {
        if ((persistentState != PersistentState.UPDATE_REQUIRED) || (this.persistentState != PersistentState.NEW)) {
          this.persistentState = persistentState;
        }
    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void doUpdate()
    {
        boolean needUpdate = false;
        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(lastUpdate);

        Calendar curCal = Calendar.getInstance();
        curCal.setTimeInMillis(System.currentTimeMillis());
        if ((lastCal.get(Calendar.DATE) != curCal.get(Calendar.DATE)) || (lastCal.get(Calendar.MONTH) != curCal.get(Calendar.MONTH)) || (lastCal.get(Calendar.YEAR) != curCal.get(Calendar.YEAR)))
        {
            this.dailyAP = 0;
            this.dailyGP = 0;
            this.dailyKill = 0;
            needUpdate = true;
        }
        if ((lastCal.get(Calendar.WEEK_OF_YEAR) != curCal.get(Calendar.WEEK_OF_YEAR)) || (lastCal.get(Calendar.YEAR) != curCal.get(Calendar.YEAR)))
        {
            this.lastKill = this.weeklyKill;
            this.lastAP = this.weeklyAP;
            this.lastGP = this.weeklyGP;
            this.weeklyKill = 0;
            this.weeklyAP = 0;
            this.weeklyGP = 0;
            needUpdate = true;
        }
        if (this.rank.getId() > this.maxRank)
        {
            this.maxRank = this.rank.getId();
            needUpdate = true;
        }
        this.lastUpdate = System.currentTimeMillis();
        if (needUpdate) {
            setPersistentState(PersistentState.UPDATE_REQUIRED);
        }
    }
}
