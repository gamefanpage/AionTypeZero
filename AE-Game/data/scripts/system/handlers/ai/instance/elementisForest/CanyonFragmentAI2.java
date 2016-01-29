package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;

/**
 * @author Romanz
 */
@AIName("canyonfragment")
public class CanyonFragmentAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		spawnCanyonGuardSpirit();
	}

	protected void spawnCanyonGuardSpirit() {
		if (getOwner()!= null && !getOwner().getLifeStats().isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(282430, getOwner().getX(), getOwner().getY(), getOwner().getZ() + 2, (byte) 3);
					getOwner().getController().die();
				}

			}, 25000);

		}
	}
}
