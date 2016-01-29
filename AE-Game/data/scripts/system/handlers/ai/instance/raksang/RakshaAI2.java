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

package ai.instance.raksang;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("raksha")
public class RakshaAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean isStartedEvent = new AtomicBoolean(false);
	private Future<?> phaseTask;

	@Override
	protected void handleDespawned() {
		cancelPhaseTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancelPhaseTask();
		spawn(730445, 1062.281f, 889.900f, 138.744f, (byte) 29);
		super.handleDied();
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), false, 0, 0);
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			sendMsg(1401152);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 75) {
			if (isStartedEvent.compareAndSet(false, true)) {
				sendMsg(1401154);
				startPhaseTask();
			}
		}
	}

	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					SkillEngine.getInstance().getSkill(getOwner(), 19938, 46, getOwner()).useNoAnimationSkill();
					List<Player> players = getLifedPlayers();
					if (!players.isEmpty()) {
						int size = players.size();
						if (players.size() < 4) {
							for (Player p : players) {
								spawnRubble(p);
							}
						}
						else {
							int count = Rnd.get(3, size);
							for (int i = 0; i < count; i++) {
								if (players.isEmpty()) {
									break;
								}
								spawnRubble(players.get(Rnd.get(players.size())));
							}
						}
					}
				}
			}

		}, 3000, 15000);
	}

	private void spawnRubble(Player player) {
		final float x = player.getX();
		final float y = player.getY();
		final float z = player.getZ();
		if (x > 0 && y > 0 && z > 0) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						spawn(282325, x, y, z, (byte) 0);
					}
				}

			}, 3000);

		}
	}

	private List<Player> getLifedPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player)) {
				players.add(player);
			}
		}
		return players;
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		cancelPhaseTask();
		isStartedEvent.set(false);
		isAggred.set(false);
		super.handleBackHome();
	}

}
