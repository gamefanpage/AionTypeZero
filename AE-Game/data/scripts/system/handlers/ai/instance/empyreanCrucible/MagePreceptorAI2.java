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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Luzien
 */
@AIName("mage_preceptor")
public class MagePreceptorAI2 extends AggressiveNpcAI2 {
	private List<Integer> percents = new ArrayList<Integer>();

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		addPercents();
	}

	@Override
	public void handleDespawned() {
		percents.clear();
		despawnNpcs();
		super.handleDespawned();
	}

	@Override
	public void handleDied() {
		despawnNpcs();
		super.handleDied();
	}

	@Override
	public void handleBackHome() {
		addPercents();
		despawnNpcs();
		super.handleBackHome();
	}

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void startEvent(int percent) {
		if (percent == 50 || percent == 25) {
			SkillEngine.getInstance().getSkill(getOwner(), 19606, 10, getTarget()).useNoAnimationSkill();
		}

		switch (percent) {
			case 75:
				SkillEngine.getInstance().getSkill(getOwner(), 19605, 10, getTargetPlayer()).useNoAnimationSkill();
				break;
			case 50:
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						if (!isAlreadyDead()) {
							SkillEngine.getInstance().getSkill(getOwner(), 19609, 10, getOwner()).useNoAnimationSkill();
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									WorldPosition p = getPosition();
									spawn(282364, p.getX(), p.getY(), p.getZ(), p.getHeading());
									spawn(282363, p.getX(), p.getY(), p.getZ(), p.getHeading());
									scheduleSkill(2000);
								}

							}, 4500);

						}
					}
				}, 3000);
				break;
			case 25:
				scheduleSkill(3000);
				scheduleSkill(9000);
				scheduleSkill(15000);
				break;
		}

	}

	private void scheduleSkill(int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), 19605, 10, getTargetPlayer()).useNoAnimationSkill();

					}
				}
			}, delay);
	}

	private Player getTargetPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 37)) {
				players.add(player);
			}
		}
		return players.get(Rnd.get(players.size()));
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

	private void despawnNpcs() {
		despawnNpc(getPosition().getWorldMapInstance().getNpc(282364));
		despawnNpc(getPosition().getWorldMapInstance().getNpc(282363));
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
}
