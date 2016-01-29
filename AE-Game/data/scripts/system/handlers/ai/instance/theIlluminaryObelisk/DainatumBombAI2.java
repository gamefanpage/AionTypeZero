package ai.instance.theIlluminaryObelisk;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.Future;

@AIName("dainatum_mine")
public class DainatumBombAI2 extends AggressiveNpcAI2 {

	private Future<?> TasksBomb;
	private boolean isCancelled;

	@Override
	protected void handleSpawned() {
		SkillActive();
		super.handleSpawned();
	}

	private void DainatumBomb(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
	}

	private void SkillActive() {

		TasksBomb = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead() && isCancelled == true) {
					CancelTask();
				}
				else {
					DainatumBomb(21275);
				}
			}
		}, 6000);

		TasksBomb = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead() && isCancelled == true) {
					CancelTask();
				}
				else {
					Npc npc = getOwner();
					NpcActions.delete(npc);
				}
			}
		}, 10000);
	}

	private void CancelTask() {
		if (TasksBomb != null && !TasksBomb.isCancelled()) {
			TasksBomb.cancel(true);
		}
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		CancelTask();
		isCancelled = true;
	}

}
