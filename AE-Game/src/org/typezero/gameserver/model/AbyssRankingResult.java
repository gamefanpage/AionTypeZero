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

package org.typezero.gameserver.model;

/**
 * @author zdead
 */
public class AbyssRankingResult {

    private String playerName;
    private int playerAbyssRank;
    private int oldRankPos;
    private int rankPos;
    private int ap;
    private int gp;
    private int title;
    private PlayerClass playerClass;
    private int playerLevel;
    private int playerId;
    private String legionName;
    private long cp;
    private int legionId;
    private int legionLevel;
    private int legionMembers;
	private Gender gender;

    public AbyssRankingResult(String playerName, int playerAbyssRank, int playerId, int ap, int gp, int title, PlayerClass playerClass, Gender gender, int playerLevel, String legionName, int oldRankPos, int rankPos)
   {
       this.playerName = playerName;
       this.playerAbyssRank = playerAbyssRank;
       this.playerId = playerId;
       this.ap = ap;
       this.gp = gp;
       this.title = title;
       this.playerClass = playerClass;
       this.playerLevel = playerLevel;
       this.legionName = legionName;
       this.oldRankPos = oldRankPos;
       this.rankPos = rankPos;
       this.gender = gender;
   }

   public AbyssRankingResult(long cp, String legionName, int legionId, int legionLevel, int legionMembers, int oldRankPos, int rankPos)
   {
       this.oldRankPos = oldRankPos;
       this.rankPos = rankPos;
       this.cp = cp;
       this.legionName = legionName;
       this.legionId = legionId;
       this.legionLevel = legionLevel;
       this.legionMembers = legionMembers;
   }

	/**
	 * @return the oldRankPos
	 */
    public String getPlayerName()
    {
        return this.playerName;
    }

    public int getPlayerId()
    {
        return this.playerId;
    }

    public int getPlayerAbyssRank()
    {
        return this.playerAbyssRank;
    }

    public int getOldRankPos()
    {
        return this.oldRankPos;
    }

    public int getRankPos()
    {
        return this.rankPos;
    }

    public int getPlayerAP()
    {
        return this.ap;
    }

    public int getPlayerGP()
    {
        return this.gp;
    }

    public int getPlayerTitle()
    {
        return this.title;
    }

    public int getPlayerLevel()
    {
        return this.playerLevel;
    }

    public PlayerClass getPlayerClass()
    {
        return this.playerClass;
    }

    public String getLegionName()
    {
        return this.legionName;
    }

    public long getLegionCP()
    {
        return this.cp;
    }

    public int getLegionId()
    {
        return this.legionId;
    }

    public int getLegionLevel()
    {
        return this.legionLevel;
    }

    public int getLegionMembers()
    {
        return this.legionMembers;
    }
	
	public Gender getGender() {
        return gender;
    }

}
