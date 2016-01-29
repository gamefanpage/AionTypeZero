/*
 * Pashid Commander
 */
package ai.instance.eternalBastion;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * M.O.G. Devs Team
 */

@AIName("pashid_commander")
public class PashidCommanderAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		PashidSkill(3000);
		PashidSkill(6000);
		super.handleSpawned();
	  }

	private void PashidFirstSkill(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
		}
	private void VritraLegionBless(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
		}

	private void PashidSkill(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
			if (time == 3000) {
				VritraLegionBless(20700);
			    }
			if (time == 6000) {
				PashidFirstSkill(21237);
			    }
		    }
    }, time);
  }

	protected int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}

  }
