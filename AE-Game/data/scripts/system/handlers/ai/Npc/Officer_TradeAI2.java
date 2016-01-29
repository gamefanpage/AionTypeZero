package ai.Npc;

import ai.siege.SiegeNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author Romanz
 *
 */
@AIName("off_trade")
public class Officer_TradeAI2 extends SiegeNpcAI2 {

    @Override
    public void handleDialogStart(Player player){
               if (player.getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId()) {
                  PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
                  return;
			   }
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
    }
}
