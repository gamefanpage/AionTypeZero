package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("quest_shop")
public class QuestShopAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
	int membership = player.getClientConnection().getAccount().getMembership();

	if(player.getInventory().getItemCountByItemId(186000343) == 1)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
	else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
	}
}
