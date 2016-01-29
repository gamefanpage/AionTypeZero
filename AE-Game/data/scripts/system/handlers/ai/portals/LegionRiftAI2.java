package ai.portals;

import ai.ActionItemNpcAI2;
import com.aionemu.commons.utils.Rnd;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Romanz
 *
 */
@AIName("legion_rift")
public class LegionRiftAI2 extends ActionItemNpcAI2
{

	@Override
	protected void handleUseItemFinish(Player player)
	{
        if (player.getRace() == Race.ELYOS) {
            switch (Rnd.get(1, 6)) {
                case 1:
                    TeleportService2.teleportTo(player, 220080000, 103.75f, 2458.56f, 197.1f, (byte) 28, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 2:
                    TeleportService2.teleportTo(player, 220080000, 1616.37f, 2895.15f, 238.1f, (byte) 70, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 3:
                    TeleportService2.teleportTo(player, 220080000, 2734.27f, 1595.81f, 343.1f, (byte) 107, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 4:
                    TeleportService2.teleportTo(player, 220080000, 107.72f, 1634.49f, 306.1f, (byte) 104, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 5:
                    TeleportService2.teleportTo(player, 220080000, 1913.61f, 550.21f, 258.1f, (byte) 100, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 6:
                    TeleportService2.teleportTo(player, 220080000, 2250.96f, 530.58f, 261.1f, (byte) 97, TeleportAnimation.BEAM_ANIMATION);
                    break;
            }
        } else {
            switch (Rnd.get(1, 6)) {
                case 1:
                    TeleportService2.teleportTo(player, 210070000, 2471.4949f, 517.88892f, 577.70447f, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 2:
                    TeleportService2.teleportTo(player, 210070000, 1910.3715f, 487.89838f, 560.77167f, (byte) 65, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 3:
                    TeleportService2.teleportTo(player, 210070000, 730.22198f, 1508.844f, 471.32031f, (byte) 32, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 4:
                    TeleportService2.teleportTo(player, 210070000, 2021.3585f, 1321.0011f, 452.09579f, (byte) 107, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 5:
                    TeleportService2.teleportTo(player, 210070000, 1776.8193f, 2534.353f, 338.30954f, (byte) 38, TeleportAnimation.BEAM_ANIMATION);
                    break;
                case 6:
                    TeleportService2.teleportTo(player, 210070000, 507.68134f, 2304.4377f, 343.5907f, (byte) 41, TeleportAnimation.BEAM_ANIMATION);
                    break;
            }
        }
	}

}
