package ai.instance.muadasTrencher;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author Romanz
 */
@AIName("general_chunapa")
public class GeneralChunapaAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (Rnd.get(1, 100) < 3) {
			spawnShirik();
		}
	}

	private void spawnShirik(){
			Npc Shirik = getPosition().getWorldMapInstance().getNpc(282535);
			if (Shirik == null) {
			spawn(282535, getOwner().getX() + 5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
			spawn(282535, getOwner().getX() + 10, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
			spawn(282535, getOwner().getX() -5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
			spawn(282535, getOwner().getX() -6, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
		}
	}
}
