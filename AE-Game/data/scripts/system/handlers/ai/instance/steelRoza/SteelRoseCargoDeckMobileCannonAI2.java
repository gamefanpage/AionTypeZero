package ai.instance.steelRoza;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.List;

/**
 *
 * @author xXMashUpXx
 *
 */
@AIName("steelrosedeckmobilecannon")
public class SteelRoseCargoDeckMobileCannonAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        if (!player.getInventory().decreaseByItemId(185000052, 1)) {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111302));
            return;
        }
        WorldPosition worldPosition = player.getPosition();
        if (worldPosition.isInstanceMap()) {
            if (worldPosition.getMapId() == 301050000) { //Steel Rose Cargo 4.3
                WorldMapInstance worldMapInstance = worldPosition.getWorldMapInstance();
                killNpc(worldMapInstance.getNpcs(230727));
                killNpc(worldMapInstance.getNpcs(231460));
            }
        }
    }

    private void killNpc(List<Npc> npcs) {
        for (Npc npc : npcs) {
            AI2Actions.killSilently(this, npc);
        }
    }
}
