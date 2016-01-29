package ai.instance.idVritraBase;

/*
 * Kurmata ID Vritra Base
 */

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("kurmata")
public class ChiefGunnerKurmataAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		addPercent();
		VritraBless(3000);
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 98 && percents.size() < 11) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 98:
						shout_start();
						skill1();
						break;
					case 95:
						shout1();
						shout2();
						skill2();
						break;
					case 80:
						skill1();
						break;
					case 70:
						shout1();
						shout2();
						skill2();
						break;
					case 60:
						skill3();
						break;
					case 50:
						shout1();
						shout2();
						skill2();
						break;
					case 40:
						shout3();
						skill1();
						break;
					case 30:
						shout1();
						shout2();
						skill2();
						break;
					case 20:
						skill1();
						break;
					case 10:
						shout1();
						shout2();
						skill2();
						break;
					case 5:
						shout_died();
						skill3();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	private void skill1() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21142, 65, target).useNoAnimationSkill();
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21292, 65, target).useNoAnimationSkill();
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21293, 65, target).useNoAnimationSkill();
		}
	}
	private void shout_start() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501048, getObjectId(), 0, 1000);
	}
	private void shout1() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501049, getObjectId(), 0, 1000);
	}
	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501050, getObjectId(), 0, 3000);
	}
	private void shout3() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501051, getObjectId(), 0, 1000);
	}
	private void shout_died() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501052, getObjectId(), 0, 1000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{98, 95, 80, 70, 60, 50, 40, 30, 20, 10, 5});
	}

	private void VritraGeneralBless(int skillId) {
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
		}

	private void VritraBless(final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

	@Override
	public void run() {
			if (time == 3000) {
				VritraGeneralBless(21135);
			    }
		    }
    }, time);
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

