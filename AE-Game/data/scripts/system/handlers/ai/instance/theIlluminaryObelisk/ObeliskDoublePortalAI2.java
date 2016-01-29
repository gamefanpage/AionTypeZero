package ai.instance.theIlluminaryObelisk;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * Author Romanz
 **/
@AIName("obrlisk_dportal")
public class ObeliskDoublePortalAI2 extends ActionItemNpcAI2
{
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		    case 730886: //Portal
				 switch (player.getWorldId()) {
                     case 301230000: //Illuminary Obelisk
						TeleportService2.teleportTo(player, 301230000, 255.1f, 269.05f, 455.2f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
			         break;
				 } switch (player.getWorldId()) {
                     case 301370000: //Illuminary Obelisk Hero
						TeleportService2.teleportTo(player, 301370000, 255.1f, 269.05f, 455.2f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
			         break;
				 }
		    break;
		}
	}
}
