package ai.portals;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @rework Romanz
 **/
@AIName("blood_jewel")
public class BloodJewelAI2 extends ActionItemNpcAI2
{
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		    case 730625:
				switch (player.getWorldId()) {
				case 300520000:
				TeleportService2.teleportTo(player, 300520000, 543.346f, 514.509f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
				break;
				}
				switch (player.getWorldId()) {
				case 300630000:
				TeleportService2.teleportTo(player, 300630000, 543.346f, 514.509f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
				break;
				}
		    break;
		}
	}
}
