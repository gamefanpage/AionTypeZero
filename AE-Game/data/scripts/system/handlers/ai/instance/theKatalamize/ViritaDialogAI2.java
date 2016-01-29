package ai.instance.theKatalamize;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * M.O.G. Devs Team
 */
@AIName("vritragialog_giperion")
public class ViritaDialogAI2 extends NpcAI2 {

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500761, getObjectId(), 0, 10000);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500762, getObjectId(), 0, 15000);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500760, getObjectId(), 0, 20000);
		startEventTask(30000);
	}

	private void startEventTask(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
		if (!isAlreadyDead()) {
			if (time == 30000) {
				AI2Actions.deleteOwner(ViritaDialogAI2.this);
				spawn(230396, 109.6370f, 140.9392f, 112.1742f, (byte) 12);
				spawn(230396, 130.4754f, 132.7612f, 112.2131f, (byte) 83);
				spawn(230396, 151.7122f, 132.7612f, 112.2131f, (byte) 83);
				spawn(230396, 124.9110f, 162.9065f, 129.2247f, (byte) 64);

				spawn(230397, 106.8842f, 143.6426f, 112.2893f, (byte) 24);
				spawn(230397, 133.4831f, 113.4827f, 128.9372f, (byte) 10);
				spawn(230397, 147.9318f, 136.2378f, 112.1742f, (byte) 60);
				spawn(230397, 127.3380f, 161.2330f, 129.2247f, (byte) 92);
			    }
		    }
    	}

    }, time);
  }
 }
