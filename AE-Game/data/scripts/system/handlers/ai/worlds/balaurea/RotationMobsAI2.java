package ai.worlds.balaurea;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;

@AIName("rotation_mobs")
public class RotationMobsAI2 extends AggressiveNpcAI2 {


	private void spawn_buff() {
		for (int i = 0; i < 2; i++) {
			float direction = Rnd.get(0, 199) / 100f;
			float x1 = (float) (Math.cos(Math.PI * direction) * 5);
			float y1 = (float) (Math.sin(Math.PI * direction) * 5);
			spawn(856175, getOwner().getX() + x1, getOwner().getY() + y1, getOwner().getZ(), (byte) 0);
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
            		if (Rnd.get(1, 30) == 1) {
			spawn_buff();
		}
	}

}
