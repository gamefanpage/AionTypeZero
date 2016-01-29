package ai.worlds.enshar;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Romanz
 *
 */
@AIName("enshar_tele")
public class EnsharTeleportAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == DialogAction.SETPRO1.id()) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId()) {
				case 804822:
					teleport(player);
					break;
				case 804823:
					teleport2(player);
					break;
				case 804824:
					teleport3(player);
					break;
				case 804825:
					teleport4(player);
					break;
			}
		}
		return true;
	}

	private void teleport(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(286001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 286001, 0));
	}

	private void teleport2(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(284001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 284001, 0));
	}

	private void teleport3(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(283001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 283001, 0));
	}

	private void teleport4(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(285001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 285001, 0));
	}
}
