package ai.instance.refugeRune;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author Romanz
 */
@AIName("key_door")
public class KeyDoorAi2 extends NpcAI2{

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();
		if (dialogId == DialogAction.SETPRO1.id()) {
			switch (getNpcId()) {
				case 701873:
					if (player.getInventory().getItemCountByItemId(185000181) > 0) {
						TeleportService2.teleportTo(player, 301140000, instanceId, 1024.7188f, 275.84598f, 308.8f, (byte) 30, TeleportAnimation.BEAM_ANIMATION);
					} else {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
					}
					break;
				case 701872:
					if (player.getInventory().getItemCountByItemId(185000182) > 0) {
						TeleportService2.teleportTo(player, 301140000, instanceId, 715.77661f, 978.23792f, 318.8f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
					} else {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
					}
					break;
				case 701871:
					if (player.getInventory().getItemCountByItemId(185000183) > 0) {
						TeleportService2.teleportTo(player, 301140000, instanceId, 1003.0133f, 1371.4274f, 338.1f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
					} else {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219));
					}
					break;
			}
		}
		return true;
	}

}
