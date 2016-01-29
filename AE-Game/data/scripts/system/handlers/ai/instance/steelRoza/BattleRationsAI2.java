package ai.instance.steelRoza;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;

/**
 * @author Romanz
 */
@AIName("battle_rations")
public class BattleRationsAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
			case 730770:
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 5000);
				AI2Actions.deleteOwner(this);
				break;
		}
	}
}
