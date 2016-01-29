package ai.portals;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.SourceLocation;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.teleport.PortalService;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Romanz
 *
 */
@AIName("eye_portal")
public class EyePortalsAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
	}

	private boolean checkSourceCount(Player player) {
		int count = 0;
		for (final SourceLocation source : SiegeService.getInstance().getSources().values()) {
			if (source.getRace().getRaceId() == player.getRace().getRaceId()) {
				count++;
			}
		}
		if (count >= 2) {
			return true;
		}
		PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("T_PORTAL"));
		return false;
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
				PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath != null) {
					PortalService.port(portalPath, player, getObjectId());
				}
			break;
            case 10001:
				PortalPath portalPath1 = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath1 != null) {
					PortalService.port(portalPath1, player, getObjectId());
				}
			break;
            case 10002:
			if (checkSourceCount(player)) {
				PortalPath portalPath2 = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath2 != null) {
					PortalService.port(portalPath2, player, getObjectId());
				}
			}
			break;
            case 10003:
			if (checkSourceCount(player)) {
				PortalPath portalPath3 = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath3 != null) {
					PortalService.port(portalPath3, player, getObjectId());
				}
			}
			break;
        }
        return true;
    }
}
