package ai;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 *
 */
@AIName("dialog_1011")
public class Dialog1011AI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }


}
