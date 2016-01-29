package ai.instance.idVritraBase;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;

/**
 * @author Romanz
 */
@AIName("false_chest")
public class FalseChestAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		spawn(230843, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

}
