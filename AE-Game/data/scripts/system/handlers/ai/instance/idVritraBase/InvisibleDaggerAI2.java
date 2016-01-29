/*
 * Vritra Dager Invisible
 */

package ai.instance.idVritraBase;


import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("dagger_invisible")
public class InvisibleDaggerAI2 extends AggressiveNpcAI2 {


	@Override
	protected void handleSpawned() {
		DaggerBless(3000);
		DaggerBless(4000);
		super.handleSpawned();
	  }

	private void InvisibleDaggerBless(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
		}

	private void DaggerBless(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
			if (time == 3000) {
				InvisibleDaggerBless(21135);
			    }
			if (time == 4000) {
				InvisibleDaggerBless(20251);
			    }
		    }
    }, time);
  }
}
