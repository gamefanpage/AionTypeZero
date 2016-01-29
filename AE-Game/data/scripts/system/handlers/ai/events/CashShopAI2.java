package ai.events;

import ai.GeneralNpcAI2;
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.controllers.observer.GaleCycloneObserver;
import org.typezero.gameserver.dao.InGameShopLogDAO;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.sql.Timestamp;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Romanz
 */
@AIName("cash_shop")
public class CashShopAI2 extends GeneralNpcAI2 {

    private final Logger log = LoggerFactory.getLogger("INGAMESHOP_LOG");

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
                addStarcard(player);
                break;
        }
        return true;
    }

    public void addStarcard(final Player player) {
        final int toll = 1;
        final long tolls = player.getClientConnection().getAccount().getToll();
        RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
            @Override
            public void acceptRequest(Creature p2, Player p) {
                if (tolls < toll) {
                    PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("COST") + toll + " Points. " + MuiService.getInstance().getMessage("ALL_POINTS") + tolls + " Points.");
                    return;
                }
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
			return;
		}
                p.getClientConnection().getAccount().setToll(tolls - toll);
                InGameShopEn.getInstance().delToll(player, toll);
                ItemService.addItem(player, 186000344, 5);
                DAOManager.getDAO(InGameShopLogDAO.class).log("BUY", new Timestamp(System.currentTimeMillis()), player.getName(), player.getAcountName(), player.getName(), 186000344, 1, 1);
		DAOManager.getDAO(InventoryDAO.class).store(player);
                log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " buy VIP cost 1 toll.");
                PacketSendUtility.sendMessage(p, MuiService.getInstance().getMessage("COST_END") + toll + " Points.");
            }

            @Override
            public void denyRequest(Creature p2, Player p) {
            }
        };
        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, 0, MuiService.getInstance().getMessage("COST") + toll + " Points. " + MuiService.getInstance().getMessage("ALL_POINTS") + tolls + " Points. "
                    + MuiService.getInstance().getMessage("BUY_MONEY")));
        }
    }
}
