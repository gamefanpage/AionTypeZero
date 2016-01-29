package ai.siege.balaurea.gelkmaros;

import ai.siege.SiegeNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 *
 */
@AIName("gelkmaros_legion")
public class GelkmarosLegionAI2 extends SiegeNpcAI2 {

    @Override
    public void handleDialogStart(Player player){
        if(player.getLegion() != null){
           int SiegeId = ((SiegeNpc) this.getOwner()).getSiegeId();
           SiegeLocation Location = SiegeService.getInstance().getSiegeLocation(SiegeId);
           if(Location != null){
               if(Location.getLegionId()== player.getLegion().getLegionId()) {
                  PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
                  return;
			   }
           }
        }
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
    }
}
