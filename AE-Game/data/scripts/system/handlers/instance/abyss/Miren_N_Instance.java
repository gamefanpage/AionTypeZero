package instance.abyss;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.Set;

@InstanceID(301290000)
public class Miren_N_Instance extends GeneralInstanceHandler {

	private boolean rewarded = false;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 233719: // bosses
				spawnChests(npc);
				break;
			case 235537:
				int ap = 0;
				switch (Rnd.get(1, 3)) {
					case 1:
						ap = 3000;
					break;
					case 2:
						ap = 6000;
					break;
					case 3:
						ap = 15000;
					break;
				}

				final int apReward = ap / instance.getPlayersInside().size();
				instance.doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
				AbyssPointsService.addAGp(player, apReward, 0);
				PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
				}

			});
                            despawnNpc(npc);
				break;
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		int spawn_point = Rnd.get(1, 2);
		spawn(spawn_point == 1 ? 235537 : 832865, 649.1376f, 278.06427f, 191.72736f, (byte) 90);
		int spawn_point2 = Rnd.get(1, 2);
		spawn(spawn_point2 == 1 ? 235537 : 832865, 424.37238f, 278.05197f, 191.72f, (byte) 90);
		int spawn_point3 = Rnd.get(1, 2);
		spawn(spawn_point3 == 1 ? 235537 : 832865, 387.86472f, 431.94095f, 197.20628f, (byte) 0);
		int spawn_point4 = Rnd.get(1, 2);
		spawn(spawn_point4 == 1 ? 235537 : 832865, 661.91473f, 431.9554f, 197.20628f, (byte) 60);
		int spawn_point5 = Rnd.get(1, 2);
		spawn(spawn_point5 == 1 ? 235537 : 832865, 500.65875f, 338.41437f, 180.32727f, (byte) 0);
		int spawn_point6 = Rnd.get(1, 2);
		spawn(spawn_point6 == 1 ? 235537 : 832865, 555.80597f, 575.031f, 176.89429f, (byte) 60);
		int spawn_point7 = Rnd.get(1, 2);
		spawn(spawn_point7 == 1 ? 235537 : 832865, 393.83f, 591.704f, 191.18f, (byte) 90);
		int spawn_point8 = Rnd.get(1, 2);
		spawn(spawn_point8 == 1 ? 235537 : 832865, 659.3397f, 571.47174f, 191.20131f, (byte) 60);
		int spawn_point9 = Rnd.get(1, 2);
		spawn(spawn_point9 == 1 ? 235537 : 832865, 540.199f, 448.91748f, 180.8847f, (byte) 90);
	}

	private void spawnChests(Npc npc) {
		if (!rewarded) {
			rewarded = true; //safety mechanism
			if (npc.getAi2().getRemainigTime() != 0) {
				long rtime = (600000 - npc.getAi2().getRemainigTime()) / 30000;
					spawn(702038, 478.7917f, 815.5538f, 199.70894f, (byte) 8);
					if (rtime > 1)
						spawn(702038, 471, 853, 199f, (byte) 115);
					if (rtime > 2)
						spawn(702038, 477, 873, 199.7f, (byte) 109);
					if (rtime > 3)
						spawn(702038, 507, 899, 199.7f, (byte) 96);
					if (rtime > 4)
						spawn(702038, 548, 901, 199.7f, (byte) 83);
					if (rtime > 5)
						spawn(702038, 565, 889, 199.7f, (byte) 76);
					if (rtime > 6)
						spawn(702038, 585, 855, 199.7f, (byte) 63);
					if (rtime > 7)
						spawn(702038, 578, 874, 199.7f, (byte) 11);
					if (rtime > 8)
						spawn(702038, 528, 903, 199.7f, (byte) 30);
					if (rtime > 9)
						spawn(702038, 490, 899, 199.7f, (byte) 44);
					if (rtime > 10)
						spawn(702038, 470, 834, 199.7f, (byte) 63);
					if (rtime > 11 && npc.getNpcId() == 233719)
						spawn(702039, 576.8508f, 836.40424f, 199.7f, (byte) 44);
			}
		}
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int itemId = 0;
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 832865:
				dropItems.clear();
				switch (Rnd.get(1, 3)) {
					case 1:
						itemId = 188052742;
						break;
					case 2:
						itemId = 188052485;
						break;
					case 3:
						itemId = 188053450;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 1));
				break;
			}
		}

    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        return TeleportService2.teleportTo(player, mapId, instanceId, 527.6408f, 109.9414f, 175.50763f, (byte) 75);
    }
}
