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

package org.typezero.gameserver.utils.stats;

import org.typezero.gameserver.configs.main.RateConfig;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;

import javax.xml.bind.annotation.XmlEnum;

/**
 * @author ATracer
 * @author Sarynth
 * @author Imaginary
 */
@XmlEnum
public enum AbyssRankEnum {

    GRADE9_SOLDIER(1, 120, 24, 0, 0, 0, 0, 1802431),
    GRADE8_SOLDIER(2, 168, 37, 1200, 0, 0, 0, 1802433),
    GRADE7_SOLDIER(3, 235, 58, 4220, 0, 0, 0, 1802435),
    GRADE6_SOLDIER(4, 329, 91, 10990, 0, 0, 0, 1802437),
    GRADE5_SOLDIER(5, 461, 143, 23500, 0, 0, 0, 1802439),
    GRADE4_SOLDIER(6, 645, 225, 42780, 0, 0, 0, 1802441),
    GRADE3_SOLDIER(7, 903, 356, 69700, 0, 0, 0, 1802443),
    GRADE2_SOLDIER(8, 1264, 561, 105600, 0, 0, 0, 1802445),
    GRADE1_SOLDIER(9, 1770, 885, 150800, 0, 0, 0, 1802447),
    STAR1_OFFICER(10, 2124, 1428, 0, 1244, 7, 1000, 1802449),
    STAR2_OFFICER(11, 2549, 1973, 0, 1368, 14, 700, 1802451),
    STAR3_OFFICER(12, 3059, 2704, 0, 1915, 28, 500, 1802453),
    STAR4_OFFICER(13, 3671, 3683, 0, 3064, 49, 300, 1802455),
    STAR5_OFFICER(14, 4405, 4994, 0, 5210, 107, 100, 1802457),
    GENERAL(15, 5286, 6749, 0, 8335, 119, 30, 1802459),
    GREAT_GENERAL(16, 6343, 9098, 0, 10002, 122, 10, 1802461),
    COMMANDER(17, 7612, 11418, 0, 11503, 127, 3, 1802463),
    SUPREME_COMMANDER(18, 9134, 13701, 0, 12437, 147, 1, 1802465);

    private int id;
    private int pointsGained;
    private int pointsLost;
    private int requiredAP;
    private int requiredGP;
    private int delgp;
    private int quota;
    private int descriptionId;

    private AbyssRankEnum(int id, int pointsGained, int pointsLost, int requiredAP, int requiredGP, int delgp, int quota, int descriptionId) {
        this.id = id;
        this.pointsGained = pointsGained;
        this.pointsLost = pointsLost;
        this.requiredAP = (requiredAP * RateConfig.ABYSS_RANK_RATE);
        this.requiredGP = requiredGP;
        this.delgp = delgp;
        this.quota = quota;
        this.descriptionId = descriptionId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the pointsLost
	 */
	public int getPointsLost() {
		return pointsLost;
	}

	/**
	 * @return the pointsGained
	 */
	public int getPointsGained() {
		return pointsGained;
	}

	/**
	 * @return AP required for Rank
	 */
	public int getAPRequired() {
		return requiredAP;
	}

	/**
     * @return Glory Point required for Rank
     */
    public int getGPRequired() {
        return requiredGP;
    }

    public int getDelGp()
    {
        return this.delgp;
    }

	/**
	 * @return The quota is the maximum number of allowed player to have the rank
	 */
	public int getQuota() {
		return quota;
	}

	public int getDescriptionId() {
		return descriptionId;
	}

	public static DescriptionId getRankDescriptionId(Player player){
		int pRankId = player.getAbyssRank().getRank().getId();
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == pRankId) {
				int descId = rank.getDescriptionId();
				return (player.getRace() == Race.ELYOS) ? new DescriptionId(descId) : new DescriptionId(descId + 36);
			}
		}
		throw new IllegalArgumentException("No rank Description Id found for player: " + player);
	}

	/**
	 * @param id
	 * @return The abyss rank enum by his id
	 */
	public static AbyssRankEnum getRankById(int id) {
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == id)
				return rank;
		}
		throw new IllegalArgumentException("Invalid abyss rank provided" + id);
	}

    public static AbyssRankEnum getRankForAGp(int ap, int gp)
    {
        AbyssRankEnum r = GRADE9_SOLDIER;
        for (AbyssRankEnum rank : values()) {
            if (gp >= 1244 && ap >= 150800)
            {
                if (rank.getGPRequired() > gp) {
                    break;
                }
                r = rank;
            } else {
                if ((rank.getAPRequired() > ap) || (rank.id > 9)) {
                    break;
                }
                r = rank;
            }
        }
        return r;
    }
}
