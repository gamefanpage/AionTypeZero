package ai.instance.refugeRune;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;

/**
 * @author Romanz
 */

@AIName("danuar_coffin")
public class DanuarCoffinAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
            		super.handleDied();
            		if (Rnd.get(1, 2) == 1) {
			spawnRemains();
		}
	}

	private void spawnRemains() {
                spawn(233085, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
                AI2Actions.deleteOwner(this);
	}



}
