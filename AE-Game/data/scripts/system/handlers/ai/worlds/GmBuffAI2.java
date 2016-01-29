package ai.worlds;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("gm_buff")
public class GmBuffAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
                addBuff(player);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                break;
        }
        return true;
    }

    public void addBuff(final Player player) {
        final int toll = 2;
        final long tolls = player.getClientConnection().getAccount().getToll();
        RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
            @Override
            public void acceptRequest(Creature p2, Player p) {
                if (tolls < toll) {
                    PacketSendUtility.sendMessage(player, MuiService.getInstance().getMessage("ADD_BUFF") + toll + " Points. " + MuiService.getInstance().getMessage("ALL_POINTS") + tolls + " Points.");
                    return;
                }
                p.getClientConnection().getAccount().setToll(tolls - toll);
                InGameShopEn.getInstance().delToll(player, toll);
                SkillEngine.getInstance().getSkill(getOwner(), 10350, 1, player).useWithoutPropSkill();
                PacketSendUtility.sendMessage(p, MuiService.getInstance().getMessage("COST_END") + toll + " Points.");
            }

            @Override
            public void denyRequest(Creature p2, Player p) {
            }
        };
        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, 0, MuiService.getInstance().getMessage("ADD_BUFF") + toll + " Points. " + MuiService.getInstance().getMessage("ALL_POINTS") + tolls + " Points. "
                    + MuiService.getInstance().getMessage("BUY_BUFF")));
        }
    }
}
