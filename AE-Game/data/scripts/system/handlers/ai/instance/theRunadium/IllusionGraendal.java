package ai.instance.theRunadium;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * M.O.G. Devs Team
 */
@AIName("illusion_graendal")
public class IllusionGraendal extends AggressiveNpcAI2 {

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
		if (hpPercentage > 80 && percents.size() < 7) {
			addPercent();
		}
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 40:
						shout_mass();
						skill_mass_active();
					  break;
					case 41:
						skill_mass_destroy();
					  break;
					case 20:
						shout_spawn();
						spawn_support();
					  break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{40,41,20});
	}
	private void skill_mass_active() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21177, 65, target).useNoAnimationSkill();
		}
	}
	private void skill_mass_destroy() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21178, 65, target).useNoAnimationSkill();
		}
	}
	private void shout_mass() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500748, getObjectId(), 0, 1000);
	}
	private void shout_spawn() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500750, getObjectId(), 0, 1000);
	}
  private void spawn_support() {
 		spawn(284379, 264.6672f, 265.9347f, 241.8658f, (byte) 90); //������ �������� 1
  	spawn(284380, 264.5786f, 249.9355f, 241.8923f, (byte) 45); //������ �������� 1
 		spawn(284381, 264.6672f, 265.9347f, 241.8658f, (byte) 90); //������ �������� 1
 		spawn(284382, 248.3278f, 249.7112f, 241.8719f, (byte) 16); //������ �������� 1
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
