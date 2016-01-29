package instance.theKatalam;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.*;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Romanz
 */
@InstanceID(301320000)
public class UnstableBridgeErmungandInstance extends GeneralInstanceHandler {

    private Future<?> fireTask;
    private byte entrance = 0;
    private int Powerdevice;
    private Map<Integer, StaticDoor> doors;
    private List<Integer> movies = new ArrayList<Integer>();
	private boolean isInstanceDestroyed;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(47).setOpen(false);
		Npc npc = instance.getNpc(231050);
		if (npc != null) {
			SkillEngine.getInstance().getSkill(npc, 21438, 1, npc).useNoAnimationSkill();
		}
	}

    @Override
    public void onEnterInstance(Player player) {
        entrance += 1;
        if (player.getRace() == Race.ELYOS) {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 498));
            if (entrance == 1) {
                spawn(801763, 740.7189f, 536.31616f, 575.6878f, (byte) 1);
            }
        } else {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 499));
            if (entrance == 1) {
                spawn(801765, 740.7189f, 536.31616f, 575.6878f, (byte) 1);
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
            case 230413:
                sendMsg(1401904);
                spawn(230417, 667.11389f, 474.22995f, 600.64978f, (byte) 0);
                despawnNpc(npc);
                break;
            case 230414:
                sendMsg(1401905);
                spawn(230418, 524.84589f, 427.63959f, 621.30267f, (byte) 0);
                despawnNpc(npc);
                break;
            case 230415:
                sendMsg(1401906);
                spawn(230419, 602.73395f, 556.29407f, 591.61957f, (byte) 0);
                despawnNpc(npc);
                break;
            case 230416:
                sendMsg(1401907);
                spawn(230420, 476.99838f, 523.91351f, 598.40454f, (byte) 0);
                despawnNpc(npc);
                break;
            case 231050: //The Vera Siege Ballista.
                fireTask.cancel(true);
                spawn(730868, 350.18478f, 490.73065f, 606.34015f, (byte) 1); //Ophidan Bridge Exit.
                instance.doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        if (player.isOnline()) {
                            PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
                        }
                    }
                });
            break;
            case 231052:
                despawnNpc(npc);
				Npc boss = instance.getNpc(231050);
				boss.getEffectController().removeEffect(21438);
            break;
            case 231055: //Balic Defence Wall.
                despawnNpc(npc);
                startFinalTimer();
                instance.doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        if (player.isOnline()) {
                            PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 1800)); //30 Minutes.
                        }
                    }
                });
            break;
            case 231056: //Surkana Defence Wall.
                despawnNpc(npc);
            break;
            case 701644:
                    doors.get(47).setOpen(true);
                    despawnNpc(npc);
                    despawnNpc(instance.getNpc(701155));
                break;
        }
     switch (npcId) {
    	 case 230413:
    	 case 230414:
    	 case 230415:
    	 case 230416:
	     Powerdevice ++;
			 if (Powerdevice == 1) {
				}
			 else if (Powerdevice == 2) {
				}
			 else if (Powerdevice == 3) {
			 }
			 else if (Powerdevice == 4) {
				 spawn(701644, 435.42862f, 496.41296f, 604.8871f, (byte) 1);
			 }
			 break;
	    }
    }

    private void startFinalTimer() {
        sendMsg(1401892);
        this.sendMessage(1401875, 15 * 60 * 1000);
        this.sendMessage(1401876, 20 * 60 * 1000);
        this.sendMessage(1401877, 25 * 60 * 1000);
        fireTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                sendMsg(1401878);
                final Npc vera = instance.getNpc(231050); //The Vera Siege Ballista.
                if (vera != null) {
                    SkillEngine.getInstance().getSkill(vera, 21442, 60, vera).useNoAnimationSkill();
                }
            }
        }, 26 * 60 * 1000);
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

    private void sendMessage(final int msgId, long delay) {
        if (delay == 0) {
            this.sendMsg(msgId);
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                public void run() {
                    sendMsg(msgId);
                }
            }, delay);
        }
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
        TeleportService2.teleportTo(player, mapId, instanceId, 749.6575f, 558.4166f, 572.97394f, (byte) 90);
        return true;
    }
}
