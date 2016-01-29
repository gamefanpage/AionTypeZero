package ai.instance.theIlluminaryObelisk;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.Future;

@AIName("dainatum_healers")
public class DainatumHealersAI2 extends GeneralNpcAI2 {

	private Future<?> SkillTasks;
	private boolean isCancelled;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		SkillActive();
	}

	private void HealDainatum(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
	}

	private void SkillActive() {

		SkillTasks = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead() && isCancelled == true) {
					CancelTask();
				}
				else {
					HealDainatum(21535);
				}
			}
		}, 1000, 10000);
	}

	private void CancelTask() {
		if (SkillTasks != null && !SkillTasks.isCancelled()) {
			SkillTasks.cancel(true);
		}
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		CancelTask();
		isCancelled = true;
		super.handleDied();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
	}
}
