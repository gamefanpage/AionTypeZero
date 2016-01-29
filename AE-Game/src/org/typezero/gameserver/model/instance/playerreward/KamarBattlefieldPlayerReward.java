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

import org.typezero.gameserver.model.Race;

/**
 *
 * @author Sharlatan
 */
public class KamarBattlefieldPlayerReward extends InstancePlayerReward {

    private int timeBonus;
    private long logoutTime;
    private float timeBonusModifier;
    private Race race;
    private int rewardAp;
    private int bonusAp;
    private int reward1;
    private int reward2;
    private int bonusReward;
    private float rewardCount;


    public KamarBattlefieldPlayerReward(Integer object, int timeBonus, Race race) {
        super(object);
        this.timeBonus = timeBonus;
        timeBonusModifier = ((float) this.timeBonus / (float) 660000);
        this.race = race;
    }

    public float getParticipation() {
        return (float) getTimeBonus() / timeBonus;
    }

    public int getScorePoints() {
        return timeBonus + getPoints();
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

    public Race getRace() {
        return race;
    }

    public int getRewardAp() {
        return rewardAp;
    }

    public int getBonusAp() {
        return bonusAp;
    }

    public void setBonusAp(int ap) {
        this.bonusAp = ap;
    }

    public void setRewardAp(int ap) {
        this.rewardAp = ap;
    }

    public int getReward1() {
        return reward1;
    }

    public int getReward2() {
        return reward2;
    }

    public int getBonusReward() {
        return bonusReward;
    }

    public int getRewardCount() {
        return (int) rewardCount;
    }

    public void setReward1(int reward) {
        this.reward1 = reward;
    }

    public void setReward2(int reward) {
        this.reward2 = reward;
    }

    public void setBonusReward(int reward) {
        this.bonusReward = reward;
    }

    public void setRewardCount(float rewardCount) {
        this.rewardCount = rewardCount;
    }
}
