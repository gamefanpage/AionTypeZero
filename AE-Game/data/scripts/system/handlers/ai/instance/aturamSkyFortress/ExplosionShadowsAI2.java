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

package ai.instance.aturamSkyFortress;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("explosion_shadows")
public class ExplosionShadowsAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome.compareAndSet(true, false)) {
			SkillEngine.getInstance().getSkill(getOwner(), 19428, 1, getOwner()).useNoAnimationSkill();
			getPosition().getWorldMapInstance().getDoors().get(2).setOpen(true);
			getPosition().getWorldMapInstance().getDoors().get(17).setOpen(true);
			doSchedule();
		}
	}

	@Override
	protected void handleBackHome() {
		isHome.set(true);
		super.handleBackHome();
	}

	private void doSchedule() {
		if (!isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), 19425, 49, getOwner()).useNoAnimationSkill();

						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								if (!isAlreadyDead()) {
									check();
								}
							}

						}, 1500);
					}
				}

			}, 3000);

		}
	}

	private void check() {
		getPosition().getWorldMapInstance().getDoors().get(17).setOpen(false);
		getPosition().getWorldMapInstance().getDoors().get(2).setOpen(false);
		getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.getEffectController().hasAbnormalEffect(19502)) {
					final Npc npc = (Npc) spawn(799657, player.getX(), player.getY(), player.getZ(), player.getHeading());
					player.getEffectController().removeEffect(19502);
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (npc != null && !npc.getLifeStats().isAlreadyDead()) {
								npc.getController().onDelete();
							}
						}

					}, 4000);
				}
			}

		});
		AI2Actions.deleteOwner(this);
	}

}
