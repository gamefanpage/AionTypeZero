package ai.classNpc;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Romanz
 *
 */

@AIName("warrior")
public class WarriorAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{98, 90, 80, 70, 60, 50, 40, 30, 20, 15, 5});
	}

	private void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 98:
						skill1();
						break;
					case 90:
						skill2();
						break;
					case 80:
						skill3();
						break;
					case 70:
						skill4();
						break;
					case 60:
						skill1();
						break;
					case 50:
						skill2();
						break;
					case 40:
						skill3();
						break;
					case 30:
						skill4();
						break;
					case 15:
						skill5();
						break;
					case 5:
						skill1();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	@Override
	protected void handleSpawned() {
		addPercent();
		super.handleSpawned();
	}

	private void skill1() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17059, getOwner().getLevel());
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17061, getOwner().getLevel());
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17230, getOwner().getLevel());
		}
	}
	private void skill4() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17386, getOwner().getLevel());
		}
	}
	private void skill5() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17083, getOwner().getLevel());
		}
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
