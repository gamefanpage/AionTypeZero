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

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("pagati_tamer_nishaka")
public class PagatiTamerNishakaAI2 extends AggressiveNpcAI2 {

	private Future<?> hideTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			sendMsg(1500397);
			startHideTask();
		}
	}

	private void cancelPhaseTask() {
		if (hideTask != null && !hideTask.isDone()) {
			hideTask.cancel(true);
		}
	}

	private void startHideTask() {
		hideTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					SkillEngine.getInstance().getSkill(getOwner(), 19660, 60, getOwner()).useNoAnimationSkill();
					sendMsg(1500398);
					startEvent(2000, 1500399, 19661);
					startEvent(6000, 1500399, 19661);
					startEvent(8000, 1500400, 19662);
				}
			}
		}, 14000, 14000);
	}

	private void startEvent(int time, final int msg, final int skill) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead() && !isHome.get()) {
					Creature target = getOwner();
					if (skill == 19661) {
						VisibleObject npcTarget = target.getTarget();
						if (npcTarget != null && npcTarget instanceof Creature) {
							target = (Creature) npcTarget;
						}
					}
					if (target != null && isInRange(target, 5)) {
						SkillEngine.getInstance().getSkill(getOwner(), skill, 60, target).useNoAnimationSkill();
					}
					getEffectController().removeEffect(19660);
					sendMsg(msg);
				}
			}

		}, time);
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
	}

	@Override
	protected void handleDied() {
		getPosition().getWorldMapInstance().getDoors().get(98).setOpen(true);
		cancelPhaseTask();
		sendMsg(1500401);
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		cancelPhaseTask();
		super.handleDespawned();
	}

	@Override
	protected void handleBackHome() {
		getEffectController().removeEffect(19660);
		cancelPhaseTask();
		isHome.set(true);
		super.handleBackHome();
	}
}
