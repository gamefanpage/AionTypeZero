package instance;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.*;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Romanz
 */
@InstanceID(300610000)
public class RaksaSoloInstance extends GeneralInstanceHandler {


    private byte entrance = 0;
    private int Door1;
    private int Door2;
    private Map<Integer, StaticDoor> doors;
    private List<Integer> movies = new ArrayList<Integer>();
    private boolean isInstanceDestroyed;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}

    @Override
    public void onEnterInstance(Player player) {
        entrance += 1;
        if (player.getRace() == Race.ELYOS) {
            //PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 498));
            if (entrance == 1) {
                spawn(206380, 818.04f, 932.1826f, 1207.4312f, (byte) 13);
            }
        } else {
            //PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 499));
            if (entrance == 1) {
                spawn(206397, 818.04f, 932.1826f, 1207.4312f, (byte) 13);
            }
        }
    }

    @Override
	public void onDie(Npc npc) {
		Creature master = npc.getMaster();
		if (master instanceof Player)
			return;
		if (isInstanceDestroyed) {
			return;
		}
		 Player player = npc.getAggroList().getMostPlayerDamage();
		 int npcId = npc.getNpcId();
		switch (npc.getNpcId()) {
            case 236304:
				openDoor(118);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402781));
                break;
            case 236306:
				spawn(730445, 619.643f, 685.1399f, 522.0487f, (byte) 0);
                break;
        }
     switch (npcId) {
    	 case 236074:
    	 case 236075:
    	 case 236076:
	     Door1 ++;
			 if (Door1 >= 1 && Door1 <= 14) {
				}
			 else if (Door1 == 15) {
				openDoor(457);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402781));
			 }
				despawnNpc(npc);
			 break;
	    }
     switch (npcId) {
    	 case 236077:
    	 case 236078:
    	 case 236079:
	     Door2 ++;
			 if (Door2 >= 1 && Door2 <= 14) {
				}
			 else if (Door2 == 15) {
				openDoor(64);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402781));
			 }
				despawnNpc(npc);
			 break;
	    }
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 702673:
				spawnMobs1_1();
				spawnMobs1_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
            case 702674:
				spawnMobs2_1();
				spawnMobs2_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
            case 702675:
				spawnMobs3_1();
				spawnMobs3_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
            case 702690:
				spawnMobs4_1();
				spawnMobs4_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
            case 702691:
				spawnMobs5_1();
				spawnMobs5_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
            case 702692:
				spawnMobs6_1();
				spawnMobs6_2();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402780));
				despawnNpc(npc);
                break;
        }
    }

	private void spawnMobs1_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236074, 967.5338f, 793.4503f, 734.0461f, (byte) 67);
					spawn(236074, 967.8346f, 787.9694f, 734.0461f, (byte) 67);
				}

			}, 3000);
	}
	private void spawnMobs1_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236074, 962.92f, 795.84f, 734.69f, (byte) 93);
					spawn(236074, 961.4771f, 790.228f, 734.0461f, (byte) 67);
					spawn(236074, 963.3557f, 784.2463f, 734.0461f, (byte) 26);
				}

			}, 10000);
	}
	private void spawnMobs2_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236075, 954.2308f, 770.0457f, 734.05475f, (byte) 8);
					spawn(236075, 953.5185f, 772.9017f, 734.05475f, (byte) 8);
				}

			}, 3000);
	}
	private void spawnMobs2_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236075, 970.5629f, 779.24f, 734.05475f, (byte) 67);
					spawn(236075, 971.4633f, 775.5289f, 734.05475f, (byte) 67);
					spawn(236075, 963.4158f, 772.5471f, 734.05475f, (byte) 37);
				}

			}, 10000);
	}
	private void spawnMobs3_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236074, 947.1217f, 775.2617f, 734.0187f, (byte) 67);
					spawn(236075, 947.6797f, 769.5505f, 734.05475f, (byte) 31);
				}

			}, 3000);
	}
	private void spawnMobs3_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236074, 946.0414f, 778.3027f, 734.0187f, (byte) 67);
					spawn(236075, 943.3198f, 781.9261f, 734.0187f, (byte) 90);
					spawn(236076, 942.0587f, 776.4103f, 734.0187f, (byte) 0);
				}

			}, 10000);
	}
	private void spawnMobs4_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236077, 985.2942f, 882.7517f, 762.51196f, (byte) 105);
					spawn(236077, 987.7871f, 884.1927f, 762.55774f, (byte) 100);
				}

			}, 3000);
	}
	private void spawnMobs4_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236077, 996.8099f, 876.8442f, 762.55774f, (byte) 67);
					spawn(236077, 995.7424f, 874.3123f, 762.55774f, (byte) 31);
					spawn(236077, 991.2494f, 876.9168f, 762.55774f, (byte) 15);
				}

			}, 10000);
	}
	private void spawnMobs5_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236078, 1009.711f, 890.2547f, 762.55774f, (byte) 45);
					spawn(236078, 1008.535f, 893.6851f, 762.55774f, (byte) 77);
				}

			}, 3000);
	}
	private void spawnMobs5_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236078, 1001.002f, 896.0778f, 762.55774f, (byte) 105);
					spawn(236078, 1000.064f, 890.5679f, 762.89075f, (byte) 15);
					spawn(236078, 1003.171f, 888.0419f, 762.89075f, (byte) 15);
				}

			}, 10000);
	}
	private void spawnMobs6_1() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236077, 991.5158f, 899.8228f, 762.55774f, (byte) 105);
					spawn(236077, 991.5474f, 894.6733f, 762.55774f, (byte) 15);
				}

			}, 3000);
	}
	private void spawnMobs6_2() {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					spawn(236078, 996.4833f, 902.6909f, 762.55774f, (byte) 77);
					spawn(236078, 999.243f, 900.2877f, 762.55774f, (byte) 77);
					spawn(236079, 994.3097f, 898.9786f, 762.55774f, (byte) 105);
				}

			}, 10000);
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 702694:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 164000342, 20));
				break;
			}
		}

	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

    @Override
    public void onLeaveInstance(Player player) {
        super.onLeaveInstance(player);
    }

    @Override
    public void onExitInstance(Player player) {
        super.onExitInstance(player);
    }

    @Override
    public void onPlayerLogOut(Player player) {
        super.onPlayerLogOut(player);
    }

    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
                movies.clear();
	}

    @Override
    public boolean onDie(final Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        PacketSendUtility.sendPacket(player, STR_REBIRTH_MASSAGE_ME);
        TeleportService2.teleportTo(player, mapId, instanceId, 835.6f, 939.2f, 1207.5f, (byte) 66);
        return true;
    }
}
