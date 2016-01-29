package ai.events;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.services.NpcShoutsService;

/**
 *
 * @author Romanz
 */
@AIName("pig")
public class PigAI2 extends AggressiveNpcAI2 {

        @Override
        protected void handleSpawned() {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 390005, getObjectId(), 0, 0);
                super.handleSpawned();
        }

}
