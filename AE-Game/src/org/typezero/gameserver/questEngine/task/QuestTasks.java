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

package org.typezero.gameserver.questEngine.task;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.spawns.SpawnSearchResult;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneName;

import java.util.concurrent.Future;

/**
 * @author ATracer
 */
public class QuestTasks {
	/**
	 * Schedule new following checker task
	 *
	 * @param player
	 * @param npc
	 * @param target
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, Npc target) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new TargetDestinationChecker(npc, target)), 1000, 1000);
	}

	/**
	 * Schedule new following checker task
	 *
	 * @param player
	 * @param npc
	 * @param npcTargetId
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, int npcTargetId) {
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), npcTargetId);
		if (searchResult == null) {
			throw new IllegalArgumentException("Supplied npc doesn't exist: " + npcTargetId);
		}
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, searchResult.getSpot().getX(), searchResult
				.getSpot().getY(), searchResult.getSpot().getZ())), 1000, 1000);
	}

	/**
	 * Schedule new following checker task
	 *
	 * @param env
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, float x, float y, float z) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, x, y, z)), 1000, 1000);
	}

	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, ZoneName zoneName) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new ZoneChecker(npc, zoneName)), 1000, 1000);
	}
}
