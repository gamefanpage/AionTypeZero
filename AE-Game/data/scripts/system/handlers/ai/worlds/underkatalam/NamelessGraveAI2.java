package ai.worlds.underkatalam;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;

/**
 * @author Romanz
 */

@AIName("nameless_grave")
public class NamelessGraveAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		spawn(283905, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

	@Override
	public int modifyDamage(int damage) {
		return super.modifyDamage(1);
	}
}
