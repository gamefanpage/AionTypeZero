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

import org.typezero.gameserver.ai2.AI2Logger;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.world.WorldMapTemplate;
import org.typezero.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import org.typezero.gameserver.world.knownlist.VisitorWithOwner;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author ATracer
 */
public class MovementNotifyTask extends AbstractFIFOPeriodicTaskManager<Creature> {

	private static Map<Integer, int[]> moveBroadcastCounts = new HashMap<Integer, int[]>();

	static {
		Iterator<WorldMapTemplate> iter = DataManager.WORLD_MAPS_DATA.iterator();
		while (iter.hasNext())
			moveBroadcastCounts.put(iter.next().getMapId(), new int[2]);
	}

	private static final class SingletonHolder {

		private static final MovementNotifyTask INSTANCE = new MovementNotifyTask();
	}

	public static MovementNotifyTask getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private final MoveNotifier MOVE_NOTIFIER = new MoveNotifier();

	public MovementNotifyTask() {
		super(500);
	}

	@Override
	protected void callTask(Creature creature) {
		if (creature.getLifeStats().isAlreadyDead())
			return;

		// In Reshanta:
		// max_move_broadcast_count is 200 and
		// min_move_broadcast_range is 75, as in client WorldId.xml
		int limit = creature.getWorldId() == 400010000 ? 200 : Integer.MAX_VALUE;
		int iterations = creature.getKnownList().doOnAllNpcsWithOwner(MOVE_NOTIFIER, limit);

		if (!(creature instanceof Player)) {
			int[] maxCounts = moveBroadcastCounts.get(creature.getWorldId());
			synchronized (maxCounts) {
				if (iterations > maxCounts[0]) {
					maxCounts[0] = iterations;
					maxCounts[1] = creature.getObjectTemplate().getTemplateId();
				}
			}
		}
	}

	public String[] dumpBroadcastStats() {
		List<String> lines = new ArrayList<String>();
		lines.add("------- Movement broadcast counts -------");
		for (Entry<Integer, int[]> entry : moveBroadcastCounts.entrySet()) {
			lines.add("WorldId=" + entry.getKey() + ": " + entry.getValue()[0] + " (NpcId " + entry.getValue()[1] + ")");
		}
		lines.add("-----------------------------------------");
		return lines.toArray(new String[0]);
	}

	@Override
	protected String getCalledMethodName() {
		return "notifyOnMove()";
	}

	private class MoveNotifier implements VisitorWithOwner<Npc, VisibleObject> {

		@Override
		public void visit(Npc object, VisibleObject owner) {

			if (object.getAi2().getState() == AIState.DIED || object.getLifeStats().isAlreadyDead()) {
				if (object.getAi2().isLogging()) {
					AI2Logger.moveinfo(object, "WARN: NPC died but still in knownlist");
				}
				return;
			}
			object.getAi2().onCreatureEvent(AIEventType.CREATURE_MOVED, (Creature) owner);
		}

	}
}
