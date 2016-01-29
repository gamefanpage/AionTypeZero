package ai.worlds.underkatalam;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;

/**
 * @author Romanz
 */

@AIName("blackened_grave")
public class BlackenedGraveAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		spawn(284262, 394.15338f, 893.5626f, 559.375f, (byte) 0);
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

	@Override
	public int modifyDamage(int damage) {
		return super.modifyDamage(1);
	}

}
