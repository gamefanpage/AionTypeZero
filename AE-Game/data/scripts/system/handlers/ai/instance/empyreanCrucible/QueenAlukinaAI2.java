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

package ai.instance.empyreanCrucible;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author Luzien
 */
@AIName("alukina_emp")
public class QueenAlukinaAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();
	private Future<?> task;

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		addPercents();
	}

	@Override
	public void handleDespawned() {
		cancelTask();
		percents.clear();
		super.handleDespawned();
	}

	@Override
	public void handleDied() {
		cancelTask();
		super.handleDied();
	}

	@Override
	public void handleBackHome() {
		addPercents();
		cancelTask();
		super.handleBackHome();
	}

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void startEvent(int percent) {

		SkillEngine.getInstance().getSkill(getOwner(), 17899, 41, getTarget()).useNoAnimationSkill();

		switch (percent) {
			case 75:
				scheduleSkill(17900, 4500);
				NpcShoutsService.getInstance().sendMsg(getOwner(), 340487, getObjectId(), 0, 10000);
				scheduleSkill(17899, 14000);
				scheduleSkill(17900, 18000);
				break;
			case 50:
				scheduleSkill(17280, 4500);
				scheduleSkill(17902, 8000);
				break;
			case 25:
				task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

					@Override
					public void run() {
						if (isAlreadyDead()) {
							cancelTask();
						}
						else {
							SkillEngine.getInstance().getSkill(getOwner(), 17901, 41, getTarget()).useNoAnimationSkill();
							scheduleSkill(17902, 5500);
							scheduleSkill(17902, 7500);
						}
					}
				}, 4500, 20000);
				break;
			}
	}

	private void cancelTask() {
		if (task != null && !task.isCancelled())
			task.cancel(true);
	}

	private void scheduleSkill(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), skillId, 41, getTarget()).useNoAnimationSkill();

					}
				}
			}, delay);
	}

	private void checkPercentage(int percentage) {
		for (Integer percent : percents) {
			if (percentage <= percent) {
				percents.remove(percent);
				startEvent(percent);
				break;
			}
		}
	}
	private void addPercents() {
		percents.clear();
		Collections.addAll(percents, new Integer[] {75, 50, 25});
	}
}
