package ai.instance.hallOfKnowledge;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;

/**
 * @author Everlight
 */
@AIName("ancient_runish_codex")
public class AncientRunishCodexAI2 extends ActionItemNpcAI2 {

    private boolean isRewarded;

    @Override
    protected void handleDialogStart(Player player) {
        InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
        if (instance != null && !instance.isStartProgress()) {
            return;
        }
        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (!isRewarded) {
            isRewarded = true;
            AI2Actions.handleUseItemFinish(this, player);
            final int npcId = getNpcId();
            if (npcId == 831145 && npcId == 831146 && npcId == 831147) {
            }
            AI2Actions.deleteOwner(this);
        }
    }
}
