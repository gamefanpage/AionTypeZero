package ai.instance.dragonLordsRefuge_Hard;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;

/**
 * @author Romanz
 */

@AIName("gods_h")
public class Gods_HardAI2 extends AggressiveNpcAI2 {

   Npc tiamat;

	@Override
	protected  void handleDeactivate() {
	}

	@Override
	public int modifyDamage(int damage) {
		return 6000;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		tiamat = getPosition().getWorldMapInstance().getNpc(856028);
		if (getNpcId() == 856020 || getNpcId() == 856023) {
			//empyrean lord (god) debuff all players before start attack Tiamat
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), (getOwner().getNpcId() == 856020 ? 20932 : 20936), 100, getOwner()).useSkill();
				}
			}, 8000);
			//empyrean lord (god) start attack Tiamat Dragon
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					AI2Actions.targetCreature(Gods_HardAI2.this, tiamat);
					getAggroList().addHate(tiamat, 100000);
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401550);
					SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 856020 ? 20931 : 20935), 60, tiamat).useNoAnimationSkill(); //adds 1mio hate
				}
			}, 12000);
		} else if (getNpcId() == 856021 || getNpcId() == 856024) {
			//empyrean lord (god) start final attack to Tiamat Dragon before became exausted
			NpcShoutsService.getInstance().sendMsg(getOwner(), (getNpcId() == 856021 ? 1401538 : 1401539));
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 856021 ? 20929 : 20933), 100, tiamat).useNoAnimationSkill();
				}
			}, 2000);
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleActivate() {
		super.handleActivate();
		tiamat = getPosition().getWorldMapInstance().getNpc(856028);
		if (getOwner().getNpcId() == 856020 || getOwner().getNpcId() == 856023) {
			AI2Actions.targetCreature(Gods_HardAI2.this, tiamat);
			SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 856020 ? 20931 : 20935), 60, tiamat).useSkill();
		}
	}

	private void checkPercentage(int hpPercentage) {
		if (getOwner().getNpcId() == 856020 || getOwner().getNpcId() == 856020) {
			if (hpPercentage == 50) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401548);
			}
			if (hpPercentage == 15) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401549);
			}
			if (hpPercentage < 5) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401548);
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401549);
			}
		}
	}
}
