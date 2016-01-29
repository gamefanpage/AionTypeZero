package instance;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import java.util.Set;

/**
 * @author Romanz
 */

@InstanceID(300480000)
public class DanuarMysticariumInstance extends GeneralInstanceHandler {

    private byte entrance = 0;

    @Override
    public void onEnterInstance(Player player) {
        entrance += 1;
        if (player.getRace() == Race.ELYOS) {
            if (entrance == 1) {
                spawn(805219, 170.8752f, 128.6104f, 231.66339f, (byte) 0);
                spawn(731583, 169.931f, 116.871f, 231.65f, (byte) 15);
            }
        } else {
            if (entrance == 1) {
                spawn(805220, 170.8752f, 128.6104f, 231.66339f, (byte) 0);
                spawn(731584, 169.931f, 116.871f, 231.65f, (byte) 15);
            }
        }
    }

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 219958:
			case 219959:
			case 219971:
				despawnNpc(npc);
				break;
			default:
				break;
		}
	}


	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                int itemId = 0;
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 702700:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000224, 1));
				if (Rnd.get(1, 100) < 70) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000066, 1));
				}
				break;
			case 219963:
			case 219964:
			case 219965:
			case 219966:
			case 219967:
			case 219968:
				dropItems.clear();
				switch (Rnd.get(1, 3)) {
					case 1:
						itemId = 188052548;
						break;
					case 2:
						itemId = 188053620;
						break;
					case 3:
						itemId = 188053400;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 3));
				break;
			}
		}

	@Override
	public void onPlayerLogOut(Player player) {
		cleanItems(player);
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}

	public void cleanItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000224, storage.getItemCountByItemId(185000224));
	}

	@Override
	public void onLeaveInstance(Player player) {
		cleanItems(player);
	}

	@Override
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}
