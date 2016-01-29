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
@AIName("ranger")
public class RangerAI2 extends AggressiveNpcAI2 {

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
						skill5();
						break;
					case 50:
						skill6();
						break;
					case 40:
						skill4();
						break;
					case 30:
						skill5();
						break;
					case 15:
						skill7();
						break;
					case 5:
						skill4();
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
			AI2Actions.useSkill(this, 17098, getOwner().getLevel());
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17099, getOwner().getLevel());
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17100, getOwner().getLevel());
		}
	}
	private void skill4() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17101, getOwner().getLevel());
		}
	}
	private void skill5() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17102, getOwner().getLevel());
		}
	}
	private void skill6() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17103, getOwner().getLevel());
		}
	}
	private void skill7() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			AI2Actions.useSkill(this, 17104, getOwner().getLevel());
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{98, 90, 80, 70, 60, 50, 40, 30, 20, 15, 5});
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
