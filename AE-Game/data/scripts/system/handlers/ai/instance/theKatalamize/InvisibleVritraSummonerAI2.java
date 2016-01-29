/*
 * Vritra Dager Invisible
 */

package ai.instance.theKatalamize;


import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("hyperionassassin_invisible")
public class InvisibleVritraSummonerAI2 extends AggressiveNpcAI2 {


	@Override
	protected void handleSpawned() {
		AssassinBless(3000);
		AssassinBless(4000);
		super.handleSpawned();
	  }

	private void InvisibleHyperionAssanssinBless(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
		}

	private void AssassinBless(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
			if (time == 3000) {
				InvisibleHyperionAssanssinBless(21135);
			    }
			if (time == 4000) {
				InvisibleHyperionAssanssinBless(20251);
			    }
		    }
    }, time);
  }
}
