package ai;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("coinfountain")
public class CoinFountainAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getCommonData().getLevel() >= 50) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 21050));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        }
    }

    @Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
		if (player.getInventory().getItemCountByItemId(186000030) >= 1) {
			player.getInventory().decreaseByItemId(186000030, 1);
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1008, 21050));
			giveItem(player);
                } else {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("COIN_GOLD"));
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
                }
                break;
        }
        return true;
    }



    private void giveItem(Player player) {
        int rnd = Rnd.get(0, 100);
        if (rnd < 15) {
            ItemService.addItem(player, 186000096, 1);
        }
        else if (rnd > 15 && rnd < 20) {
            ItemService.addItem(player, 186000096, 2);
        }
        else if (rnd > 20 && rnd < 30) {
            ItemService.addItem(player, 186000030, 1);
        }
	else {
            ItemService.addItem(player, 182005205, 1);
        }
    }
}
