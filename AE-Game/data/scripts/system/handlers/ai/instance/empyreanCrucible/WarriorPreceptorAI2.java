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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Luzien
 */
@AIName("warrior_preceptor")
public class WarriorPreceptorAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> task;

	@Override
	public void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}

	@Override
	public void handleDied() {
		cancelTask();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500208, getObjectId(), 0, 0);
		super.handleDied();
	}

	@Override
	public void handleBackHome() {
		cancelTask();
		isHome.set(true);
		super.handleBackHome();
	}

	@Override
	public void handleAttack(Creature creature) {

		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}

	private void startSkillTask() {
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}
				else {
					startSkillEvent();
				}
			}

		}, 30000, 30000);
	}

	private void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}

	private void startSkillEvent() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500207, getObjectId(), 0, 0);
		SkillEngine.getInstance().getSkill(getOwner(), 19595, 10, getTargetPlayer()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 19596, 15, getOwner()).useNoAnimationSkill();
				}
			}

		}, 6000);
	}

	private Player getTargetPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 15)) {
				players.add(player);
			}
		}
		return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
	}
}
