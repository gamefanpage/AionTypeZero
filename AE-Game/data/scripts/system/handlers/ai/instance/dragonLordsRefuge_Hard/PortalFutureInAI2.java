package ai.instance.steelRose;

import ai.ActionItemNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @rework Romanz
 **/
@AIName("portal_future")
public class PortalFutureInAI2 extends ActionItemNpcAI2
{
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		//Enter
		    case 730673:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 217.144f, 195.616f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 217.144f, 195.616f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730674:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 785.866f, 197.713f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 785.866f, 197.713f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730675:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 217.947f, 832.552f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 217.947f, 832.552f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730676:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 779.178f, 833.055f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 779.178f, 833.055f, 246.0712f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		//Exit
		    case 730633:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 461.493f, 459.308f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 461.493f, 459.308f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730634:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 546.01f, 458.934f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 546.01f, 458.934f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730635:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 462.188f, 568.913f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 462.188f, 568.913f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		    case 730636:
				switch (player.getWorldId()) {
				case 300520000:
						TeleportService2.teleportTo(player, 300520000, 545.773f, 569.225f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
				switch (player.getWorldId()) {
				case 300630000:
						TeleportService2.teleportTo(player, 300630000, 545.773f, 569.225f, 417.405f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			        break;
				}
		    break;
		}
	}
}
