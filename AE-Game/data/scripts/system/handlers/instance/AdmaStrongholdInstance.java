/*
 * Форт Адма
 */
package instance;

import java.util.Map;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * M.O.G. Devs Team
 */
@InstanceID(320130000)
public class AdmaStrongholdInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;

	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}


	private void removeEffects(Player player) {
		player.getEffectController().removeEffect(18464);
		player.getEffectController().removeEffect(18465);
	}


@Override
public void handleUseItemFinish(Player player, Npc npc) {
	switch (npc.getNpcId()) {
		case 700396:
			SkillEngine.getInstance().getSkill(npc, 18464, 50, npc).useNoAnimationSkill();
			break;
		case 700397:
			SkillEngine.getInstance().getSkill(npc, 18465, 50, npc).useNoAnimationSkill();
			break;
	  }
 }

@Override
public boolean onDie(final Player player, Creature lastAttacker) {
	PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
			: lastAttacker.getObjectId()), true);

	PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
	return true;
   }

}
