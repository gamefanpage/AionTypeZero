package ai.instance.idAsteriaIUParty;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision M.O.G. Devs Team
 */

@AIName("makekike")
public class MakekikeAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		addPercent();
		Gossip_19();
		super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(MakekikeAI2.this);
            }
        }, 90000);
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 80 && percents.size() < 6) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 80:
						SpawnBlueBox();
						break;
					case 70:
						Gossip_13();
						break;
					case 40:
                        Gossip_17();
						break;
					case 30:
                        SpawnRedBox();
						break;
					case 10:
						Gossip_04();
						break;
					case 5:
						Gossip_05();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] { 80, 70, 40, 30, 10, 5 });
	}

	private void Gossip_21() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500998, getObjectId(), 0, 1000);
	}

	private void Gossip_22() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500999, getObjectId(), 0, 1000);
	}

	private void Gossip_23() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501000, getObjectId(), 0, 1000);
	}

	private void Gossip_13() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500990, getObjectId(), 0, 1000);
	}

	private void Gossip_17() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500994, getObjectId(), 0, 1000);
	}

	private void Gossip_04() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501002);
	}

	private void Gossip_19() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500996);
	}

	private void Gossip_05() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501003);
	}

	private void BoxOpen(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 65, getOwner()).useNoAnimationSkill();
	}

	private void SpawnBlueBox() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_21();
				spawn_blue_box();
			}
		}, 1000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_23();
				BoxOpen(21340);
			}
		}, 5000);
	}

	private void SpawnRedBox() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_21();
				spawn_red_box();
			}
		}, 1000);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Gossip_22();
				BoxOpen(21340);
			}
		}, 5000);
	}

	private void spawn_red_box() {
		spawn(831348, 539.05f, 552.27f, 198.87f, (byte) 60);
		spawn(831348, 512.92f, 552.13f, 198.87f, (byte) 60);
		spawn(831348, 521.76f, 548.68f, 198.75f, (byte) 60);
		spawn(831348, 513.17f, 577.76f, 198.86f, (byte) 60);
		spawn(831348, 521.97f, 583.61f, 198.85f, (byte) 60);
		spawn(831348, 507.65f, 560.18f, 198.87f, (byte) 60);
		spawn(831348, 507.42f, 570.53f, 198.78f, (byte) 60);
		spawn(831348, 543.32f, 572.56f, 198.79f, (byte) 60);
		spawn(831348, 530.72f, 549.67f, 198.79f, (byte) 60);
		spawn(831348, 536.98f, 578.31f, 198.83f, (byte) 60);
		spawn(831348, 529.46f, 582.23f, 198.67f, (byte) 60);
		spawn(831348, 542.64f, 560.79f, 198.83f, (byte) 60);
	}

	private void spawn_blue_box() {
		spawn(831347, 539.05f, 552.27f, 198.87f, (byte) 60);
		spawn(831347, 512.92f, 552.13f, 198.87f, (byte) 60);
		spawn(831347, 521.76f, 548.68f, 198.75f, (byte) 60);
		spawn(831347, 513.17f, 577.76f, 198.86f, (byte) 60);
		spawn(831347, 521.97f, 583.61f, 198.85f, (byte) 60);
		spawn(831347, 507.65f, 560.18f, 198.87f, (byte) 60);
		spawn(831347, 507.42f, 570.53f, 198.78f, (byte) 60);
		spawn(831347, 543.32f, 572.56f, 198.79f, (byte) 60);
		spawn(831347, 530.72f, 549.67f, 198.79f, (byte) 60);
		spawn(831347, 536.98f, 578.31f, 198.83f, (byte) 60);
		spawn(831347, 529.46f, 582.23f, 198.67f, (byte) 60);
		spawn(831347, 542.64f, 560.79f, 198.83f, (byte) 60);
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
