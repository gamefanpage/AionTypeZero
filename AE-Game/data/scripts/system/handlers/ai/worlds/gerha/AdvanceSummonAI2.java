package ai.worlds.gerha;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;


/**
 * @author Romanz
 *
 */
@AIName("advance_summon")
public class AdvanceSummonAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402540));
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402602));
			}
		});
		despawn();
	}

	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(AdvanceSummonAI2.this);
				}
			}
		}, 600000);
	}
}
