package ai.instance.theKatalamize;

import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;


/**
 * Romanz
 *
 */

@AIName("hy_turret")
public class HyperionTurretAI2 extends AggressiveNpcAI2 {

	private Future<?> CannonAttack;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		startSkillTask();
	}

	private void startSkillTask() {
		CannonAttack = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
		 @Override
		 public void run() {
			if (isAlreadyDead())
				SkillEngine.getInstance().getSkill(getOwner(), 21201, 65, getTarget()).useNoAnimationSkill();
			  cancelTask();
		 }
	  }, 1, 1000);
   }


	private void cancelTask() {
	  if (CannonAttack != null && !CannonAttack.isCancelled()) {
	  	CannonAttack.cancel(true);
	  }
   }
}

