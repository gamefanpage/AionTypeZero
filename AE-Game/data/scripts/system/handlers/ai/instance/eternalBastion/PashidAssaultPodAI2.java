/*
 * Pashid Assault Pod
 */
package ai.instance.eternalBastion;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldPosition;

import java.util.concurrent.Future;

/**
 * M.O.G. Devs Team
 */

@AIName("pashid_assault_pod")
public class PashidAssaultPodAI2 extends NpcAI2 {
    private Future<?> spawntask;

    @Override
	protected void handleSpawned() {
        StartTimerPodSpawn();
		super.handleSpawned();
	  }

    private void cancelSpawnTask() {
        if (spawntask != null && !spawntask.isDone()) {
            spawntask.cancel(true);
        }
    }

    private void StartTimerPodSpawn() {
        spawntask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSpawnTask();
                } else {
                    spawn_assault();
                }
            }
        }, 10000, 120000);
	    }

	private void spawn_assault() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(0, 10);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		int rnd = Rnd.get(1, 2);
    switch (rnd) {
     case 1:
      	spawn(231105, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
     	  spawn(231108, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
     	  spawn(231106, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
        break;
     case 2:
    	  spawn(231105, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
    	  spawn(231108, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
    	  spawn(231107, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
        break;
       }
	   }

    @Override
    protected void handleDespawned() {
        cancelSpawnTask();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        cancelSpawnTask();
        super.handleDied();
    }

}
