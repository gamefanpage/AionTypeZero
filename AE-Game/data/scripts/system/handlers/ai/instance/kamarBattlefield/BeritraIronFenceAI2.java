package ai.instance.kamarBattlefield;

import ai.NoActionAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;

/**
 *
 * @author Steve
 */
@AIName("beritraironfence")
public class BeritraIronFenceAI2 extends NoActionAI2
{

    @Override
    public boolean canThink()
    {
        return false;
    }

    @Override
    public void handleDied()
    {
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }
}
