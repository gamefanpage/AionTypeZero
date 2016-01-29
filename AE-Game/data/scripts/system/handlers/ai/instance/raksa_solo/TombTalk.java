package ai.instance.raksa_solo;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.MathUtil;
import java.util.concurrent.atomic.AtomicBoolean;


@AIName("tomb_talk")
public class TombTalk extends ActionItemNpcAI2 {

private AtomicBoolean startedEvent = new AtomicBoolean(false);

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 5) {
				if (startedEvent.compareAndSet(false, true)) {
					NpcShoutsService.getInstance().sendMsg(getOwner(), 349934, getObjectId(), 0, 1000);
				}
			}
		}
	}

}
