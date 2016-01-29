/*
 * Aggro Support
 */

package ai.instance.refugeRune;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;


/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("aggro_support")
public class AggroSupportAI2 extends AggressiveNpcAI2 {


	@Override
	protected void handleSpawned() {
		SulackBless(3000);
		super.handleSpawned();
	}

	private void SulackGeneralBless(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
		}

	private void SulackBless(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
			if (time == 3000) {
				SulackGeneralBless(20557);
			    }
		    }
    }, time);
  }

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkForSupport(creature);
	}

	private void checkForSupport(Creature creature) {
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			if (object instanceof Npc && isInRange(object, 25) && !((Npc) object).getLifeStats().isAlreadyDead())
				((Npc) object).getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
		}
	}

}

