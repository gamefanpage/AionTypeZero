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

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritsu
 */
@AIName("calindiflamelord")
public class CalindiFlamelordAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean isStart = new AtomicBoolean(false);

	@Override
	protected void handleSpawned() {
		addPercent();
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if (isStart.compareAndSet(false, true)) {
			checkTimer();
		}

	}

	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				if (percent == 60) {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 18233, 50, getOwner()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							sp(281267);
						}
					}, 3000);
				}
				else {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 18233, 50, getOwner()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							sp(281268);
							sp(281268);
						}
					}, 3000);
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void sp(int npcId) {
		if (npcId == 281267) {
			spawn(npcId, 1191.2714f, 1220.5795f, 144.2901f, (byte) 36);
			spawn(npcId, 1188.3695f, 1257.1322f, 139.66028f, (byte) 80);
			spawn(npcId, 1177.1423f, 1253.9136f, 140.58705f, (byte) 97);
			spawn(npcId, 1163.5889f, 1231.9149f, 145.40042f, (byte) 118);
		}
		else {
			float direction = Rnd.get(0, 199) / 100f;
			int distance = Rnd.get(0, 2);
			float x1 = (float) (Math.cos(Math.PI * direction) * distance);
			float y1 = (float) (Math.sin(Math.PI * direction) * distance);
			WorldPosition p = getPosition();
			spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), p.getHeading());
		}
	}

	private void checkTimer() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					EmoteManager.emoteStopAttacking(getOwner());
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1400259);
					SkillEngine.getInstance().getSkill(getOwner(), 19679, 50, getTarget()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead()) {
								getOwner().getController().onDelete();
								NpcShoutsService.getInstance().sendMsg(getOwner(), 1400260);
							}
						}
					}, 2000);
				}
			}
		}, 600000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{60, 30});
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		super.handleDied();
	}
}
