/*
 * Slot ID Vritra Base
 */

package ai.instance.idVritraBase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("slot")
public class DefenderOfTheGateSlotAI2 extends AggressiveNpcAI2 {


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
		if (hpPercentage > 98 && percents.size() < 4) {
			addPercent();
		}

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 98:
						shout_start();
						break;
					case 50:
						shout1();
						break;
					case 30:
						shout2();
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

	private void shout_start() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501057, getObjectId(), 0, 1000);
	}
	private void shout1() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501058, getObjectId(), 0, 1000);
	}
	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501059, getObjectId(), 0, 1000);
	}
	private void shout_died() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1501060, getObjectId(), 0, 1000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{98, 50, 30, 5});
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
