package ai.instance.admaStronghold;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("wreck_unstable")
public class WreckUnstableAI2 extends NpcAI2 {

    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
          final QuestState qs = player.getQuestStateList().getQuestState(2094);
        if (player.getRace() == Race.ELYOS){
            AI2Actions.deleteOwner(this);
            return;
        }

        if (player.getRace() == Race.ASMODIANS){
           if (qs == null)
           {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
                    return;
           }
            if (qs.getStatus() != QuestStatus.COMPLETE){
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            }

            else
            {
               AI2Actions.deleteOwner(this);
            }
    }
}
}
