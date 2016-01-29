package ai.instance.varuna_lab;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("varuna_portal")
public class Varuna_PortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
	int instanceId = getPosition().getInstanceId();
        switch (dialogId) {
            case 20007:
				if (player.getInventory().getItemCountByItemId(185000196) >= 1) {
					player.getInventory().decreaseByItemId(185000196, 1);
					spawn(702592, 161.43388f, 258.7004f, 312.6443f, (byte) 0);
					spawn(234990, 252.2635f, 259.4003f, 312.37897f, (byte) 0);
					TeleportService2.teleportTo(player, 301270000, instanceId, 212.3f, 259.5f, 313.65f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
					AI2Actions.deleteOwner(this);
                } else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
                }
                break;
            case 20008:
				if (player.getInventory().getItemCountByItemId(185000196) >= 5) {
					player.getInventory().decreaseByItemId(185000196, 5);
					spawn(702592, 161.43388f, 258.7004f, 312.6443f, (byte) 0);
					spawn(233898, 252.2635f, 259.4003f, 312.37897f, (byte) 0);
					TeleportService2.teleportTo(player, 301270000, instanceId, 212.3f, 259.5f, 313.65f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
					AI2Actions.deleteOwner(this);
                } else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
                }
                break;
            case 20009:
				if (player.getInventory().getItemCountByItemId(185000196) >= 7) {
					player.getInventory().decreaseByItemId(185000196, 7);
					spawn(702592, 161.43388f, 258.7004f, 312.6443f, (byte) 0);
					spawn(234991, 252.2635f, 259.4003f, 312.37897f, (byte) 0);
					TeleportService2.teleportTo(player, 301270000, instanceId, 212.3f, 259.5f, 313.65f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
					AI2Actions.deleteOwner(this);
                } else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
                }
                break;
        }
        return true;
    }
}
