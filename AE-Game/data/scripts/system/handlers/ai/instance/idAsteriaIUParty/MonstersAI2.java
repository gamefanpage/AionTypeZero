package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("id_asteria_iu_monster")
public class MonstersAI2 extends AggressiveNpcAI2 {

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
	}

	private void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 98:
						shout_start();
						break;
					case 50:
						Gossip_18();
						break;
					case 5:
						shout_died();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void shout_start() { // ��-��-��...
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501005, getObjectId(), 0, 1000);
	}

	private void Gossip_18() { // ��� �����?!
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500995, getObjectId(), 0, 1000);
	}

	private void shout_died() { // �� ��� ������!
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501006, getObjectId(), 0, 1000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] { 98, 50, 5 });
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
