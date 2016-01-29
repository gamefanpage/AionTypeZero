package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
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

@AIName("vibaloka")
public class VibalokaAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		addPercent();
		Gossip_11();
		super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(VibalokaAI2.this);
            }
        },50000);

    }

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 70 && percents.size() < 3) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 70:
						SpawnSupport();
						break;
					case 10:
						Gossip_01();
						break;
					case 5:
						Gossip_02();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] { 70, 10, 5 });
	}

	private void Gossip_11() { // ������ �� ������!
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500988, getObjectId(), 0, 1000);
	}

	private void Gossip_09() { // ������-������!
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500986, getObjectId(), 0, 1000);
	}

	private void Gossip_10() { // �-�-�, ��� �������...
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500987, getObjectId(), 0, 1000);
	}

	private void Gossip_01() { // �������� ���������� ��� ���.
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500975);
	}

	private void Gossip_02() { // �������� ������ ������ � ��������.
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500976);
	}

	private void SpawnSupport() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_09();
				spawn_support();
			}
		}, 1000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_10();
			}
		}, 10000);
	}

	private void spawn_support() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(0, 10);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(233150, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
		spawn(233150, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
		spawn(233150, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
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
