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

package ai.instance.unstableSplinterpath;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;


/**
 * @author Luzien
 * @edit Cheatkiller
 */
@AIName("unstablepazuzu")
public class UnstablePazuzuAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> task;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 342219, getObjectId(), 0, 0);
			startTask();
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500003, getObjectId(), 0, 0);
	}

	private void startTask() {
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19145, 55, getOwner()).useNoAnimationSkill();
				if (getPosition().getWorldMapInstance().getNpc(283206) == null) {
					spawn(283206, 651.351990f, 326.425995f, 465.523987f, (byte) 8);
					spawn(283206, 666.604980f, 314.497009f, 465.394012f, (byte) 27);
					spawn(283206, 685.588989f, 342.955994f, 465.908997f, (byte) 68);
					spawn(283206, 651.322021f, 346.554993f, 465.563995f, (byte) 111);
				}
			}

		}, 5000, 70000);
	}

	private void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}

}
