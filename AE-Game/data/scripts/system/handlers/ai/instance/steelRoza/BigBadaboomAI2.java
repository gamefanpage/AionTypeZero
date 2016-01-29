package ai.instance.steelRoza;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;

/**
 *
 * @author xXMashUpXx
 *
 */
@AIName("big_badaboom")
public class BigBadaboomAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        player.getController().stopProtectionActiveTask();
        int morphSkill = 0;
        switch (getNpcId()) {
            case 231016: //Big Badaboom.
            case 231017: //Bigger Badaboom.
                morphSkill = 0x4E502E;
                break;
        }
        SkillEngine.getInstance().getSkill(getOwner(), morphSkill >> 8, morphSkill & 0xFF, player).useNoAnimationSkill();
        AI2Actions.deleteOwner(this);
    }
}
