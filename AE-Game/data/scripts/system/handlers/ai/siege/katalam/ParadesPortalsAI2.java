package ai.siege.katalam;

import ai.GeneralNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;


//By Romanz

@AIName("parades_portal")
public class ParadesPortalsAI2 extends GeneralNpcAI2 {
    private int countuse;

    @Override
    protected void handleDialogStart(Player player) {

        if (SiegeService.getInstance().isSiegeInProgress(6021) && countuse < 10) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == DialogAction.SETPRO1.id()) {
            switch (Rnd.get(1, 16)) {
                case 1:
                    TeleportService2.teleportTo(player, 600060000, 2605.0525f, 2675.3674f, 255.8187f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 2:
                    TeleportService2.teleportTo(player, 600060000, 2718.1396f, 2709.8005f, 256.27106f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 3:
                    TeleportService2.teleportTo(player, 600060000, 2755.1733f, 2661.176f, 254.60423f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 4:
                    TeleportService2.teleportTo(player, 600060000, 2715.261f, 2715.9832f, 256.27106f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 5:
                    TeleportService2.teleportTo(player, 600060000, 2604.0044f, 2674.0989f, 255.8187f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 6:
                    TeleportService2.teleportTo(player, 600060000, 2600.8762f, 2679.2068f, 255.8187f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 7:
                    TeleportService2.teleportTo(player, 600060000, 2563.343f, 2728.2131f, 254.99194f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 8:
                    TeleportService2.teleportTo(player, 600060000, 2597.7876f, 2675.9924f, 255.8187f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 9:
                    TeleportService2.teleportTo(player, 600060000, 2758.429f, 2658.0803f, 254.60423f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 10:
                    TeleportService2.teleportTo(player, 600060000, 2713.6741f, 2713.6401f, 256.27106f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 11:
                    TeleportService2.teleportTo(player, 600060000, 2754.5164f, 2654.051f, 254.60423f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 12:
                    TeleportService2.teleportTo(player, 600060000, 2561.1353f, 2730.533f, 254.99194f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 13:
                    TeleportService2.teleportTo(player, 600060000, 2719.5994f, 2711.7988f, 256.27106f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 14:
                    TeleportService2.teleportTo(player, 600060000, 2566.78f, 2732.4858f, 254.99194f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 15:
                    TeleportService2.teleportTo(player, 600060000, 2751.2563f, 2656.9124f, 254.60423f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
                case 16:
                    TeleportService2.teleportTo(player, 600060000, 2564.3347f, 2734.4973f, 254.99194f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
                    countuse++;
                    break;
            }
        }
        return true;
    }
}
