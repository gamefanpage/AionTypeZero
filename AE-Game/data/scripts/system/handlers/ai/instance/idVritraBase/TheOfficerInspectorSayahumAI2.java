package ai.instance.idVritraBase;

/*
 * Sayahum ID Vritra Base
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

@AIName("sayahum")
public class TheOfficerInspectorSayahumAI2 extends AggressiveNpcAI2 {

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
						skill2();
						break;
					case 95:
						skill3();
						break;
					case 80:
						skill2();
						break;
					case 70:
						skill3();
						shout1();
						break;
					case 60:
						skill2();
						break;
					case 50:
						shout1();
						skill3();
						break;
					case 40:
						skill2();
						break;
					case 30:
						skill3();
						shout2();
						break;
					case 20:
						skill2();
						break;
					case 10:
						skill3();
						break;
					case 5:
						skill2();
						shout_died();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17228, 65, target).useNoAnimationSkill();
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 18334, 65, target).useNoAnimationSkill();
		}
	}

	private void shout_start() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501068, getObjectId(), 0, 1000);
	}
	private void shout1() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501069, getObjectId(), 0, 1000);
	}
	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501070, getObjectId(), 0, 1000);
	}
	private void shout_died() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501071, getObjectId(), 0, 1000);
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

