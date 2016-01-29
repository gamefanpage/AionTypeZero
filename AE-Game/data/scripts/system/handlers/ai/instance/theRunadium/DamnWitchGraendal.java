package ai.instance.theRunadium;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * M.O.G. Devs Team , fix Romanz
 */

@AIName("damn_witch_graendal")
public class DamnWitchGraendal extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500743, getObjectId(), 0, 1000);
		addPercent();
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage > 95 && percents.size() < 3) {
			addPercent();
		}
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 95:
						//shout();
						skill1();
						break;
					case 75:
						shout2();
						skill3();
						break;
					case 63:
						shout_spawn();
						spawn_support();
						degeneration_skill();
						degeneration();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{95,75,63});
	}
	private void skill1() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21172, 65, target).useNoAnimationSkill();
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21172, 65, target).useNoAnimationSkill();
		}
	}
	private void degeneration_skill() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21165, 65, target).useNoAnimationSkill();
		}
	}
	private void shout_spawn() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500750, getObjectId(), 0, 1000);
	}
  private void spawn_support() {
 		spawn(284379, 264.6672f, 265.9347f, 241.8658f, (byte) 90);
 		spawn(284380, 248.6492f, 265.8888f, 241.8923f, (byte) 90);
 		spawn(284381, 264.6672f, 265.9347f, 241.8658f, (byte) 90);
 		spawn(284382, 248.3278f, 249.7112f, 241.8719f, (byte) 16);
 	}
	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500738, getObjectId(), 0, 1000);
	}
	private void checkhp(final Npc npc) {
				npc.getLifeStats().setCurrentHpPercent(63);
	}
  private void degeneration() {
 		AI2Actions.deleteOwner(this);
                checkhp((Npc) spawn(284375, 255.5008f, 293.1228f, 253.7146f, (byte) 89));
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
