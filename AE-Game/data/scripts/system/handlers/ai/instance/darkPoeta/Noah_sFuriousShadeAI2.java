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

package ai.instance.darkPoeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 *
 */
@AIName("spectral_tree")
public class Noah_sFuriousShadeAI2 extends AggressiveNpcAI2 {

	private Future<?> skillTask;
	private Future<?> skill2Task;
	protected List<Integer> percents = new ArrayList<Integer>();

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		addPercent();
		useSkill();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 30:
						AI2Actions.useSkill(this, 18529);
						useSkillTree();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void useSkill() {
		if (Rnd.get(2) > 0) {
			SkillEngine.getInstance().getSkill(getOwner(), 16822, 50, getOwner()).useSkill();
		}
	}

	private void useSkillTree() {
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 17736, 50, getTarget()).useSkill();
				skill2Task = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 18531, 50, getTarget()).useSkill();
					}
				}, 8000);
			}
		}, 7000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{30});
	}

	private void cancelTask() {
		if (skillTask != null && !skillTask.isDone()) {
			skillTask.cancel(true);
		} else if (skill2Task != null && !skill2Task.isDone()) {
			skill2Task.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		cancelTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		cancelTask();
		super.handleDied();
	}
}
