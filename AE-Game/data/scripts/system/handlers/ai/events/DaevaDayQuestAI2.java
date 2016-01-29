package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("daeva_day_quest")
public class DaevaDayQuestAI2 extends GeneralNpcAI2 {

    @Override
	protected void handleDialogStart(Player player) {
            int level = player.getLevel();
		if (level > 15) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
	}
}
