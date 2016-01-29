/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package ai;

import java.util.concurrent.Future;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.model.skill.NpcSkillEntry;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@AIName("trap")
public class TrapNpcAI2 extends NpcAI2 {

	public static int EVENT_SET_TRAP_RANGE = 1;
	private int trapRange = 0;
	private Future<?> despawnTask;

	@Override
	protected void handleCreatureSee(Creature creature) {
		super.handleCreatureSee(creature);
		tryActivateTrap(creature);
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		super.handleCreatureMoved(creature);
		tryActivateTrap(creature);
	}

	private void tryActivateTrap(Creature creature) {
		if (despawnTask != null)
			return;

		if (!creature.getLifeStats().isAlreadyDead() && !creature.isInVisualState(CreatureVisualState.BLINKING)
				&& isInRange(creature, trapRange)) {

			Creature creator = (Creature) getCreator();
			if (!creator.isEnemy(creature))
				return;


			if (setStateIfNot(AIState.FIGHT)) {
				getOwner().unsetVisualState(CreatureVisualState.HIDE1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
				AI2Actions.targetCreature(this, creature);
				NpcSkillEntry npcSkill = getSkillList().getRandomSkill();
				if (npcSkill != null)
					AI2Actions.useSkill(this, npcSkill.getSkillId());
				despawnTask = ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 5000);
			}
		}
	}

	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if (eventId == EVENT_SET_TRAP_RANGE)
			trapRange = getOwner().getAggroRange();
	}


	@Override
	public boolean isMoveSupported() {
		return false;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

	private static final class TrapDelete implements Runnable {

		private TrapNpcAI2 ai;

		TrapDelete(TrapNpcAI2 ai) {
			this.ai = ai;
		}

		@Override
		public void run() {
			AI2Actions.deleteOwner(ai);
			ai = null;
		}

	}

}
