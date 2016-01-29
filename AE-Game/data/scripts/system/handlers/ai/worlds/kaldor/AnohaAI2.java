package ai.worlds.kaldor;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.DiedEventHandler;
import org.typezero.gameserver.controllers.attack.AggroInfo;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;


/**
 * @author Romanz
 *
 */
@AIName("anoha")
public class AnohaAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402503));
			}
		});
		despawn();
	}

    @Override
    protected void handleDied() {
            for (AggroInfo damager : this.getAggroList().getList()) {
            if (damager.getAttacker() instanceof Player) {
                ((Player) damager.getAttacker()).getAbyssRank().addAGp(0,200);
                PacketSendUtility.sendPacket((Player) damager.getAttacker(), SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_GAIN(200));
                PacketSendUtility.sendPacket((Player) damager.getAttacker(), new SM_ABYSS_RANK(((Player) damager.getAttacker()).getAbyssRank()));
                }
            }
			announceKill();
            DiedEventHandler.onDie(this);
    }

	private void announceKill() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
		@Override
		public void visit(Player player) {
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402504));
			}
		});
	}

	private void announceFail() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
		@Override
		public void visit(Player player) {
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402505));
			}
		});
	}

	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(AnohaAI2.this);
					announceFail();
				}
			}
		}, 30*60*1000);
	}
}
