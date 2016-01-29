/*
 * Commander Race Eternal Bastion
 */

package ai.instance.eternalBastion;


import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.AggroEventHandler;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("commander_bastion")
public class CommanderAI2 extends AggressiveNpcAI2 {

    int attackCount;

    private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		addPercent();
		super.handleSpawned();
        }

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
        attackCount++;
        if(attackCount == 20) {
            AggroEventHandler.onAggro(this, creature);
        }
    }

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 98 && percents.size() < 9) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 98:
						shout_attack();
						break;
					case 80:
						shout_attack();
						break;
					case 70:
						shout_attack();
						break;
					case 60:
						shout_attack();
						break;
					case 50:
						shout_attack();
						break;
					case 40:
						shout_attack();
						break;
					case 30:
						shout_attack();
						break;
					case 20:
						shout_attack();
						break;
					case 5:
						shout_attack();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void shout_attack() { // MSG Notice 05
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401827));
			}
		});
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{98, 80, 70, 60, 50, 40, 30, 20, 5});
	}


    @Override
	protected void handleDespawned() {
		percents.clear();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		super.handleDied();
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		super.handleBackHome();
	}

}
