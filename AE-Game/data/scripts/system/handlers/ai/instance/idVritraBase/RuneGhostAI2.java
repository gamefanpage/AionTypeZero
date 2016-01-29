package ai.instance.idVritraBase;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.skillengine.SkillEngine;


/**
 * @author Romanz
 *
 */
@AIName("rune_ghost")
public class RuneGhostAI2 extends NpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		useskill();
		despawn();
	}

	private void useskill() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 21185, 25, getOwner()).useNoAnimationSkill();
				}
			}
		}, 2000);
	}

	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(RuneGhostAI2.this);
				}
			}
		}, 4500);
	}
}
