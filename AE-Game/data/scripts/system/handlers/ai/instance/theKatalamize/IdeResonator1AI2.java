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
@AIName("ideres1")
public class IdeResonator1AI2 extends NpcAI2 {

	private Future<?> skillTask;
	private Future<?> skillTask1;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startpower();
	}

	private void startpower() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				AI2Actions.targetCreature(IdeResonator1AI2.this, getPosition().getWorldMapInstance().getNpc(231073));
				AI2Actions.useSkill(IdeResonator1AI2.this, 21381);
			}
		}, 3000, 5000);

		skillTask1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401792);
				AI2Actions.targetCreature(IdeResonator1AI2.this, getPosition().getWorldMapInstance().getNpc(231073));
				AI2Actions.useSkill(IdeResonator1AI2.this, 21382);
			}
		}, 32000);
	}

	private void cancelskillTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}

	private void cancelskillTask1() {
		if (skillTask1 != null && !skillTask1.isCancelled()) {
			skillTask1.cancel(true);
		}
	}

	@Override
	protected void handleDied() {
		cancelskillTask();
		cancelskillTask1();
		super.handleDied();
	}

    @Override
    protected void handleDespawned() {
		cancelskillTask();
		cancelskillTask1();
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
