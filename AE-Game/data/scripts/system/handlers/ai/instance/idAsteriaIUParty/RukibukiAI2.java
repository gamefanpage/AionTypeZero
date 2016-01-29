package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.world.WorldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("rukibuki")
public class RukibukiAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		addPercent();
		Gossip_06();
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 80 && percents.size() < 4) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 80:
						Gossip_15();
						break;
					case 70:
						SpawnRukibuki();
						break;
					case 30:
						SpawnSupport();
						break;
					case 5:
						Gossip_16();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] { 80, 70, 30, 5 });
	}

	private void Gossip_06() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500983, getObjectId(), 0, 1000);
	}

	private void Gossip_14() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500991, getObjectId(), 0, 1000);
	}

	private void Gossip_15() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500992, getObjectId(), 0, 1000);
	}

	private void Gossip_16() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500993, getObjectId(), 0, 1000);
	}

	private void SpawnRukibuki() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_14();
				spawn_rukibuki();
			}
		}, 1000);
	}

	private void spawn_rukibuki() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(0, 10);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(233162, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
	}

	private void SpawnSupport() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(0, 10);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(233151, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
		spawn(233151, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
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
