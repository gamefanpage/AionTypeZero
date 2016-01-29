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

package ai.instance.rentusBase;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("tarotran")
public class TarotranAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean isStartedEvent = new AtomicBoolean(false);
	private Future<?> eventTask;
	private Future<?> thinkTask;
	private int buffNr = 1;
	private boolean canThink = true;

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isStartedEvent.compareAndSet(false, true)) {
			spawn(282386, 383.964f, 541.48f, 147.5f, (byte) 38);
			startEventTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleMoveValidate() {
		if (!isAlreadyDead() && getOwner().isSpawned()) {
			super.handleMoveValidate();
		}
	}

	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				SkillEngine.getInstance().getSkill(getOwner(), 19700, 60, getOwner()).useNoAnimationSkill();
				thinkTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						int count = Rnd.get(4, 8);
						while (count > 0) {
							count --;
							sp(282385);
						}
						canThink = true;
						Creature creature = getAggroList().getMostHated();
						if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
							setStateIfNot(AIState.FIGHT);
							think();
						}
						else {
							getMoveController().abortMove();
							getOwner().setTarget(creature);
							getOwner().getGameStats().renewLastAttackTime();
							getOwner().getGameStats().renewLastAttackedTime();
							getOwner().getGameStats().renewLastChangeTargetTime();
							getOwner().getGameStats().renewLastSkillTime();
							setStateIfNot(AIState.FIGHT);
							handleMoveValidate();
						}
					}

				}, 4000);
				break;
			}
		}
	}

	private void sp(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(1, 2);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		final Npc npc = (Npc) spawn(npcId,  p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!NpcActions.isAlreadyDead(npc) && npc.isSpawned()) {
					NpcActions.delete(npc);
				}
			}

		}, 15000);
	}

	private void startEventTask() {
		cancelEventTask();
		eventTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelEventTask();
				}
				else {
					int skill = 0;
					switch (buffNr) {
						case 1:
							buffNr ++;
							skill = 19370;
							break;
						case 2:
							buffNr ++;
							skill = 19371;
							break;
						case 3:
							buffNr = 1;
							skill = 19372;
							break;
					}
					SkillEngine.getInstance().getSkill(getOwner(), skill, 60, getOwner()).useNoAnimationSkill();
				}
			}

		}, 21000, 21000);
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{85, 65, 55, 45, 30, 15});
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void cancelEventTask() {
		if (eventTask != null && !eventTask.isDone()) {
			eventTask.cancel(true);
		}
	}

	private void cancelThinkTask() {
		if (thinkTask != null && !thinkTask.isDone()) {
			thinkTask.cancel(true);
		}
	}

	@Override
	protected void handleDespawned() {
		cancelEventTask();
		cancelThinkTask();
		super.handleDespawned();
	}

	@Override
	protected void handleBackHome() {
		canThink = true;
		addPercent();
		cancelEventTask();
		cancelThinkTask();
		isStartedEvent.set(false);
		removeHelpers();
		getEffectController().removeEffect(19370);
		getEffectController().removeEffect(19371);
		getEffectController().removeEffect(19372);
		super.handleBackHome();
	}

	private void removeHelpers() {
		WorldPosition p = getPosition();
		if (p != null) {
			WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null) {
				deleteNpcs(instance.getNpcs(282386));
				deleteNpcs(instance.getNpcs(282387));
				deleteNpcs(instance.getNpcs(282530));
				deleteNpcs(instance.getNpcs(282385));
			}
		}
	}

	@Override
	protected void handleDied() {
		removeHelpers();
		cancelEventTask();
		cancelThinkTask();
		super.handleDied();
	}

}
