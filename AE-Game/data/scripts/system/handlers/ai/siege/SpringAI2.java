/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ai.siege;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.stats.container.CreatureLifeStats;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("spring")
public class SpringAI2 extends NpcAI2 {

	@Override
	public void handleSpawned() {
		startSchedule();
	}

	private void startSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				checkForHeal();
			}
		}, 5000);
	}

	private void checkForHeal() {
		if (!isAlreadyDead() && getPosition().isSpawned()) {
			for (VisibleObject object : getKnownList().getKnownObjects().values()) {
				Creature creature = (Creature) object;
				CreatureLifeStats<?> lifeStats = creature.getLifeStats();
				if (isInRange(creature, 10) && !creature.getEffectController().hasAbnormalEffect(19116)
					&& !lifeStats.isAlreadyDead() && (lifeStats.getCurrentHp() < lifeStats.getMaxHp()))
					if (creature instanceof SiegeNpc) {
						SiegeNpc npc = (SiegeNpc) creature;
						if (getObjectTemplate().getRace() == npc.getObjectTemplate().getRace()) {
							doHeal();
							break;
						}
					}
					else if (creature instanceof Player) {
						Player player = (Player) creature;
						if (getObjectTemplate().getRace() == player.getRace() && player.isOnline()) {
							doHeal();
							break;
						}
					}
			}
			startSchedule();
		}
	}

	private void doHeal() {
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, 19116);
	}
}
