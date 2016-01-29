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

import java.util.NoSuchElementException;

/**
 * @author Source
 */
public enum APRewardEnum {

	GRADE9_SOLDIER(1, 100f),
	GRADE8_SOLDIER(2, 100f),
	GRADE7_SOLDIER(3, 100f),
	GRADE6_SOLDIER(4, 93.75f),
	GRADE5_SOLDIER(5, 87.5f),
	GRADE4_SOLDIER(6, 84.75f),
	GRADE3_SOLDIER(7, 81.25f),
	GRADE2_SOLDIER(8, 62.5f),
	GRADE1_SOLDIER(9, 37.5f),
	STAR1_OFFICER(10, 31.25f),
	STAR2_OFFICER(11, 31.25f),
	STAR3_OFFICER(12, 18.75f),
	STAR4_OFFICER(13, 18.75f),
	STAR5_OFFICER(14, 12.5f),
	GENERAL(15, 6.25f),
	GREAT_GENERAL(16, 6.25f),
	COMMANDER(17, 6.25f),
	SUPREME_COMMANDER(18, 6.25f);

	private int playerRank;

	private float rewardPercent;

	private APRewardEnum(int playerRank, float rewardPercent) {
		this.playerRank = playerRank;
		this.rewardPercent = rewardPercent;
	}

	public float rewardPercent() {
		return rewardPercent;
	}

	/**
	 * @param playerRank current Abyss Rank
	 * @return AP reward percentage
	 */
	public static float apReward(int playerRank) {
		if (playerRank < GRADE9_SOLDIER.playerRank) {
			return GRADE9_SOLDIER.rewardPercent;
		}
		if (playerRank > SUPREME_COMMANDER.playerRank) {
			return SUPREME_COMMANDER.rewardPercent;
		}

		for (APRewardEnum apReward : values()) {
			if (apReward.playerRank == playerRank) {
				return apReward.rewardPercent;
			}
		}

		throw new NoSuchElementException("AP reward for such rank was not found");
	}

}
