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

package ai.instance.argentManor;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("elemental_iron_prison")
public class ElementalIronPrisonAI2 extends GeneralNpcAI2 {

	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean isStartEvent = new AtomicBoolean(false);
	private Future<?> phaseTask;
	private Future<?> aggroTask;

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 25) {
				if (isStartEvent.compareAndSet(false, true)) {
					Npc npc = getPosition().getWorldMapInstance().getNpc(205498);
					if (npc != null) {
						NpcShoutsService.getInstance().sendMsg(npc, 1500465, npc.getObjectId(), 0, 0);
						NpcShoutsService.getInstance().sendMsg(npc, 1500464, npc.getObjectId(), 0, 10000);
					}
				}
			}
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(76).setOpen(false);
			startPhaseTask();
			aggroTask();
		}
		super.handleAttack(creature);
	}

	private void aggroTask() {
		aggroTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelAggroTask();
				}
				else {
					if (!isInRangePlayer()) {
						handleBackHome();
					}
				}
			}

		}, 2000, 2000);
	}

	private boolean isInRangePlayer() {
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 40) && !PlayerActions.isAlreadyDead(player) && getOwner().canSee(player)) {
				return true;
			}
		}
		return false;
	}

	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					int skill = 0;
					switch (Rnd.get(1, 4)) {
						case 1:
							skill = 19312;
							break;
						case 2:
							skill = 19313;
							break;
						case 3:
							skill = 19314;
							break;
						case 4:
							skill = 19315;
							break;
					}
					SkillEngine.getInstance().getSkill(getOwner(), skill, 60, getOwner()).useNoAnimationSkill();
				}
			}

		}, 0, 30000);
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	private void cancelAggroTask() {
		if (aggroTask != null && !aggroTask.isDone()) {
			aggroTask.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		handleFinishAttack();
		cancelAggroTask();
		cancelPhaseTask();
		getPosition().getWorldMapInstance().getDoors().get(76).setOpen(true);
		getEffectController().removeEffect(19312);
		getEffectController().removeEffect(19313);
		getEffectController().removeEffect(19314);
		getEffectController().removeEffect(19315);
		isAggred.set(false);
		super.handleBackHome();
	}

	@Override
	protected void handleDied() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			instance.getDoors().get(76).setOpen(true);
			instance.getDoors().get(26).setOpen(true);
			Npc npc = instance.getNpc(701000);
			NpcActions.delete(npc);
		}
		cancelAggroTask();
		cancelPhaseTask();
		super.handleDied();
	}

	@Override
	public int modifyHealValue(int value) {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		int healValue = instance.getPlayersInside().size() == 12 ? 1 : 10;
		return healValue;
	}
}
