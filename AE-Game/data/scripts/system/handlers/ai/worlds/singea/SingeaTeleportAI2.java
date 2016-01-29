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
@AIName("singea_tele")
public class SingeaTeleportAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == DialogAction.SETPRO1.id()) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId()) {
				case 804811:
					teleport(player);
					break;
				case 804812:
					teleport2(player);
					break;
				case 804813:
					teleport3(player);
					break;
				case 804814:
					teleport4(player);
					break;
			}
		}
		return true;
	}

	private void teleport(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(279001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 279001, 0));
	}

	private void teleport2(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(281001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 281001, 0));
	}

	private void teleport3(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(280001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 280001, 0));
	}

	private void teleport4(Player player) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(282001);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 282001, 0));
	}
}
