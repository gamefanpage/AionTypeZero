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

package org.typezero.gameserver.services;

import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.stats.container.CreatureLifeStats;
import org.typezero.gameserver.model.stats.container.PlayerLifeStats;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import java.util.concurrent.Future;

/**
 * @author ATracer
 */
public class LifeStatsRestoreService {

	private static final int DEFAULT_DELAY = 6000;
	private static final int DEFAULT_FPREDUCE_DELAY = 2000;
	private static final int DEFAULT_FPRESTORE_DELAY = 2000;

	private static LifeStatsRestoreService instance = new LifeStatsRestoreService();

	/**
	 * HP and MP restoring task
	 *
	 * @param creature
	 * @return Future<?>
	 */
	public Future<?> scheduleRestoreTask(CreatureLifeStats<? extends Creature> lifeStats) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new HpMpRestoreTask(lifeStats), 1700, DEFAULT_DELAY);
	}

	/**
	 * HP restoring task
	 *
	 * @param lifeStats
	 * @return
	 */
	public Future<?> scheduleHpRestoreTask(CreatureLifeStats<? extends Creature> lifeStats) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new HpRestoreTask(lifeStats), 1700, DEFAULT_DELAY);
	}

	/**
	 * @param lifeStats
	 * @return
	 */
	public Future<?> scheduleFpReduceTask(final PlayerLifeStats lifeStats, Integer costFp) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FpReduceTask(lifeStats, costFp), 2000,
			DEFAULT_FPREDUCE_DELAY);
	}

	/**
	 * @param lifeStats
	 * @return
	 */
	public Future<?> scheduleFpRestoreTask(PlayerLifeStats lifeStats) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FpRestoreTask(lifeStats), 2000,
			DEFAULT_FPRESTORE_DELAY);
	}

	public static LifeStatsRestoreService getInstance() {
		return instance;
	}

	private static class HpRestoreTask implements Runnable {

		private CreatureLifeStats<?> lifeStats;

		private HpRestoreTask(CreatureLifeStats<?> lifeStats) {
			this.lifeStats = lifeStats;
		}

		@Override
		public void run() {
			boolean inWorld = World.getInstance().isInWorld(lifeStats.getOwner());
			if (!inWorld || lifeStats.isAlreadyDead() || lifeStats.isFullyRestoredHp() || lifeStats.getOwner().getAi2().getState().equals(AIState.FIGHT)) {
				lifeStats.cancelRestoreTask();
				lifeStats = null;
			}
			else {
				lifeStats.restoreHp();
			}
		}
	}

	private static class HpMpRestoreTask implements Runnable {

		private CreatureLifeStats<?> lifeStats;

		private HpMpRestoreTask(CreatureLifeStats<?> lifeStats) {
			this.lifeStats = lifeStats;
		}

		@Override
		public void run() {
			boolean inWorld = World.getInstance().isInWorld(lifeStats.getOwner());
			if (!inWorld || lifeStats.isAlreadyDead() || lifeStats.isFullyRestoredHpMp()) {
				lifeStats.cancelRestoreTask();
				lifeStats = null;
			}
			else {
				lifeStats.restoreHp();
				lifeStats.restoreMp();
			}
		}
	}

	private static class FpReduceTask implements Runnable {

		private PlayerLifeStats lifeStats;
		private Integer costFp;

		private FpReduceTask(PlayerLifeStats lifeStats, final Integer costFp) {
			this.lifeStats = lifeStats;
			this.costFp = costFp;
		}

		@Override
		public void run() {
			boolean inWorld = World.getInstance().isInWorld(lifeStats.getOwner());
			if (!inWorld || lifeStats.isAlreadyDead()){
				lifeStats.cancelFpReduce();
				lifeStats = null;
				return;
			}

			if (lifeStats.getCurrentFp() == 0) {
				if (lifeStats.getOwner().getFlyState() > 0) {
					lifeStats.getOwner().getFlyController().endFly(true);
				}
				else {
					lifeStats.triggerFpRestore();
				}
			}
			else {
				int reduceFp = lifeStats.getOwner().getFlyState() == 2 && lifeStats.getOwner().isInsideZoneType(ZoneType.FLY) ? 1 : 2;
				if (costFp != null) {
					reduceFp = costFp.intValue();
				}

				lifeStats.reduceFp(reduceFp);
				lifeStats.specialrestoreFp();
			}
		}
	}

	private static class FpRestoreTask implements Runnable {

		private PlayerLifeStats lifeStats;

		private FpRestoreTask(PlayerLifeStats lifeStats) {
			this.lifeStats = lifeStats;
		}

		@Override
		public void run() {
			if (lifeStats.isAlreadyDead() || lifeStats.isFlyTimeFullyRestored()) {
				lifeStats.cancelFpRestore();
				lifeStats = null;
			}
			else {
				lifeStats.restoreFp();
			}
		}
	}
}
