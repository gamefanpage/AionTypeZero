package instance;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@InstanceID(301000000)
public class TestSubjectPrisonInstance extends GeneralInstanceHandler {

    @Override
    public void onDie(Npc npc) {
        switch (npc.getObjectTemplate().getTemplateId()) {
            case 206288:
            case 206290:
            case 231067:
            case 231068:
            case 231069:
		despawnNpc(npc);
		break;
        }
    }

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

    @Override
    public boolean onDie(final Player player, Creature lastAttacker) {
        Summon summon = player.getSummon();
        if (summon != null) {
            summon.getController().release(UnsummonType.UNSPECIFIED);
        }
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
