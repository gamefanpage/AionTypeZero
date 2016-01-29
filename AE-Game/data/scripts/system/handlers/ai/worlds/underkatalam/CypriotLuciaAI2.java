package ai.worlds.kataramunderground;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

import ai.AggressiveNpcAI2;

@AIName("cypriotlucia")
public class CypriotLuciaAI2 extends AggressiveNpcAI2 {

	private int stage = 0;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkLifeStart(getLifeStats().getHpPercentage());
	}

	private void checkLifeStart(int perce) {
		if (perce <= 50 && stage == 0) {
			stage++;
			this.spawn();
		}
		if (perce <= 25 && stage == 1) {
			stage++;
			this.spawn();
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		stage = 0;
	}

	private void spawn() {
		for (int i = 0; i < 2; i++) {
			float direction = Rnd.get(0, 199) / 100f;
			float x1 = (float) (Math.cos(Math.PI * direction) * 5);
			float y1 = (float) (Math.sin(Math.PI * direction) * 5);
			spawn(284273, getOwner().getX() + x1, getOwner().getY() + y1, getOwner().getZ(), (byte) 0);
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		Npc npc = getOwner().getPosition().getWorldMapInstance().getNpc(284273);
		Npc npc1 = getOwner().getPosition().getWorldMapInstance().getNpc(284278);
		if (npc != null && npc.isSpawned())
			npc.getController().delete();
		if (npc1 != null && npc.isSpawned())
			npc1.getController().delete();
	}
}
