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

package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;

/**
 *
 * @author Ritsu
 */
@AIName("gatessummoned")
public class GatesSummonedAI2 extends GeneralNpcAI2 {

	private Future<?> eventTask;
	private boolean canThink = true;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startMove();
	}

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleDied() {
		cancelEventTask();
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		cancelEventTask();
		super.handleDespawned();
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		startEventTask();
	}

	private void startMove()
	{
		canThink = false;
		EmoteManager.emoteStopAttacking(getOwner());
		setStateIfNot(AIState.FOLLOWING);
		getOwner().setState(1);
		AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(216960));
		getMoveController().moveToTargetObject();
	}

	private void cancelEventTask() {
		if (eventTask != null && !eventTask.isDone()) {
			eventTask.cancel(true);
		}
	}

	private void startEventTask() {
		eventTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Npc boss = getPosition().getWorldMapInstance().getNpc(216960);
				if (isAlreadyDead() && getOwner() == null)
					cancelEventTask();
				else{
					if(Rnd.get(1) == 0)
						SkillEngine.getInstance().getSkill(getOwner(), 19257, 55, boss).useNoAnimationSkill();
					else
						SkillEngine.getInstance().getSkill(getOwner(), 19281, 55, boss).useNoAnimationSkill();
				}
			}

		}, 5000, 30000);

	}
}
