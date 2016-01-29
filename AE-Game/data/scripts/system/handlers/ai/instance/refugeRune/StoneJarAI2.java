package ai.instance.refugeRune;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;

/**
 * @author Romanz
*/
@AIName("stone_jar")
public class StoneJarAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
            		super.handleDied();
            		if (Rnd.get(1, 3) == 1) {
			spawnRemains();
		}
	}

	private void spawnRemains() {
                spawn(284026, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
                AI2Actions.deleteOwner(this);
	}



}
