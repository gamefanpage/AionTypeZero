package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;


/**
 * @author Romanz
 *
 */
@AIName("kutol")
public class HeadKutolAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);

		if (Rnd.get(1, 100) < 1) {
			spawnClone();
		}
	}

	private void spawnClone() {
		Npc KutolClone = getPosition().getWorldMapInstance().getNpc(282302);
		int random = Rnd.get(1,3);
		if (KutolClone == null) {
			switch(random){
				case 1:
					spawn(282302, getOwner().getX(), getOwner().getY(), getOwner().getZ() + 2, (byte) 3);
					break;
				case 2:
					spawn(282302, getOwner().getX(), getOwner().getY(), getOwner().getZ() + 2, (byte) 3);
					spawn(282302, getOwner().getX()-5, getOwner().getY()-3, getOwner().getZ() + 2, (byte) 3);
				break;
				default:
				  spawn(282302, getOwner().getX(), getOwner().getY(), getOwner().getZ() + 2, (byte) 3);
					spawn(282302, getOwner().getX()-5, getOwner().getY()-3, getOwner().getZ() + 2, (byte) 3);
					spawn(282302, getOwner().getX()+5, getOwner().getY()-3, getOwner().getZ() + 2, (byte) 3);
					break;
			}
		}
	}
}
