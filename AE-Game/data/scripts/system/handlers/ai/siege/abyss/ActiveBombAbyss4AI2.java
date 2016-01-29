package ai.siege.abyss;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

@AIName("active_bomb_abyss4")
public class ActiveBombAbyss4AI2 extends ActionItemNpcAI2 {

	@Override
	public void handleUseItemFinish(Player player) {
		BombActive();
		AI2Actions.deleteOwner(this);

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402732));
			}
		});
	}

	private void BombActive() {
		Npc boxBomb = getPosition().getWorldMapInstance().getNpc(297602);
		if (boxBomb != null && !boxBomb.getLifeStats().isAlreadyDead()) {
			spawn(297603, boxBomb.getX(), boxBomb.getY(), boxBomb.getZ(), boxBomb.getHeading());
			boxBomb.getController().onDelete();
		}
	}
}
