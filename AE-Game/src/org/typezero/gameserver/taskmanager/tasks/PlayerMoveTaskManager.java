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

import javolution.util.FastMap;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.taskmanager.AbstractPeriodicTaskManager;

/**
 * @author ATracer
 */
public class PlayerMoveTaskManager extends AbstractPeriodicTaskManager {

	private final FastMap<Integer, Creature> movingPlayers = new FastMap<Integer, Creature>().shared();

	private PlayerMoveTaskManager() {
		super(200);
	}

	public void addPlayer(Creature player) {
		movingPlayers.put(player.getObjectId(), player);
	}

	public void removePlayer(Creature player) {
		movingPlayers.remove(player.getObjectId());
	}

	@Override
	public void run() {
		for (FastMap.Entry<Integer, Creature> e = movingPlayers.head(), mapEnd = movingPlayers.tail(); (e = e.getNext()) != mapEnd;) {
			Creature player = e.getValue();
			player.getMoveController().moveToDestination();
		}
	}

	public static final PlayerMoveTaskManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static final class SingletonHolder {

		private static final PlayerMoveTaskManager INSTANCE = new PlayerMoveTaskManager();
	}
}
