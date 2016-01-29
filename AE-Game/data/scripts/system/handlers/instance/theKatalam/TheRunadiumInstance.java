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
import java.util.concurrent.atomic.AtomicInteger;


/**
 * M.O.G. Devs Team , fix Romanz
 */

@InstanceID(301110000)
public class TheRunadiumInstance extends GeneralInstanceHandler
{

	private Map<Integer, StaticDoor> doors;
	private int guardGraendalKilled;
	private int IllusionGraendalKilled;
	private final AtomicInteger specNpcKilled = new AtomicInteger();
	private boolean isInstanceDestroyed;


	@Override
	public void onEnterInstance(Player player) {
		super.onInstanceCreate(instance);
		player.getEffectController().removeEffect(218611);
		player.getEffectController().removeEffect(218610);

    }

	@Override
  public void onDie(Npc npc) {

		switch (npc.getNpcId()) {

  	 case 284377:
  	 case 284378:
  	 case 284379:

  		guardGraendalKilled ++;
  	 if (guardGraendalKilled == 1) {
			}
			else if (guardGraendalKilled == 2) {
			}
			else if (guardGraendalKilled == 3) {
		  spawn(231304, 256.52f, 257.87f, 241.78f, (byte) 90);
			}
			despawnNpc(npc);
			break;

  	 case 284383:
  	 case 284384:
  	 case 284428:
  	 case 284429:
  	  IllusionGraendalKilled ++;
	  if (IllusionGraendalKilled == 4) {
 			spawn(231305, 256.5205f, 257.8747f, 241.7870f, (byte) 90);
 			}
 			despawnNpc(npc);
 			break;

  	 case 284380:
  	 case 284381:
  	 case 284382:
  	 case 284659:
   	 case 284660:
   	 case 284661:
   	 case 284662:
   	 case 284663:
  	  despawnNpc(npc);
 			break;

     case 231305:
     sendMsg(1401893);
		if (Rnd.get(1, 100) < 30) {
			spawn(802184, 256.33f, 267.22f, 241.84f, (byte) 0);
		}
 	   despawnNpc(getNpc(284377));
 	   despawnNpc(getNpc(284378));
 	   despawnNpc(getNpc(284379));
 	   despawnNpc(getNpc(284380));
 	   despawnNpc(getNpc(284381));
 	   despawnNpc(getNpc(284382));
 	   despawnNpc(getNpc(284659));
	   despawnNpc(getNpc(284660));
	   despawnNpc(getNpc(284661));
	   despawnNpc(getNpc(284662));
	   despawnNpc(getNpc(284663));
 	   despawnNpc(getNpc(231305));
		 spawn(730843, 256.2082f, 250.2959f, 241.8779f, (byte) 30);
		 spawn(701795, 256.5212f, 258.3195f, 241.7859f, (byte) 89);

		 return;
		}
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 231305:
				if (Rnd.get(1, 100) < 5) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000193, 1));
				}
				break;
			}
		}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
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
