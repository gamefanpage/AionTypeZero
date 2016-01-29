package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.services.NpcShoutsService;
import java.util.concurrent.Future;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("makekike_box")
public class MakekikeBoxAI2 extends AggressiveNpcAI2 {
    private Future<?> SpawnTimer;


	@Override
	protected void handleSpawned() {
        BoxDestroy();
		super.handleSpawned();
        SpawnTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                float direction = Rnd.get(0, 199) / 100f;
                int distance = Rnd.get(1, 4);
                float x1 = (float) (Math.cos(Math.PI * direction) * distance);
                float y1 = (float) (Math.sin(Math.PI * direction) * distance);
                spawn(233145, getOwner().getX() + x1,  getOwner().getY() + y1,  getOwner().getZ(), (byte) 0);
                spawn(233145, getOwner().getX() + y1,  getOwner().getY() + x1,  getOwner().getZ(), (byte) 0);
                spawn(233145, getOwner().getX() + x1,  getOwner().getY() + y1,  getOwner().getZ(), (byte) 0);
                spawn(233146, getOwner().getX() + x1,  getOwner().getY() + y1,  getOwner().getZ(), (byte) 0);
                getOwner().getController().die();
            }
        },35000);
	}

	private void Gossip_20() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500997, getObjectId(), 0, 1000);
	}

	private void BoxDestroy() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead())
					Gossip_20();
				else {
				}
			}
		}, 1000);
	}

    private void cancelTask() {
        if (SpawnTimer != null && !SpawnTimer.isCancelled()) {
            SpawnTimer.cancel(true);
        }
    }

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}

    @Override
    protected void handleDied() {
        cancelTask();
        super.handleDied();
    }
}
