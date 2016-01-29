package instance.steelroza;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

@InstanceID(301050000)
public class SteelRoseDeckInstance extends GeneralInstanceHandler {

	private boolean isInstanceDestroyed;
	private int Killed;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
	  }

	@Override
	public void onEnterInstance(Player player) {
  	player.getEffectController().removeEffect(218611);
		player.getEffectController().removeEffect(218610);
		player.getEffectController().removeEffect(284320);
	}

	@Override
	public void onDie(Npc npc) {
		Creature master = npc.getMaster();
    if (master instanceof Player)
	    return;
		if (isInstanceDestroyed) {
			return;
		}
     switch (npc.getNpcId()) {

			case 230727:
			case 230728:
			case 230729:
			case 230730:
				Killed ++;
				if (Killed == 1) {
				}
				else if (Killed == 2) {
				}
				else if (Killed == 3) {
				}
				else if (Killed == 4) {
				spawn(230741, 487.51f, 508.65f, 1032.83f, (byte) 0);
				}
 			 despawnNpc(npc);
				break;

 		 case 230702:
 		 case 230703:
 		 case 230706:
 		 case 230696:
 		 case 230697:
 		 case 231016:
 		 case 231017:
 		 case 231018:
 			 despawnNpc(npc);
 		   break;

    }
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
	  case 730786: // Cannon Deck
	  	SkillEngine.getInstance().getSkill(npc, 20385, 65, npc).useNoAnimationSkill();
	   break;
	  case 730772: // Cannon Deck
	  	SkillEngine.getInstance().getSkill(npc, 20385, 65, npc).useNoAnimationSkill();
		 break;
	  case 730788: // Cannon Deck
	  	SkillEngine.getInstance().getSkill(npc, 20385, 65, npc).useNoAnimationSkill();
		 break;
	  case 730792: // Cannon Deck
	  	SkillEngine.getInstance().getSkill(npc, 20385, 65, npc).useNoAnimationSkill();
		 break;
	  case 730794: // Cannon Deck
	  	SkillEngine.getInstance().getSkill(npc, 20385, 65, npc).useNoAnimationSkill();
		 break;
		}
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}

}
