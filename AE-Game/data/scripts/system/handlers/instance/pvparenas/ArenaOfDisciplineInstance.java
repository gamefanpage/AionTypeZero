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
@InstanceID(300360000)
public class ArenaOfDisciplineInstance extends DisciplineTrainingGroundsInstance {

	@Override
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size() == 1 ? (instanceReward.getRound() == 1 ? 1 : 2) : 2; //enemy left before start: low reward, else full
		// 100 * (rate * size) * (playerScore / playersScore)
		float totalAP = (3.292f * size) * 100; // to do config
		float totalCrucible = (1.964f * size) * 100; // to do config
		float totalCourage = (0.174f * size) * 100; // to do config
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				float playerRate = 1;
				Player player = instance.getPlayer(playerReward.getOwner());
				if (player != null) {
					playerRate = player.getRates().getDisciplineRewardRate();
				}
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank;
				if (instanceReward.getRound() == 3) {
					rank = instanceReward.getRank(score);
				}
				else {
					rank = 1;
				}
				float percent = reward.getParticipation();
				int basicAP = 200;
				// to do other formula
				int rankingAP = 431;
				if (size > 1) {
					rankingAP = rank == 0 ? 1108 : 431;
				}
				int scoreAP = (int) (totalAP * scoreRate);
				basicAP *= percent;
				rankingAP *= percent;
				rankingAP *= playerRate;
				reward.setBasicAP(basicAP);
				reward.setRankingAP(rankingAP);
				reward.setScoreAP(scoreAP);
				reward.setBasicGP(0);
				reward.setRankingGP(0);
				reward.setScoreGP(0);
				int basicCrI = 195;
				basicCrI *= percent;
				// to do other formula
				int rankingCrI = 256;
				if (size > 1) {
					rankingCrI = rank == 0 ? 660 : 256;
				}
				rankingCrI *= percent;
				rankingCrI *= playerRate;
				int scoreCrI = (int) (totalCrucible * scoreRate);
				reward.setBasicCrucible(basicCrI);
				reward.setRankingCrucible(rankingCrI);
				reward.setScoreCrucible(scoreCrI);
				int basicCoI = 0;
				basicCoI *= percent;
				// to do other formula
				int rankingCoI = 23;
				if (size > 1) {
					rankingCoI = rank == 0 ? 59 : 23;
				}
				rankingCoI *= percent;
				rankingCoI *= playerRate;
				int scoreCoI = (int) (totalCourage * scoreRate);
				reward.setBasicCourage(basicCoI);
				reward.setRankingCourage(rankingCoI);
				reward.setScoreCourage(scoreCoI);
				if (instanceReward.canRewardOpportunityToken(reward)) {
					reward.setOpportunity(4);
				}
			}
		}
		super.reward();
	}

}
