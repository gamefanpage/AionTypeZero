package ai.instance.dragonLordsRefuge_Hard;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.concurrent.Future;


/**
 * @author Romanz
 *
 */
@AIName("calindisurkana_h")
public class CalindiSurkana_HardAI2 extends NpcAI2 {

	private Future<?> skillTask;
	Npc calindi;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		calindi = getPosition().getWorldMapInstance().getNpc(856026);
		reflect();
	}

	private void reflect() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				 NpcShoutsService.getInstance().sendMsg(getOwner(), 1401543);
			   SkillEngine.getInstance().applyEffectDirectly(20891, getOwner(), calindi, 0);
			}
		}, 3000, 10000);
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
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
