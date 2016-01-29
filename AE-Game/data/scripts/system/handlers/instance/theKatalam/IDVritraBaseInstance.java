package instance.theKatalam;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.Map;
import java.util.Set;


/**
 * @author Dision , Romanz
 *
 */
@InstanceID(301130000)
public class IDVritraBaseInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;
	private boolean isInstanceDestroyed;
	private int drakans;
	private boolean startSuramaEvent;

	@Override
	public void onEnterInstance(Player player) {
  	player.getEffectController().removeEffect(218611);
		player.getEffectController().removeEffect(218610);
		player.getEffectController().removeEffect(284320);
	}

	@Override
	public void onDie(Npc npc) {
		if (isInstanceDestroyed) {
			return;
		}
		switch (npc.getNpcId()) {
			case 230849:
				sendMsg(1401914);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 600.03f, 351.22f, 202.91f, (byte) 0);
				}
				doors.get(383).setOpen(true);
				break;
			case 230851:
				sendMsg(1401915);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 634.28f, 496.86f, 202.53f, (byte) 0);
				}
				doors.get(59).setOpen(true);
				break;
			case 230818:
				sendMsg(1401916);
				doors.get(372).setOpen(true);
				break;
			case 230850:
				sendMsg(1401917);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 473.77f, 330.28f, 181.79f, (byte) 0);
				}
				doors.get(375).setOpen(true);
				despawnNpc(getNpc(284455));
				despawnNpc(getNpc(284457));
				despawnNpc(getNpc(284687));
				break;
			case 233255:
				sendMsg(1401918);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 378.47f, 297.19f, 158.83f, (byte) 0);
				}
				doors.get(378).setOpen(true);
				break;
			case 230852:
				sendMsg(1401919);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 296.99f, 339.82f, 159.36f, (byte) 0);
				}
				doors.get(388).setOpen(true);
				break;
			case 233256:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 266.56f, 397.63f, 156.83f, (byte) 0);
				}
				despawnNpc(getNpc(233285));
				despawnNpc(getNpc(233286));
				break;
			case 230790:
				sendMsg(1401920);
				doors.get(376).setOpen(true);
				break;
			case 230853:
				sendMsg(1401921);
				sendMsg(1401922);
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 154.59f, 430.45f, 150.77f, (byte) 0);
				}
				spawn(730872, 130.36f, 432.42f, 151.67f, (byte) 117, 3);
				break;

			case 284435:
			case 284455:
			case 284457:
			case 284687:
				despawnNpc(npc);
				break;
			case 233285:
			case 233286:
				despawnNpc(npc);
				break;

			// Secret Exit portal
			case 230854:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 304.29f, 893.22f, 411.60f, (byte) 0);
				}
				spawn(801967, 322.83f, 889.77f, 411.45f, (byte) 60);
				break;
			case 230855:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 501.16f, 894.52f, 411.62f, (byte) 0);
				}
				spawn(801967, 519.19f, 889.77f, 411.45f, (byte) 60);
				break;
			case 230856:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 104.94f, 899.45f, 411.33f, (byte) 0);
				}
				spawn(801967, 124.89f, 889.77f, 411.45f, (byte) 60);
				break;
			case 230857:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 710.66f, 897.54f, 411.53f, (byte) 0);
				}
				spawn(801967, 718.97f, 889.77f, 411.45f, (byte) 60);
				break;
			case 230858:
				if (Rnd.get(1, 100) < 20) {
					spawn(802181, 903.43f, 893.13f, 411.57f, (byte) 0);
				}
				spawn(801967, 915.43f, 889.77f, 411.45f, (byte) 60);
				break;
		}
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 230849:
			case 230850:
			case 230851:
			case 230852:
			case 230853:
			case 230854:
			case 230855:
			case 230856:
			case 230857:
			case 230858:
				if (Rnd.get(1, 100) < 3) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000191, 1));
				}
				break;
			}
		}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		if (Rnd.get(1, 2) == 1) {
			spawn(230846, 497.44034f, 410.62006f, 182.13792f, (byte) 90);
		}
		else {
			spawn(230845, 494.81485f, 401.50598f, 182.13792f, (byte) 90);
		}

		int chance = Rnd.get(1, 2);
		spawn(chance == 1 ? 230847 : 230848, 673.56476f, 350.30112f, 203.68098f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 673.5929f, 355.43643f, 203.68098f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 319.44083f, 388.2419f, 159.1054f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 320.11377f, 363.97287f, 159.13863f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 260.303f, 364.23767f, 159.13574f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 280.89667f, 327.7948f, 159.36792f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 299.76794f, 328.2231f, 159.36792f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 516.25977f, 460.57306f, 182.00262f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 582.1885f, 474.40613f, 191.13799f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 508.2988f, 528.0495f, 181.9969f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 470.9632f, 413.62903f, 181.98624f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 490.32245f, 392.86053f, 181.98306f, (byte) 0);
		spawn(chance == 1 ? 230847 : 230848, 488.26163f, 357.20752f, 182.0165f, (byte) 0);
		spawn(chance == 1 ? 230848 : 230847, 492.07034f, 357.97043f, 181.98257f, (byte) 0);
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}

	@Override
	public void onLeaveInstance(Player player)
	{
		player.getInventory().decreaseByItemId(185000179, player.getInventory().getItemCountByItemId(185000179));
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}
}
