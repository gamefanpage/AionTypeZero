
package ai.instance.idVritraBase;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dision , Romanz
 *
 */

@AIName("chitha")
public class CommanderShithaAI2 extends AggressiveNpcAI2 {


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
						shout_cast1();
						skill3();
						break;
					case 80:
						shout1();
						spawn_support1();
						break;
					case 70:
						shout_cast2();
						skill2();
						break;
					case 60:
						shout2();
						break;
					case 50:
						shout_cast3();
						skill3();
						break;
					case 40:
						shout3();
						spawn_support2();
						break;
					case 30:
						shout_cast1();
						skill2();
						break;
					case 20:
						shout4();
						spawn_support3();
						break;
					case 10:
						shout_cast2();
						skill3();
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

	private void skill1() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21410, 65, target).useNoAnimationSkill();
		}
	}
	private void skill2() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 21425, 65, target).useNoAnimationSkill();
		}
	}
	private void skill3() {
		VisibleObject target = getTarget();
		if (target != null && target instanceof Player) {
			SkillEngine.getInstance().getSkill(getOwner(), 20774, 65, target).useNoAnimationSkill();
		}
	}

	private void shout_start() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500773, getObjectId(), 0, 1000);
	}
	private void shout1() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500774, getObjectId(), 0, 1000);
	}
	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500775, getObjectId(), 0, 1000);
	}
	private void shout3() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500776, getObjectId(), 0, 1000);
	}
	private void shout4() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500777, getObjectId(), 0, 1000);
	}
	private void shout_died() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500778, getObjectId(), 0, 1000);
	}
	private void shout_cast1() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500779, getObjectId(), 0, 1000);
	}
	private void shout_cast2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500780, getObjectId(), 0, 1000);
	}
	private void shout_cast3() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500781, getObjectId(), 0, 1000);
	}

	private void spawn_support1() {
		Npc spawn_support1 = getPosition().getWorldMapInstance().getNpc(284435);
		if (spawn_support1 == null){
	  spawn(284435, getOwner().getX() + 1, getOwner().getY() - 1, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() - 1, getOwner().getY() + 1, getOwner().getZ(), (byte) 0);
		}
	}

	private void spawn_support2() {
		Npc spawn_support2 = getPosition().getWorldMapInstance().getNpc(284435);
		if (spawn_support2 == null){
	  spawn(284435, getOwner().getX() + 2, getOwner().getY() - 2, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() + 1, getOwner().getY() - 1, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() - 1, getOwner().getY() + 1, getOwner().getZ(), (byte) 0);
		}
	}

	private void spawn_support3() {
		Npc spawn_support3 = getPosition().getWorldMapInstance().getNpc(284435);
		if (spawn_support3 == null){
	  spawn(284435, getOwner().getX() - 2, getOwner().getY() + 2, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() + 2, getOwner().getY() - 2, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() + 1, getOwner().getY() - 1, getOwner().getZ(), (byte) 0);
	  spawn(284435, getOwner().getX() - 1, getOwner().getY() + 1, getOwner().getZ(), (byte) 0);
		}
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
