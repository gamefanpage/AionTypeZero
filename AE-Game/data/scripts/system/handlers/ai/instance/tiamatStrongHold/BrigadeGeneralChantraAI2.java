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

package ai.instance.tiamatStrongHold;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;


/**
 * @author Cheatkiller
 *
 */
@AIName("brigadegeneralchantra")
public class BrigadeGeneralChantraAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> trapTask;
	private boolean isFinalBuff;

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		if(isHome.compareAndSet(true, false))
			startSkillTask();
		if(!isFinalBuff && getOwner().getLifeStats().getHpPercentage() <= 25) {
			isFinalBuff = true;
			AI2Actions.useSkill(this, 20942);
		}
	}

	private void startSkillTask()	{
		trapTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
				if (isAlreadyDead())
					cancelTask();
				else {
					startTrapEvent();
				}
			}
		}, 5000, 40000);
	}

	private void cancelTask() {
		if (trapTask != null && !trapTask.isCancelled()) {
			trapTask.cancel(true);
		}
	}

	private void startTrapEvent() {
		int [] trapNpc = {283092, 283094};
		final int trap = trapNpc[Rnd.get(0, trapNpc.length -1)];
		if (getPosition().getWorldMapInstance().getNpc(trap) == null) {
			spawn(trap, 1031.1f, 466.38f, 445.45f, (byte) 0);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

	  		@Override
	  		public void run() {
	  			Npc ring = getPosition().getWorldMapInstance().getNpc(trap);
	  			if(trap == 283092)
	  				spawn(283171, 1031.1f, 466.38f, 445.45f, (byte) 0);
	  			else
	  				spawn(283172, 1031.1f, 466.38f, 445.45f, (byte) 0);
	  			ring.getController().onDelete();
	  		}
	  	}, 5000);
	  }
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isFinalBuff = false;
		isHome.set(true);
	}
}
