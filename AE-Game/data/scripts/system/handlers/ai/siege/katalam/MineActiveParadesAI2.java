package ai.siege.katalam;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.Future;

@AIName("mine_active_parades")
public class MineActiveParadesAI2 extends AggressiveNpcAI2 {

	private Future<?> Tasks;

	@Override
	protected void handleSpawned() {
		MineActive();
	}

	private void Mine(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
	}

	private void MineActive() {

		Tasks = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Mine(21275);
			}
		}, 500);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Npc npc = getOwner();
				NpcActions.delete(npc);
			}
		}, 3000);
	}

	private void CancelTask() {
		if (Tasks != null && !Tasks.isCancelled()) {
			Tasks.cancel(true);
		}
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		CancelTask();
	}

}
