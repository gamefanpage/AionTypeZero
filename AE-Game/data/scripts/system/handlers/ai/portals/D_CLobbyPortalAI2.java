package ai.portals;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("d_clobby_portal")
public class D_CLobbyPortalAI2 extends NpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
    int membership = player.getClientConnection().getAccount().getMembership();

        if(membership >= 2)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("T_PORTAL"));
			}
		}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
			TeleportService2.teleportTo(player, 140010000, 281.3614f, 266.1f, 96.5f, (byte) 60, TeleportAnimation.BEAM_ANIMATION);
            break;
        }
        return true;
    }
}
