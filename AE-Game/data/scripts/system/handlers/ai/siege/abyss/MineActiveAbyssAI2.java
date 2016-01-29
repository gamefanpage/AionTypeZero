package ai.siege.abyss;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

@AIName("mine_active_abyss")
public class MineActiveAbyssAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		activate();
                despawn();
	}

	private void activate() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
                            announce();
                            SkillEngine.getInstance().getSkill(getOwner(), 21855, 1, getOwner()).useSkill();
			}
		}, 500);
	}

	private void announce() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402733));
			}
		});
	}

	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(MineActiveAbyssAI2.this);
				}
			}
		}, 1500);
	}

}
