package ai.instance.unstableSplinterpath;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.world.WorldPosition;

/**
 *
 * @author Romanz
 */
@AIName("greater_fragment")
public class GreaterOrkanimumFragmentAI2 extends AggressiveNpcAI2 {

        @Override
        protected void handleSpawned() {
                super.handleSpawned();
        }

	private Npc rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		return (Npc) spawn(npcId,p .getX() + x1, p .getY() + y1, p .getZ(), (byte) 0);
	}

	private void spawnChests(int npcId) {
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
	}

	@Override
	protected void handleDied() {
		spawnChests(701587);
		super.handleDied();
	}
}
