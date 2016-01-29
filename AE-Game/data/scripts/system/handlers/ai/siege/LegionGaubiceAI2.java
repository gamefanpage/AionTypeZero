package ai.siege;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * M.O.G. Devs Team
 */
@AIName("legion_gaubice")
public class LegionGaubiceAI2 extends ActionItemNpcAI2 {

	private AtomicBoolean canUse = new AtomicBoolean(true);

	@Override
	protected void handleUseItemFinish(Player player) {

		if (canUse.compareAndSet(true, false)) {
			int morphSkill = getMorphSkill();
			SkillEngine.getInstance().getSkill(getOwner(), morphSkill >> 8, morphSkill & 0xFF, player).useNoAnimationSkill();
			player.getController().onStopMove();

			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		}
	}

	private int getMorphSkill() {
		switch (getNpcId()) {
			// Asmodians
			case 272269:
				return 0x4F8D3C;
			case 272270:
				return 0x4F8D3C;
			case 272769:
				return 0x4F8D3C;
			case 272770:
				return 0x4F8D3C;
			// Elyos
			case 272267:
				return 0x4F8C3C;
			case 272268:
				return 0x4F8C3C;
			case 272767:
				return 0x4F8C3C;
			case 272768:
				return 0x4F8C3C;
		}
		return 0;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return super.pollInstance(question);
		}
	}
}
