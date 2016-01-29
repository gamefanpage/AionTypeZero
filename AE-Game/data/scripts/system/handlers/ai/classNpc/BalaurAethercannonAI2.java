package ai.classNpc;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author Romanz
 */
@AIName("balaur_aethercannon")
public class BalaurAethercannonAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		skill();
		super.handleDied();
	}

	private void skill() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 20465, getOwner().getLevel());
			AI2Actions.deleteOwner(BalaurAethercannonAI2.this);
		}
	}
}
