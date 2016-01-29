package instance.theKatalam;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.*;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

@InstanceID(300590000)
public class BridgeErmungandInstance extends GeneralInstanceHandler {

    private int Portaldevice;
    private boolean isInstanceDestroyed;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
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
            case 231055:
                despawnNpc(npc);
                break;
            case 235768:
                spawn(730868, 315.8804f, 488.6518f, 608.7385f, (byte) 0);
                break;

	    }

     switch (npcId) {
    	 case 235774:
    	 case 235775:
    	 case 235776:
    	 case 235777:
	     Portaldevice ++;
			 if (Portaldevice == 1) {
				}
			 else if (Portaldevice == 2) {
				}
			 else if (Portaldevice == 3) {
			 }
			 else if (Portaldevice == 4) {
				spawn(731544, 436.19977f, 496.52478f, 605.33521f, (byte) 1, 7);
				sendMsg(1401233);
			 }
			 break;
	    }

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
