/*
 * Arena's 4.6.0
 */
package instance.pvparenas;

import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.instance.playerreward.HarmonyGroupReward;

/**
 * @author xTz
 * @modified M.O.G. Dision
 */
@InstanceID(301100000)
public class ArenaOfUnityInstance extends HaramoniousTrainingCenterInstance {

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
			group.setBasicGP(0); // 4.5
			group.setRankingGP(0); // 4.5
			group.setScoreGP(0); // 4.5
			group.setBasicCourage(basicCoI);
			group.setRankingCourage(rankingCoI);
			group.setScoreCourage(scoreCoI);
		}
		super.reward();
	}
}
