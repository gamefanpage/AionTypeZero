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
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldPosition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Luzien
 */
@AIName("priest_preceptor")
public class PriestPreceptorAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean is75EventStarted = new AtomicBoolean(false);
	private AtomicBoolean is25EventStarted = new AtomicBoolean(false);

	@Override
	public void handleSpawned() {
		super.handleSpawned();

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19612, 15, getOwner()).useNoAnimationSkill();
			}

		}, 1000);

	}

	@Override
	public void handleBackHome() {
		is75EventStarted.set(false);
		is25EventStarted.set(false);
		super.handleBackHome();
	}

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int percentage) {
		if (percentage <= 75) {
			if (is75EventStarted.compareAndSet(false, true)) {
				SkillEngine.getInstance().getSkill(getOwner(), 19611, 10, getTargetPlayer()).useNoAnimationSkill();
			}
		}
		if (percentage <= 25) {
			if (is25EventStarted.compareAndSet(false, true)) {
				startEvent();
			}
		}
	}

	private void startEvent() {
		SkillEngine.getInstance().getSkill(getOwner(), 19610, 10, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
						public void run() {
							SkillEngine.getInstance().getSkill(getOwner(), 19614, 10, getOwner()).useNoAnimationSkill();

							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
									public void run() {
										WorldPosition p = getPosition();
										applySoulSickness((Npc) spawn(282366, p.getX(), p.getY(), p.getZ(), p.getHeading()));
										applySoulSickness((Npc) spawn(282367, p.getX(), p.getY(), p.getZ(), p.getHeading()));
										applySoulSickness((Npc) spawn(282368, p.getX(), p.getY(), p.getZ(), p.getHeading()));
								}
							}, 5000);
						}

		}, 2000);
	}

	private Player getTargetPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 25)) {
				players.add(player);
			}
		}
		return players.get(Rnd.get(players.size()));
	}

	private void applySoulSickness(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				npc.getLifeStats().setCurrentHpPercent(50); //TODO: remove this, fix max hp debuffs not reducing current hp properly
				SkillEngine.getInstance().getSkill(npc, 19594, 4, npc).useNoAnimationSkill();
			}

		}, 1000);
	}

}
