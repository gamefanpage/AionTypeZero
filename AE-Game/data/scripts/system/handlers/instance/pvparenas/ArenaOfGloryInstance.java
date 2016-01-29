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

package instance.pvparenas;

import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.playerreward.InstancePlayerReward;
import org.typezero.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 *
 * @author xTz
 */
@InstanceID(300550000)
public class ArenaOfGloryInstance extends PvPArenaInstance {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 1000;
		deathFine = -200;
		super.onInstanceCreate(instance);
	}

	@Override
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		// 100 * (rate * size) * (playerScore / playersScore)
		float totalScoreAP = (59.952f * size) * 100;
		// to do other formula
		float rankingRate = 0;
		if (size > 1) {
			rankingRate = (0.077f * (4 - size));
		}
		float totalRankingAP = 30800 - 30800 * rankingRate;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				float playerRate = 1;
				Player player = instance.getPlayer(playerReward.getOwner());
				if (player != null) {
					playerRate = player.getRates().getGloryRewardRate();
				}
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = 0;
				if (instanceReward.getRound() == 3) {
					rank = instanceReward.getRank(score);
				}
				else {
					rank = 3;
				}
				float percent = reward.getParticipation();
				float generalRate = 0.167f + rank * 0.227f;
				int basicAP = 0;
				float rankingAP = totalRankingAP;
				if (rank > 0) {
					rankingAP = rankingAP - rankingAP * generalRate;
				}
				int scoreAP = (int) (totalScoreAP * scoreRate);
				basicAP *= percent;
				rankingAP *= percent;
				rankingAP *= playerRate;
				reward.setBasicAP(basicAP);
				reward.setRankingAP((int) rankingAP);
				reward.setScoreAP(scoreAP);
                reward.setBasicGP(0);
                reward.setScoreGP(0);
				switch (rank) {
					case 0:
						reward.setGloriousInsignia(1);
						reward.setMithrilMedal(5);
                        reward.setRankingGP(300);
						break;
					case 1:
						reward.setGloriousInsignia(1);
						reward.setplatinumMedal(3);
                        reward.setRankingGP(160);
						break;
					case 2:
						reward.setplatinumMedal(3);
                        reward.setRankingGP(106);
						break;
					case 3:
						reward.setLifeSerum(1);
                        reward.setRankingGP(60);
						break;
				}
			}
		}
		super.reward();
	}

}
