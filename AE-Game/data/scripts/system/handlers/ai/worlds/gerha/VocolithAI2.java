package ai.worlds.gerha;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("vocolith")
public class VocolithAI2 extends NpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
			return true;
		}
	if (dialogId == DialogAction.SETPRO1.id()) {
        switch (getNpcId()) {
		case 804573:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804574:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn2();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804575:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn3();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804579:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn4();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804580:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn5();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804581:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn6();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804582:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn7();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		case 804583:
		if (player.getInventory().getItemCountByItemId(185000216) >= 1) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402541));
			player.getInventory().decreaseByItemId(185000216, 1);
			spawn8();
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		} else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402539));
		}
			break;
		}
		return true;
	}
        return false;

    }

	private void spawn() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 155.14493f, 896.0185f, 241.98811f, (byte) 0);
				break;
			case 2:
				spawn(235218, 155.14493f, 896.0185f, 241.98811f, (byte) 0);
				break;
			case 3:
				spawn(235219, 155.14493f, 896.0185f, 241.98811f, (byte) 0);
				break;
			case 4:
				spawn(235220, 155.14493f, 896.0185f, 241.98811f, (byte) 0);
				break;
		}
	}

	private void spawn2() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 238.737f, 1581.1804f, 227.77995f, (byte) 0);
				break;
			case 2:
				spawn(235218, 238.737f, 1581.1804f, 227.77995f, (byte) 0);
				break;
			case 3:
				spawn(235219, 238.737f, 1581.1804f, 227.77995f, (byte) 0);
				break;
			case 4:
				spawn(235220, 238.737f, 1581.1804f, 227.77995f, (byte) 0);
				break;
		}
	}

	private void spawn3() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 606.0103f, 1462.3446f, 276.86612f, (byte) 0);
				break;
			case 2:
				spawn(235218, 606.0103f, 1462.3446f, 276.86612f, (byte) 0);
				break;
			case 3:
				spawn(235219, 606.0103f, 1462.3446f, 276.86612f, (byte) 0);
				break;
			case 4:
				spawn(235220, 606.0103f, 1462.3446f, 276.86612f, (byte) 0);
				break;
		}
	}

	private void spawn4() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 1106.4177f, 1231.3401f, 308.5724f, (byte) 0);
				break;
			case 2:
				spawn(235218, 1106.4177f, 1231.3401f, 308.5724f, (byte) 0);
				break;
			case 3:
				spawn(235219, 1106.4177f, 1231.3401f, 308.5724f, (byte) 0);
				break;
			case 4:
				spawn(235220, 1106.4177f, 1231.3401f, 308.5724f, (byte) 0);
				break;
		}
	}

	private void spawn5() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 1663.793f, 612.8768f, 227.9901f, (byte) 0);
				break;
			case 2:
				spawn(235218, 1663.793f, 612.8768f, 227.9901f, (byte) 0);
				break;
			case 3:
				spawn(235219, 1663.793f, 612.8768f, 227.9901f, (byte) 0);
				break;
			case 4:
				spawn(235220, 1663.793f, 612.8768f, 227.9901f, (byte) 0);
				break;
		}
	}

	private void spawn6() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 1491.7947f, 313.15384f, 253.56389f, (byte) 0);
				break;
			case 2:
				spawn(235218, 1491.7947f, 313.15384f, 253.56389f, (byte) 0);
				break;
			case 3:
				spawn(235219, 1491.7947f, 313.15384f, 253.56389f, (byte) 0);
				break;
			case 4:
				spawn(235220, 1491.7947f, 313.15384f, 253.56389f, (byte) 0);
				break;
		}
	}

	private void spawn7() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 1211.9669f, 584.015f, 277.96094f, (byte) 0);
				break;
			case 2:
				spawn(235218, 1211.9669f, 584.015f, 277.96094f, (byte) 0);
				break;
			case 3:
				spawn(235219, 1211.9669f, 584.015f, 277.96094f, (byte) 0);
				break;
			case 4:
				spawn(235220, 1211.9669f, 584.015f, 277.96094f, (byte) 0);
				break;
		}
	}

	private void spawn8() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(235217, 894.8337f, 838.7992f, 313.66245f, (byte) 0);
				break;
			case 2:
				spawn(235218, 894.8337f, 838.7992f, 313.66245f, (byte) 0);
				break;
			case 3:
				spawn(235219, 894.8337f, 838.7992f, 313.66245f, (byte) 0);
				break;
			case 4:
				spawn(235220, 894.8337f, 838.7992f, 313.66245f, (byte) 0);
				break;
		}
	}
}
