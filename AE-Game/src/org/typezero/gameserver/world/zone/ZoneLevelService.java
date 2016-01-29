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

package org.typezero.gameserver.world.zone;

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class ZoneLevelService {

	private static final long DROWN_PERIOD = 2000;

	/**
	 * Check water level (start drowning) and map death level (die)
	 */
	public static void checkZoneLevels(Player player) {
		World world = World.getInstance();
		float z = player.getZ();

		if (player.getLifeStats().isAlreadyDead())
			return;

		if (z < world.getWorldMap(player.getWorldId()).getDeathLevel()) {
			player.getController().die();
			return;
		}

		// TODO need fix character height
		float playerheight = player.getPlayerAppearance().getHeight() * 1.6f;
		if (z < world.getWorldMap(player.getWorldId()).getWaterLevel() - playerheight)
			startDrowning(player);
		else
			stopDrowning(player);
	}

	/**
	 * @param player
	 */
	private static void startDrowning(Player player) {
		if (!isDrowning(player))
			scheduleDrowningTask(player);
	}

	/**
	 * @param player
	 */
	private static void stopDrowning(Player player) {
		if (isDrowning(player))
			player.getController().cancelTask(TaskId.DROWN);

	}

	/**
	 * @param player
	 * @return
	 */
	private static boolean isDrowning(Player player) {
		return player.getController().getTask(TaskId.DROWN) == null ? false : true;
	}

	/**
	 * @param player
	 */
	private static void scheduleDrowningTask(final Player player) {
		player.getController().addTask(TaskId.DROWN, ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				int value = Math.round(player.getLifeStats().getMaxHp() / 10);
				// TODO retail emotion, attack_status packets sending
				if (!player.getLifeStats().isAlreadyDead()) {
					if (!player.isInvul()) {
						player.getLifeStats().reduceHp(value, player);
						player.getLifeStats().sendHpPacketUpdate();
					}
				}
				else
					stopDrowning(player);
			}
		}, 0, DROWN_PERIOD));
	}
}
