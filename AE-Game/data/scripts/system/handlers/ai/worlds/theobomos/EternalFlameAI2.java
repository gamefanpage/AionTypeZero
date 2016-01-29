package ai.worlds.theobomos;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.world.WorldPosition;

/**
 * @author Romanz
 */
@AIName("eternal_flame")
public class EternalFlameAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		WorldPosition p = getPosition();
		if (p != null) {
			spawn(214552, p.getX(), p.getY(), p.getZ() - 3, (byte) 0);
		}
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

}
