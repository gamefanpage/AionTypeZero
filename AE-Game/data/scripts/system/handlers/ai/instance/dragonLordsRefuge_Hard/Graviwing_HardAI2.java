package ai.instance.dragonLordsRefuge_Hard;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;


/**
 * @author Romanz
 *
 */
@AIName("graviwing_h")
public class Graviwing_HardAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		isDeadGod();
	}

	private boolean isDeadGod() {
		Npc marcutan = getNpc(856023);
		Npc kaisinel = getNpc(856020);
		if (isDead(marcutan) || isDead(kaisinel)) {
			AI2Actions.useSkill(this, 20983);
			return true;
		}
		return false;
	}

	private boolean isDead(Npc npc) {
		return (npc != null && npc.getLifeStats().isAlreadyDead());
	}

	private Npc getNpc(int npcId) {
		return getPosition().getWorldMapInstance().getNpc(npcId);
	}
}
