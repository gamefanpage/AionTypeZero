package ai.classNpc;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Romanz
 *
 */
@AIName("drakan_fi")
public class DrakanFighterAI2 extends AggressiveNpcAI2 {

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
		if (hpPercentage > 98 && percents.size() < 5) {
			addPercent();
		}

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
			SkillEngine.getInstance().getSkill(getOwner(), 17302, 65, target).useNoAnimationSkill();
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17292, 65, target).useNoAnimationSkill();
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17293, 65, target).useNoAnimationSkill();
		}
	}
	private void skill4() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17294, 65, target).useNoAnimationSkill();
		}
	}
	private void skill5() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17295, 65, target).useNoAnimationSkill();
		}
	}
	private void skill6() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17300, 65, target).useNoAnimationSkill();
		}
	}
	private void skill7() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 17310, 65, target).useNoAnimationSkill();
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
