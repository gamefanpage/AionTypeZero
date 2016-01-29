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

package org.typezero.gameserver.model.instance.playerreward;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.InstanceBuff;

/**
 *
 * @author xTz
 */
public class PvPArenaPlayerReward extends InstancePlayerReward {

	private int position;
	private int timeBonus;
	private float timeBonusModifier;
	private int basicAP;
	private int rankingAP;
	private int scoreAP;
	private int basicGP; // 4.5
    private int rankingGP; // 4.5
    private int scoreGP; // 4.5
	private int basicCrucible;
	private int rankingCrucible;
	private int scoreCrucible;
	private int basicCourage;
	private int rankingCourage;
	private int scoreCourage;
	private int opportunity;
	private int gloryTicket;
	private int mithrilMedal;
	private int platinumMedal;
	private int gloriousInsignia;
	private int lifeSerum;
	private long logoutTime;
	private boolean isRewarded = false;
	private InstanceBuff boostMorale;

	public PvPArenaPlayerReward(Integer object, int timeBonus, byte buffId) {
		super(object);
		super.addPoints(13000);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		boostMorale = new InstanceBuff(buffId);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getTimeBonus() {
		return timeBonus > 0 ? timeBonus : 0;
	}

	public void updateLogOutTime() {
		logoutTime = System.currentTimeMillis();
	}

	public void updateBonusTime() {
		int offlineTime = (int) (System.currentTimeMillis() - logoutTime);
		timeBonus -= offlineTime * timeBonusModifier;
	}

	public boolean isRewarded() {
		return isRewarded;
	}

	public void setRewarded() {
		isRewarded = true;
	}

	public int getBasicAP() {
		return basicAP;
	}

	public int getRankingAP() {
		return rankingAP;
	}

	public int getScoreAP() {
		return scoreAP;
	}

	public void setBasicAP(int ap) {
		this.basicAP = ap;
	}

	public void setRankingAP(int ap) {
		this.rankingAP = ap;
	}

	public void setScoreAP(int ap) {
		this.scoreAP = ap;
	}

    public int getBasicGP() { // 4.5
        return basicGP;
    }

    public int getRankingGP() { // 4.5
        return rankingGP;
    }

    public int getScoreGP() { // 4.5
        return scoreGP;
    }

    public void setBasicGP(int gloryPoint) { // 4.5
        this.basicGP = gloryPoint;
    }

    public void setRankingGP(int gloryPoint) { // 4.5
        this.rankingGP = gloryPoint;
    }

    public void setScoreGP(int gloryPoint) { // 4.5
        this.scoreGP = gloryPoint;
    }
	public float getParticipation() {
		return (float) getTimeBonus() / timeBonus;
	}

	public int getBasicCrucible() {
		return basicCrucible;
	}

	public int getRankingCrucible() {
		return rankingCrucible;
	}

	public int getScoreCrucible() {
		return scoreCrucible;
	}

	public void setBasicCrucible(int basicCrucible) {
		this.basicCrucible = basicCrucible;
	}

	public void setRankingCrucible(int rankingCrucible) {
		this.rankingCrucible = rankingCrucible;
	}

	public void setScoreCrucible(int scoreCrucible) {
		this.scoreCrucible = scoreCrucible;
	}

	public void setBasicCourage(int basicCourage) {
		this.basicCourage = basicCourage;
	}

	public void setRankingCourage(int rankingCourage) {
		this.rankingCourage = rankingCourage;
	}

	public void setScoreCourage(int scoreCourage) {
		this.scoreCourage = scoreCourage;
	}

	public int getBasicCourage() {
		return basicCourage;
	}

	public int getRankingCourage() {
		return rankingCourage;
	}

	public int getScoreCourage() {
		return scoreCourage;
	}

	public int getOpportunity() {
		return opportunity;
	}

	public void setOpportunity(int opportunity) {
		this.opportunity = opportunity;
	}

	public int getGloryTicket() {
		return gloryTicket;
	}

	public void setGloryTicket(int gloryTicket) {
		this.gloryTicket = gloryTicket;
	}

	public int getMithrilMedal() {
		return mithrilMedal;
	}

	public void setMithrilMedal(int mithrilMedal) {
		this.mithrilMedal = mithrilMedal;
	}

	public int getPlatinumMedal() {
		return platinumMedal;
	}

	public void setplatinumMedal(int platinumMedal) {
		this.platinumMedal = platinumMedal;
	}

	public int getGloriousInsignia() {
		return gloriousInsignia;
	}

	public void setGloriousInsignia(int gloriousInsignia) {
		this.gloriousInsignia = gloriousInsignia;
	}

	public int getLifeSerum() {
		return lifeSerum;
	}

	public void setLifeSerum(int lifeSerum) {
		this.lifeSerum = lifeSerum;
	}

	public int getScorePoints() {
		return timeBonus + getPoints();
	}

	public boolean hasBoostMorale() {
		return boostMorale.hasInstanceBuff();
	}

	public void applyBoostMoraleEffect(Player player) {
		boostMorale.applyEffect(player, 20000);
	}

	public void endBoostMoraleEffect(Player player) {
		boostMorale.endEffect(player);
	}

	public int getRemaningTime() {
		int time = boostMorale.getRemaningTime();
		if (time >= 0 && time < 20) {
			return 20 - time;
		}
		return 0;
	}
}
