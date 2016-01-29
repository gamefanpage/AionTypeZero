package ai.siege.katalam;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("parades_gate_control")
public class ParadesGateControllerAI2 extends ActionItemNpcAI2 {

	@Override
	public void handleUseItemFinish(Player player) {

		switch (getNpcId()) {
			case 701784: // South D
			case 701786: // South L
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401691));
				DoorActivatorSouth();
				break;
			case 701783: // North D
			case 701785: // North L
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401690));
				DoorActivatorNorth();
				break;
		}
	}

	private void DoorActivatorSouth() {
		Npc gateD = getPosition().getWorldMapInstance().getNpc(273288);
		gateD.getController().onDelete();
		Npc gateL = getPosition().getWorldMapInstance().getNpc(273289);
		gateL.getController().onDelete();
	}

	private void DoorActivatorNorth() {
		Npc gateD = getPosition().getWorldMapInstance().getNpc(273285);
		gateD.getController().onDelete();
		Npc gateL = getPosition().getWorldMapInstance().getNpc(273286);
		gateL.getController().onDelete();
	}

}
