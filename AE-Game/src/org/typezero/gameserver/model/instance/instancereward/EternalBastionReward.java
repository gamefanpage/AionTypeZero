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

package org.typezero.gameserver.model.instance.instancereward;

import org.typezero.gameserver.model.instance.playerreward.EternalBastionPlayerReward;

/**
 * @author Romanz
 */
public class EternalBastionReward extends InstanceReward<EternalBastionPlayerReward> {

    private int points;
    private int npcKills;
    private int rank = 7;
    private int basicAP;
    private int powerfulBundlewater;
    private int ceraniumMedal;
    private int powerfulBundleessence;
    private int largeBundlewater;
    private int largeBundleessence;
    private int smallBundlewater;
    private boolean isRewarded = false;

    public EternalBastionReward(Integer mapId, int instanceId) {
        super(mapId, instanceId);
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public void addNpcKill() {
        npcKills++;
    }

    public int getNpcKills() {
        return npcKills;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
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

    public void setBasicAP(int ap) {
        this.basicAP = ap;
    }

    public int getCeramiumMedal() {
        return ceraniumMedal;
    }

    public int getPowerfulBundleWater() {
        return powerfulBundlewater;
    }

    public int getPowerfulBundleEssence() {
        return powerfulBundleessence;
    }

    public int getLargeBundleWater() {
        return largeBundlewater;
    }

    public int getLargeBundleEssence() {
        return largeBundleessence;
    }

    public int getSmallBundleWater() {
        return smallBundlewater;
    }

    public void setCeramiumMedal(int ceraniumMedal) {
        this.ceraniumMedal = ceraniumMedal;
    }

    public void setPowerfulBundleWater(int powerfulBundlewater) {
        this.powerfulBundlewater = powerfulBundlewater;
    }

    public void setPowerfulBundleEssence(int powerfulBundleessence) {
        this.powerfulBundleessence = powerfulBundleessence;
    }

    public void setLargeBundleWater(int largeBundlewater) {
        this.largeBundlewater = largeBundlewater;
    }

    public void setLargeBundleEssence(int largeBundleessence) {
        this.largeBundleessence = largeBundleessence;
    }

    public void setSmallBundleWater(int smallBundlewater) {
        this.smallBundlewater = smallBundlewater;
    }
}
