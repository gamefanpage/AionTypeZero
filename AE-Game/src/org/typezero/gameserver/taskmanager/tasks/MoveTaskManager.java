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

package org.typezero.gameserver.taskmanager.tasks;

import com.aionemu.commons.utils.internal.chmv8.ForkJoinTask;
import com.google.common.base.Predicate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.taskmanager.AbstractPeriodicTaskManager;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneUpdateService;

import static org.typezero.gameserver.taskmanager.parallel.ForEach.forEach;

/**
 * @author ATracer
 * @reworked Rolandas, parallelized by using Fork/Join framework
 */
public class MoveTaskManager extends AbstractPeriodicTaskManager {

	private final FastMap<Integer, Creature> movingCreatures = new FastMap<Integer, Creature>().shared();

	public static final int UPDATE_PERIOD = 100;

	private final Predicate<Creature> CREATURE_MOVE_PREDICATE = new Predicate<Creature>() {

		@Override
		public boolean apply(Creature creature) {
			creature.getMoveController().moveToDestination();
			if (creature.getAi2().poll(AIQuestion.DESTINATION_REACHED)) {
				movingCreatures.remove(creature.getObjectId());
				creature.getAi2().onGeneralEvent(AIEventType.MOVE_ARRIVED);
				ZoneUpdateService.getInstance().add(creature);
			}
			else {
				creature.getAi2().onGeneralEvent(AIEventType.MOVE_VALIDATE);
			}
			return true;
		}

	};

	private MoveTaskManager() {
		super(UPDATE_PERIOD);
	}

	public void addCreature(Creature creature) {
		movingCreatures.put(creature.getObjectId(), creature);
	}

	public void removeCreature(Creature creature) {
		movingCreatures.remove(creature.getObjectId());
	}

	@Override
	public void run() {
		final FastList<Creature> copy = new FastList<Creature>();
		for (FastMap.Entry<Integer, Creature> e = movingCreatures.head(), mapEnd = movingCreatures.tail(); (e = e.getNext()) != mapEnd;) {
			copy.add(e.getValue());
		}
		ForkJoinTask<Creature> task = forEach(copy, CREATURE_MOVE_PREDICATE);
		if (task != null)
			ThreadPoolManager.getInstance().getForkingPool().invoke(task);
	}

	public static MoveTaskManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static final class SingletonHolder {

		private static final MoveTaskManager INSTANCE = new MoveTaskManager();
	}

}
