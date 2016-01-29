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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;


/**
 * @author Cheatkiller
 *
 */
@AIName("adjutantanuhart")
public class AdjutantAnuhartAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> bladeStormTask;
	protected List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if(isHome.compareAndSet(true, false))
			startBladeStormTask();
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void startBladeStormTask()	{
		bladeStormTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
				if (isAlreadyDead())
					cancelTask();
				else {
					startBladeStormEvent();
				}
			}
		}, 5000, 40000);
	}


	private void startBladeStormEvent() {
		shield();
		SkillEngine.getInstance().getSkill(getOwner(), 20747, 55, getOwner()).useNoAnimationSkill();
		spawn(283099, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
	}

	private void cancelTask() {
		if (bladeStormTask != null && !bladeStormTask.isCancelled()) {
			bladeStormTask.cancel(true);
		}
	}

	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch(percent){
					case 50:
						chooseBuff(20938);
						break;
					case 25:
						chooseBuff(20939);
						break;
					case 10:
						chooseBuff(20940);
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void chooseBuff(int buff) {
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, buff);
	}

	private void shield() {
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, 20749);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50, 25, 10});
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		super.handleDied();
		cancelTask();
	}
}
