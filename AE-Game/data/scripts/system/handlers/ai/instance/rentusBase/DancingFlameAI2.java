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

package ai.instance.rentusBase;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
@AIName("dancing_flame")
public class DancingFlameAI2 extends GeneralNpcAI2 {

	private Future<?> task;

	private void startTask() {
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}
				else {
					if (isPlayerInRange()) {
						WorldPosition p = getPosition();
						if (getNpcId() == 282996) {
							spawn(282998, p.getX(), p.getY(), p.getZ(), p.getHeading());
						}
						else {
							spawn(282999, p.getX(), p.getY(), p.getZ(), p.getHeading());
						}
					}
				}
			}

		}, 3000, 3000);
	}

	private boolean isPlayerInRange() {
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				return true;
			}
		}
		return false;
	}

	private void cancelTask() {
		if (task != null && !task.isDone())  {
			task.cancel(true);
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getNpcId() == 282996 || getNpcId() == 282997) {
			startTask();
		}
		else {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), getNpcId() == 282998 ? 20536 : 20535, 60, getOwner()).useNoAnimationSkill();
				}

			}, 500);
			starLifeTask();
		}
	}

	private void starLifeTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				despawn();
			}

		}, 4000);
	}

	private void despawn() {
		if (!isAlreadyDead()) {
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
	}

	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_ATTACK_PLAYER:
				return AIAnswers.POSITIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
	}

}
