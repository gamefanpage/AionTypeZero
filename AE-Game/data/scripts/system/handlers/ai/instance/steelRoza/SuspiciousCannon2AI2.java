package ai.instance.steelRoza;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("suspiciouscannonroza")
public class SuspiciousCannon2AI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleUseItemFinish(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(73001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 73001, 0));
	}

}
