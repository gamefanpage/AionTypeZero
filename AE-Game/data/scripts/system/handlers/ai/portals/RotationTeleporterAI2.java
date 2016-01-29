package ai.portals;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Romanz
 */
@AIName("rotation_tele")
public class RotationTeleporterAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
                if (player.getRace() == Race.ELYOS) {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
                } else {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1352));
                }
	}
}
