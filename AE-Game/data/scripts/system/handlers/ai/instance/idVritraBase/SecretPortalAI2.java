package ai.instance.idVritraBase;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("secret_portal")
public class SecretPortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 20008:
				if (player.getInventory().getItemCountByItemId(185000179) >= 1) {
					player.getInventory().decreaseByItemId(185000179, 1);
					spawn(730876, 127.77f, 432.75f, 151.69f, (byte) 117);
					AI2Actions.deleteOwner(this);
                } else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
                }
                break;
            case 20009:
				if (player.getInventory().getItemCountByItemId(185000179) >= 2) {
					player.getInventory().decreaseByItemId(185000179, 2);
					spawn(730877, 127.77f, 432.75f, 151.69f, (byte) 117);
					AI2Actions.deleteOwner(this);
                } else {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
                }
                break;
        }
        return true;
    }
}
