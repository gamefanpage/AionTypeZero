package ai.siege.katalam;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.atomic.AtomicBoolean;

@AIName("legion_gaubice_big")
public class LegionGaubiceBigAI2 extends ActionItemNpcAI2 {

	private AtomicBoolean canUse = new AtomicBoolean(true);

	@Override
	public void handleUseItemFinish(Player player) {

		  if (canUse.compareAndSet(true, false)) {
		  switch (player.getRace()) {
			case ASMODIANS:
		  SkillEngine.getInstance().applyEffectDirectly(21391, player, player, 160000 * 3);
		  break;
			case ELYOS:
		  SkillEngine.getInstance().applyEffectDirectly(21390, player, player, 160000 * 3);
		  break;
		  }
		  PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("LEGION_GAUBICE_BIG"));
		  stopMove(player);
		  AI2Actions.deleteOwner(this);
		}
	}

	private void stopMove(Player player) {
		if (player != null) {
			player.getController().onStopMove();
		}
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
