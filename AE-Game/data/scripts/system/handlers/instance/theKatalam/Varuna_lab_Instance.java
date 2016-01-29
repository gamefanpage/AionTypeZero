package instance.theKatalam;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.controllers.effect.PlayerEffectController;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.*;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.*;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;
import java.util.*;
import java.util.concurrent.Future;

/*
*Author Romanz
*/

@InstanceID(301270000)
public class Varuna_lab_Instance extends GeneralInstanceHandler
{
    private long startTime;
    private int beritraKilled;
    private Future<?> TWDTask;
    private Future<?> instanceTimer;
    private Map<Integer, StaticDoor> doors;
    protected boolean isInstanceDestroyed = false;
    private List<Integer> movies = new ArrayList<Integer>();
    boolean moviePlayed = false;
    private boolean isStartTimer = false;
    private long startTimer;
    private Race spawnRace;
    private byte entrance = 0;



    @Override
    public void onEnterInstance(Player player) {
        entrance += 1;
        if (player.getRace() == Race.ELYOS) {
            if (entrance == 1) {
                spawn(206361, 347.9223f, 252.1834f, 311.36133f, (byte) 15);
                spawn(855087, 226.8177f, 256.8576f, 312.37897f, (byte) 0);
            }
        } else {
            if (entrance == 1) {
                spawn(206362, 347.9223f, 252.1834f, 311.36133f, (byte) 15);
                spawn(855088, 226.8177f, 256.8576f, 312.37897f, (byte) 0);
            }
        }
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("IDLDF4RE_01_ITEMUSEAREA_1")) {
            instance.doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(final Player player) {
                    if (player.isOnline()) {
                        if (isStartTimer) {
                            long time = System.currentTimeMillis() - startTime;
                            if (time < 1200000) {
                                PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 1200 - (int) time / 1000));
                            }
                        }
                        if (!isStartTimer) {
                            isStartTimer = true;
                            startTimer = System.currentTimeMillis();
                            PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 1200)); //20 Minutes.
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402453));
                        }
                        TWDTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                if (getNpc(233887) != null ) {
                                    despawnNpcs(instance.getNpcs(233887));
                                }
                                if (getNpc(233888) != null ) {
									despawnNpcs(instance.getNpcs(233888));
                                }
								if (getNpc(233889) != null ) {
									despawnNpcs(instance.getNpcs(233889));
                                }
								if (getNpc(233890) != null ) {
									despawnNpcs(instance.getNpcs(233890));
                                }
								if (getNpc(233891) != null ) {
									despawnNpcs(instance.getNpcs(233891));
                                }
								if (getNpc(233892) != null ) {
									despawnNpcs(instance.getNpcs(233892));
                                }
								if (getNpc(233893) != null ) {
									despawnNpcs(instance.getNpcs(233893));
                                }
								if (getNpc(233894) != null ) {
									despawnNpcs(instance.getNpcs(233894));
                                }
								if (getNpc(233895) != null ) {
									despawnNpcs(instance.getNpcs(233895));
                                }
								if (getNpc(233896) != null ) {
									despawnNpcs(instance.getNpcs(233896));
                                }
								if (getNpc(233897) != null ) {
									despawnNpcs(instance.getNpcs(233897));
                                }
							}
                        }, 1201000); //20 Minutes.
                    }
                }
            });
        }
    }

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                int itemId = 0;
				int count = 0;
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 234992:
			case 234993:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053238, 1));
				break;
			case 233887:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000196, 1));
				break;
			case 233891:
				dropItems.clear();
				switch (Rnd.get(1, 2)) {
					case 1:
						itemId = 164000291;
						break;
					case 2:
						itemId = 164000292;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 1));
				break;
			case 233888:
			case 233889:
			case 233890:
				dropItems.clear();
				switch (Rnd.get(1, 4)) {
					case 1:
						count = 6593;
						break;
					case 2:
						count = 13631;
						break;
					case 3:
						count = 15584;
						break;
					case 4:
						count = 19584;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 182400001, count));
				break;
			case 234990:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000236, 12));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000237, 20));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000119, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000120, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053331, 1));
				break;
			case 233898:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000236, 12));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000237, 30));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000119, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000120, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053332, 1));
				if (Rnd.get(1, 100) < 1) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053295, 1));
				}
				if (Rnd.get(1, 100) < 30) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 169405254, 1));
				}
				switch (Rnd.get(1, 2)) {
					case 1:
						itemId = 166100008;
						break;
					case 2:
						itemId = 166100011;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 20));
				break;
			case 234991:
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000236, 12));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 186000237, 40));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000119, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 162000120, 5));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053333, 1));
				if (Rnd.get(1, 100) < 2) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 188053295, 1));
				}
				if (Rnd.get(1, 100) < 80) {
				switch (Rnd.get(1, 3)) {
					case 1:
						itemId = 169405254;
						break;
					case 2:
						itemId = 152012600;
						break;
					case 3:
						itemId = 152012602;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 1));
				}
				switch (Rnd.get(1, 2)) {
					case 1:
						itemId = 166100008;
						break;
					case 2:
						itemId = 166100011;
						break;
				}
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, itemId, 25));
				break;

			}
		}

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 233892:
			case 233894:
			case 233895:
			case 233896:
			case 233897:
				despawnNpc(npc);
				break;
			case 234990:
			case 234991:
			case 233898:
					spawn(702338, 258.54f, 260.39f, 312.4f, (byte) 60);
				break;
			default:
				break;
		}
	}

    private void removeEffects(Player player) {
        PlayerEffectController effectController = player.getEffectController();
        effectController.removeEffect(0);
    }

    private void sendMsg(final String str) {
        instance.doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                PacketSendUtility.sendMessage(player, str);
            }
        });
    }

    @Override
    public void onPlayerLogOut(Player player) {
        removeEffects(player);
    }

    @Override
    public void onLeaveInstance(Player player) {
        removeEffects(player);
    }

    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }

    @Override
    public void onExitInstance(Player player) {
        TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
    }

    private void deleteNpc(int npcId) {
        if (getNpc(npcId) != null) {
            getNpc(npcId).getController().onDelete();
        }
    }

    protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    protected void despawnNpcs(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().onDelete();
        }
    }

    @Override
    protected Npc getNpc(int npcId) {
        if (!isInstanceDestroyed) {
            return instance.getNpc(npcId);
        }
        return null;
    }

    protected List<Npc> getNpcs(int npcId) {
        if (!isInstanceDestroyed) {
            return instance.getNpcs(npcId);
        }
        return null;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        return TeleportService2.teleportTo(player, mapId, instanceId, 364.14f, 259.84f, 311.4f, (byte) 60);
    }

    @Override
    public boolean onDie(final Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
