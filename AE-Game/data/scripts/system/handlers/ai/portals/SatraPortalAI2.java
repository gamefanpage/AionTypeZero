package ai.portals;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.model.templates.portal.PortalUse;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.PortalService;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Romanz
 */
@AIName("satraportal")
public class SatraPortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10)); // Initial dialog
	}

	@Override
	public boolean onDialogSelect(Player player, final int dialogId, int questId, int extendedRewardIndex) {
		switch (dialogId) {
			case 65: { // I'm ready to enter
				if (player.isInGroup2()) {
					moveToInstance(player);
				}
				else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENTER_ONLY_PARTY_DON);
				}
				break;
			}
		}
		return true;
	}

	private void moveToInstance(Player player) {
		PortalUse portalUse = DataManager.PORTAL2_DATA.getPortalUse(getNpcId());
		if (portalUse != null) {
			PortalPath portalPath = portalUse.getPortalPath(player.getRace());
			if (portalPath != null) {
				PortalService.port(portalPath, player, getObjectId());
			}
		}
	}
}
