package ai.siege.kaldor;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Romanz
 */
@AIName("mercenary_trap")
public class MercenaryTrapAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleCreatureAggro(Creature creature) {

		AI2Actions.useSkill(this, 20465);//21516
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.deleteOwner(MercenaryTrapAI2.this);
			}

		}, 1500);
	}

}
