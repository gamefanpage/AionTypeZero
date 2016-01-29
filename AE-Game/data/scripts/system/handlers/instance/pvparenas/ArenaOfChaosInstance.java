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

/**
 *
 * @author xTz
 */
@InstanceID(300350000)
public class ArenaOfChaosInstance extends ChaosTrainingGroundsInstance {

	@Override
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		// 100 * (rate * size) * (playerScore / playersScore)
		float totalScoreAP = (3.292f * size) * 100;
		float totalScoreCrucible = (1.964f * size) * 100;
		float totalScoreCourage = (0.225f * size) * 100;
		// to do other formula
		float rankingRate = 0;
		if (size > 1) {
			rankingRate = (0.077f * (8 - size));
		}
		float totalRankingAP = 1453 - 1453 * rankingRate;
		float totalRankingCrucible = 865 - 865 * rankingRate;
		float totalRankingCourage = 101 - 101 * rankingRate;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				float playerRate = 1;
				Player player = instance.getPlayer(playerReward.getOwner());
				if (player != null) {
					playerRate = player.getRates().getChaosRewardRate();
				}
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank;
				if (instanceReward.getRound() == 3) {
					rank = instanceReward.getRank(score);
				}
				else {
					rank = 7;
				}
				float percent = reward.getParticipation();
				float generalRate = 0.167f + rank * 0.095f;
				int basicAP = 200;
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
				reward.setBasicGP(0); // 4.5
				reward.setRankingGP(0); // 4.5
				reward.setScoreGP(0); // 4.5
				int basicCrI = 195;
				basicCrI *= percent;
				float rankingCrI = totalRankingCrucible;
				if (rank > 0) {
					rankingCrI = rankingCrI - rankingCrI * generalRate;
				}
				rankingCrI *= percent;
				rankingCrI *= playerRate;
				int scoreCrI = (int) (totalScoreCrucible * scoreRate);
				reward.setBasicCrucible(basicCrI);
				reward.setRankingCrucible((int) rankingCrI);
				reward.setScoreCrucible(scoreCrI);
				int basicCoI = 0;
				basicCoI *= percent;
				float rankingCoI = totalRankingCourage;
				if (rank > 0) {
					rankingCoI = rankingCoI - rankingCoI * generalRate;
				}
				rankingCoI *= percent;
				rankingCoI *= playerRate;
				int scoreCoI = (int) (totalScoreCourage * scoreRate);
				reward.setBasicCourage(basicCoI);
				reward.setRankingCourage((int) rankingCoI);
				reward.setScoreCourage(scoreCoI);
				if (instanceReward.canRewardOpportunityToken(reward)) {
					reward.setOpportunity(4);
				}
				if (rank < 2) {
					reward.setGloryTicket(1);
				}
			}
		}
		super.reward();
	}

}
