package ai.instance.dragonLordsRefuge_Hard;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;


/**
 * @author Romanz
 *
 */
@AIName("firsttiamat_h")
public class FirstTiamat_HardAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleDeactivate() {
	}

	@Override
	protected void handleSpawned() {
	   super.handleSpawned();
	   if (getNpcId() == 856027)
		  AI2Actions.useSkill(this, 20917);
	}
}

