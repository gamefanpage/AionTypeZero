package ai.siege.katalam;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

@AIName("wooden_barrel_parades_06")
public class Wooden_Barrel_Parades_06_AI2 extends GeneralNpcAI2 {

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		BoxActive();
	}

	private void BoxActive() {
		Npc boxBomb = getPosition().getWorldMapInstance().getNpc(701714);
		if (boxBomb != null && !boxBomb.getLifeStats().isAlreadyDead()) {
			spawn(701721, boxBomb.getX(), boxBomb.getY(), boxBomb.getZ(), boxBomb.getHeading());
			boxBomb.getController().onDelete();
		}
	}
}
