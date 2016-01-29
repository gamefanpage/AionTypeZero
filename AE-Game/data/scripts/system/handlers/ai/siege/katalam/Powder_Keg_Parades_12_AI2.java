package ai.siege.katalam;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

@AIName("power_keg_parades_12")
public class Powder_Keg_Parades_12_AI2 extends GeneralNpcAI2 {

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
                BigBoxActive();
                Message();
	}

	private void BigBoxActive() {
		Npc boxBigBomb = getPosition().getWorldMapInstance().getNpc(701720);
		if (boxBigBomb != null && !boxBigBomb.getLifeStats().isAlreadyDead()) {
			spawn(701721, boxBigBomb.getX(), boxBigBomb.getY(), boxBigBomb.getZ(), boxBigBomb.getHeading());
			spawn(701724, boxBigBomb.getX(), boxBigBomb.getY(), boxBigBomb.getZ(), boxBigBomb.getHeading());
			boxBigBomb.getController().onDelete();
		}
	}

	private void Message() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401693));
			}
		});
	}
}
