package ai.instance.theKatalamize;

import java.util.concurrent.Future;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;


/**
 * @author DeathMagnestic
 *
 */
@AIName("ideres3")
public class IdeResonator3AI2 extends NpcAI2 {

	private Future<?> skillTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		power();
	}

	private void power() {
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401794);
				AI2Actions.targetCreature(IdeResonator3AI2.this, getPosition().getWorldMapInstance().getNpc(231073));
				AI2Actions.useSkill(IdeResonator3AI2.this, 21416);
			}
		}, 32000);
	}

	private void cancelskillTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}

	@Override
	protected void handleDied() {
		cancelskillTask();
		super.handleDied();
	}

    @Override
    protected void handleDespawned() {
		cancelskillTask();
        super.handleDespawned();
    }

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

}
