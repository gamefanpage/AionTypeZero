package ai.events;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.mail.SystemMailService;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.List;
import org.joda.time.DateTime;
/**
 * @author Romanz
 *
 */

@AIName("rvr_boss")
public class RvrBossAI2 extends AggressiveNpcAI2 {

   @Override
   protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		//add player to event list for additional reward
		if (creature instanceof Player && (int)getPosition().getMapId() == 600010000) {
			SiegeService.getInstance().checkRvrPlayerOnEvent((Player)creature);
		}
		//TODO Spawn defensive guards (only for bosses in silentera Canyon)
	}

   @Override
   protected void handleDied() {
		super.handleDied();
		despawnEnemyBoss();
		scheduleRespawn();
		performPartecipationReward();
   }

 	//despawn enemy boss (only for silentera)
	private void despawnEnemyBoss() {
		if ((int)getPosition().getMapId() == 600010000) {
			WorldMapInstance instance = getPosition().getWorldMapInstance();
			deleteNpcs(instance.getNpcs(getNpcId() == 219641 ? 219642 : 219641 ));
		}
	}

	//schedule respawn of both silentera bosses (bosses in other maps must not respawn)
	private void scheduleRespawn() {
		if ((int)getPosition().getMapId() == 600010000) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawn(219641, 658.7087f, 795.21857f, 293.14087f, (byte) 7);
					spawn(219642, 657.95105f, 737.5624f, 293.19818f, (byte) 0);

				}
			}, 43200000);
		}
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null)
				npc.getController().onDelete();
		}
	}

	private void performPartecipationReward() {
		if ((int)getPosition().getMapId() == 600010000) {
			DateTime now = DateTime.now();
			int hour = now.getHourOfDay();
			if (hour >= 19 && hour <= 23) {
				List<Player> eventPlayerList = SiegeService.getInstance().getRvrPlayersOnEvent();
				for (Player rewardedPlayer: eventPlayerList) {
					SystemMailService.getInstance().sendMail("EventService", rewardedPlayer.getName(), "EventReward", "Medal",
						186000147, 1, 0, LetterType.NORMAL);
				}
			}
			SiegeService.getInstance().clearRvrPlayersOnEvent();
		}
	}

	@Override
	protected void handleDespawned() {
	  super.handleDespawned();
	}
}
