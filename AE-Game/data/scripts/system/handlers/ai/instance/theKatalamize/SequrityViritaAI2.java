package ai.instance.theKatalamize;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * M.O.G. Devs Team
 */
@AIName("virita_giperion")
public class SequrityViritaAI2 extends NpcAI2 {

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500792, getObjectId(), 0, 13000);
		startEventTask(30000);
	}

	private void startEventTask(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
		if (!isAlreadyDead()) {
			if (time == 30000) {
				AI2Actions.deleteOwner(SequrityViritaAI2.this);
			    }
		    }
    	}
    }, time);
  }
 }
