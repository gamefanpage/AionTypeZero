package instance.steelroza;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

@InstanceID(301020000)
public class SteelRoseQuartersInstance extends GeneralInstanceHandler {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		if (Rnd.get(1, 2) == 1) {
			spawn(230650, 745.5443f, 457.9260f, 942.3539f, (byte) 60);
		}
		else {
			spawn(230650, 728.008f, 541.524f, 942.354f, (byte) 60);
		}
	  }

	@Override
	public void onEnterInstance(Player player) {
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 730764:
				TeleportService2.teleportTo(player, 301020000, 702.97f, 500.75f, 939.62f, (byte) 119, TeleportAnimation.BEAM_ANIMATION);
			break;
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
