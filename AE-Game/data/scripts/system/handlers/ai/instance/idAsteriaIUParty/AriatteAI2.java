package ai.instance.idAsteriaIUParty;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.controllers.observer.GaleCycloneObserver;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import javolution.util.FastMap;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("ariatte")
public class AriatteAI2 extends NpcAI2 {

	private FastMap<Integer, GaleCycloneObserver> observed = new FastMap<Integer, GaleCycloneObserver>().shared();
	private boolean blocked;

	@Override
	protected void handleCreatureSee(Creature creature) {
		if (blocked) {
			return;
		}
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			final GaleCycloneObserver observer = new GaleCycloneObserver(player, getOwner()) {

				@Override
				public void onMove() {
					if (!blocked) {
						SkillEngine.getInstance().getSkill(getOwner(), 21467, 65, player).useNoAnimationSkill();
					}
				}

			};
			player.getObserveController().addObserver(observer);
			observed.put(player.getObjectId(), observer);
		}
	}

	@Override
	protected void handleCreatureNotSee(Creature creature) {
		if (blocked) {
			return;
		}
		if (creature instanceof Player) {
			Player player = (Player) creature;
			Integer obj = player.getObjectId();
			GaleCycloneObserver observer = observed.remove(obj);
			if (observer != null) {
				player.getObserveController().removeObserver(observer);
			}
		}
	}

	@Override
	protected void handleDied() {
		clear();
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		clear();
		super.handleDespawned();
	}

	private void clear() {
		blocked = true;
		for (Integer obj : observed.keySet()) {
			Player player = getKnownList().getKnownPlayers().get(obj);
			GaleCycloneObserver observer = observed.remove(obj);
			if (player != null) {
				player.getObserveController().removeObserver(observer);
			}
		}
	}
}
