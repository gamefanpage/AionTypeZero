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

package ai.worlds.pernon;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.controllers.observer.GaleCycloneObserver;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import javolution.util.FastMap;

/**
 * @author xTz
 */
@AIName("gale_cyclone")
public class GaleCycloneAI2 extends NpcAI2 {

private FastMap<Integer, GaleCycloneObserver> observed = new FastMap<Integer, GaleCycloneObserver>().shared();
private boolean blocked;

	@Override
	protected void handleCreatureSee(Creature creature) {
		if (blocked) {
			return;
		}
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			final GaleCycloneObserver observer = new GaleCycloneObserver(player, getOwner()) {

				@Override
				public void onMove() {
					if (!blocked) {
						SkillEngine.getInstance().getSkill(getOwner(), 20528, 50, player).useNoAnimationSkill();
					}
				}

			};
			player.getObserveController().addObserver(observer);
			observed.put(player.getObjectId(), observer);
		}
	}

	@Override
	protected void handleCreatureNotSee(Creature creature) {
		if (blocked) {
			return;
		}
		if (creature instanceof Player) {
			Player player = (Player) creature;
			Integer obj = player.getObjectId();
			GaleCycloneObserver observer = observed.remove(obj);
			if (observer != null) {
				player.getObserveController().removeObserver(observer);
			}
		}
	}

	@Override
	protected void handleDied() {
		clear();
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		clear();
		super.handleDespawned();
	}

	private void clear() {
		blocked = true;
		for (Integer obj : observed.keySet()) {
			Player player = getKnownList().getKnownPlayers().get(obj);
			GaleCycloneObserver observer = observed.remove(obj);
			if (player != null) {
				player.getObserveController().removeObserver(observer);
			}
		}
	}
}
