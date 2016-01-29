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
import org.typezero.gameserver.model.instance.playerreward.HarmonyGroupReward;

/**
 *
 * @author xTz
 */
@InstanceID(300450000)
public class ArenaOfHarmonyInstance extends HaramoniousTrainingCenterInstance {

	@Override
	protected void reward() {
		float totalScoreAP = (9.599f * 3) * 100;
		float totalScoreCourage = (0.1f * 3) * 100;
		int totalPoints = instanceReward.getTotalPoints();
		for (HarmonyGroupReward group : instanceReward.getGroups()) {
			int score = group.getPoints();
			int rank = 0;
			if (instanceReward.getRound() == 3) {
				rank = instanceReward.getRank(score);
			}
			else {
				rank = 1;
			}
			float percent = group.getParticipation();
			float scoreRate = ((float) score / (float) totalPoints);
			int basicAP = 200;
			int rankingAP = 0;
			basicAP *= percent;
			int basicCoI = 0;
			int rankingCoI = 0;
			basicCoI *= percent;
			int scoreAP = (int) (totalScoreAP * scoreRate);
			switch (rank) {
				case 0:
					rankingAP = 4681;
					rankingCoI = 49;
					group.setGloryTicket(1);
					break;
				case 1:
					rankingAP = 1887;
					rankingCoI = 20;
					break;
				case 2:
					rankingAP = 151;
					rankingCoI = 1;
					break;
			}
			rankingAP *= percent;
			rankingCoI *= percent;
			int scoreCoI = (int) (totalScoreCourage * scoreRate);
			group.setBasicAP(basicAP);
			group.setRankingAP(rankingAP);
			group.setScoreAP(scoreAP);
			group.setBasicGP(0);
			group.setRankingGP(0);
			group.setScoreGP(0);
			group.setBasicCourage(basicCoI);
			group.setRankingCourage(rankingCoI);
			group.setScoreCourage(scoreCoI);
		}
		super.reward();
	}
}
