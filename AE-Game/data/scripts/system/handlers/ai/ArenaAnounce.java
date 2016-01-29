package ai;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.handler.TalkEventHandler;
import org.typezero.gameserver.controllers.observer.GaleCycloneObserver;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;


@AIName("arena_an")
public class ArenaAnounce extends NpcAI2 {

	private FastMap<Integer, GaleCycloneObserver> observed = new FastMap<Integer, GaleCycloneObserver>().shared();
	private boolean blocked;

	@Override
	protected void handleDialogStart(Player player) {
		TalkEventHandler.onTalk(this, player);
	}

	@Override
	protected void handleDialogFinish(Player creature) {
		TalkEventHandler.onFinishTalk(this, creature);
	}

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
                        String[] message = {
						"" + player.getName() + " , " + MuiService.getInstance().getMessage("ARENA_AN1"),
						"" + player.getName() + " , " + MuiService.getInstance().getMessage("ARENA_AN2"),
						"" + player.getName() + " , " + MuiService.getInstance().getMessage("ARENA_AN3"),
						"" + player.getName() + " , " + MuiService.getInstance().getMessage("ARENA_AN4"),
						"" + MuiService.getInstance().getMessage("ARENA_AN5") + " , " + player.getName()+ ".",
						"" + MuiService.getInstance().getMessage("ARENA_AN6") + " , " + player.getName()+ "?",
						};
                        PacketSendUtility.broadcastPacket(getOwner(), new SM_MESSAGE(getObjectId(), getOwner().getName(), message[Rnd.get(message.length)], ChatType.NORMAL));
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
