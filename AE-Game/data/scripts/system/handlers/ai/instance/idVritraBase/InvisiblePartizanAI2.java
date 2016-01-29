package ai.instance.idVritraBase;


import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Romanz
 */

@AIName("partizan_invisible")
public class InvisiblePartizanAI2 extends AggressiveNpcAI2 {

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
