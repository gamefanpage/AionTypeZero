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

import ai.ActionItemNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.controllers.effect.EffectController;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("explosive_drana_crystal")
public class ExplosiveDranaCrystalAI2 extends ActionItemNpcAI2 {

	private AtomicBoolean isUsed = new AtomicBoolean(false);
	private Future<?> lifeTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startLifeTask();
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (isUsed.compareAndSet(false, true)) {
			WorldPosition p = getPosition();
			Npc boss = p.getWorldMapInstance().getNpc(217308);
			if (boss != null && !NpcActions.isAlreadyDead(boss)) {
				EffectController ef = boss.getEffectController();
				if (ef.hasAbnormalEffect(19370)) {
					ef.removeEffect(19370);
				}
				else if (ef.hasAbnormalEffect(19371)) {
					ef.removeEffect(19371);
				}
				else if (ef.hasAbnormalEffect(19372)) {
					ef.removeEffect(19372);
				}
			}
			Npc npc = (Npc) spawn(282530, p.getX(), p.getY(), p.getZ(), p.getHeading());
			Npc invisibleNpc = (Npc) spawn(282529, p.getX(), p.getY(), p.getZ(), p.getHeading());
			SkillEngine.getInstance().getSkill(npc, 19373, 60, npc).useNoAnimationSkill();
			SkillEngine.getInstance().getSkill(invisibleNpc, 19654, 60, invisibleNpc).useNoAnimationSkill();
			NpcActions.delete(invisibleNpc);
			AI2Actions.deleteOwner(this);
		}
	}

	private void cancelLifeTask() {
		if (lifeTask != null && !lifeTask.isDone()) {
			lifeTask.cancel(true);
		}
	}

	private void startLifeTask() {
		lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(null);
				}
			}

		}, 60000);
	}

	@Override
	protected void handleDied() {
		cancelLifeTask();
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		cancelLifeTask();
		super.handleDespawned();
	}

}
