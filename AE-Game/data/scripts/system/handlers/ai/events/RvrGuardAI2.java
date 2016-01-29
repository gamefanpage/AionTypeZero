package ai.events;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.SiegeService;
import org.joda.time.DateTime;

/**
 * @author Romanz
 *
 */

@AIName("rvr_guard")
public class RvrGuardAI2 extends AggressiveNpcAI2 {

   @Override
   protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		//add player to event list for additional reward
		if (creature instanceof Player && (int)getPosition().getMapId() == 600010000) {
			DateTime now = DateTime.now();
			int hour = now.getHourOfDay();
			if (hour >= 19 && hour <= 23) {
				Npc bossAsmo = getPosition().getWorldMapInstance().getNpc(219641);
				Npc bossElyos = getPosition().getWorldMapInstance().getNpc(219642);
				if (bossAsmo != null && bossElyos != null) {
					SiegeService.getInstance().checkRvrPlayerOnEvent((Player)creature);
				}
			}
		}
	}
}
