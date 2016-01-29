package ai.events;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;


/**
 * @author Romanz
 *
 */
@AIName("hulking_bunny")
public class HulkingBunnyAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleDied() {
		if (Rnd.get(1, 100) < 15) {
			spawn(702313, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
		}
		super.handleDied();
	}
}
