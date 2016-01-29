package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import java.util.concurrent.Future;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("rukibuki_summon")
public class RukibukiSummonAI2 extends AggressiveNpcAI2 {

	private Future<?> Tasks;

	@Override
	protected void handleSpawned() {
		Gossip_14();
		RukibukiDespawn();
	}

	private void Gossip_14() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500991, getObjectId(), 0, 1000);
	}

	private void RukibukiDespawn() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Npc npc = getOwner();
				NpcActions.delete(npc);
			}
		}, 30000);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}

}
