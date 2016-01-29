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
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldPosition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Luzien
 */
@AIName("rm_1337")
public class RM1337AI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isEventStarted = new AtomicBoolean(false);
	private Future<?> task1, task2;

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500229, getObjectId(), 0, 2000);
	}

	@Override
	public void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}

	@Override
	public void handleDied() {
		cancelTask();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500231, getObjectId(), 0, 0);
		super.handleDied();
	}

	@Override
	public void handleBackHome() {
		cancelTask();
		super.handleBackHome();
	}

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask1();
		}
		if (getLifeStats().getHpPercentage() <= 75) {
			if (isEventStarted.compareAndSet(false, true)) {
				startSkillTask2();
			}
		}
	}

	private void cancelTask() {
		if (task1 != null && !task1.isCancelled()) {
			task1.cancel(true);
		}
		if (task2 != null && !task2.isCancelled()) {
			task2.cancel(true);
		}
	}

	private void startSkillTask1() {
		task1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

					@Override
					public void run() {
						if (isAlreadyDead()) {
							cancelTask();
						}
						else {
							if (getOwner().getCastingSkill() != null)
								return;
							if (getLifeStats().getHpPercentage() <= 50) {
								switch (Rnd.get(2)) {
									case 0:
										SkillEngine.getInstance().getSkill(getOwner(), 19550, 10, getTargetPlayer()).useNoAnimationSkill();
										break;
									default:
										final Player target = getTargetPlayer();
										SkillEngine.getInstance().getSkill(getOwner(), 19552, 10, target).useNoAnimationSkill();
										ThreadPoolManager.getInstance().schedule(new Runnable() {

												@Override
												public void run() {
													if (!isAlreadyDead()) {
														SkillEngine.getInstance().getSkill(getOwner(), 19553, 10, target).useNoAnimationSkill();
													}
												}
										}, 4000);
								}
							}
							else
								SkillEngine.getInstance().getSkill(getOwner(), 19550, 10, getTargetPlayer()).useNoAnimationSkill();
						}
					}
				}, 10000, 23000);
	}

	private void startSkillTask2() {
		task2 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

					@Override
					public void run() {
						if (isAlreadyDead()) {
							cancelTask();
						}
						else {
							getOwner().getController().cancelCurrentSkill();
							NpcShoutsService.getInstance().sendMsg(getOwner(), 1500230, getObjectId(), 0, 0);
							SkillEngine.getInstance().getSkill(getOwner(), 19551, 10, getTarget()).useNoAnimationSkill();
							spawnSparks();
						}
					}
				}, 0, 60000);
	}

	private void spawnSparks() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						int count = Rnd.get(8,12);
						for (int i=0; i < count; i++) {
							rndSpawn(282373);
						}

					}
				}
			}, 4000);
	}

	private Player getTargetPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 37)) {
				players.add(player);
			}
		}
		return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
	}

	private void rndSpawn(int npcId) {
		float direction = Rnd.get(0, 180) / 100f;
		int distance = Rnd.get(3, 12);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
	}
}
