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

package org.typezero.gameserver.model.stats.container;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.network.aion.serverpackets.SM_FLY_TIME;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_HP;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import org.typezero.gameserver.services.LifeStatsRestoreService;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import org.typezero.gameserver.taskmanager.tasks.TeamEffectUpdater;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ATracer, sphinx
 */
public class PlayerLifeStats extends CreatureLifeStats<Player> {

	protected int currentFp;
	private final ReentrantLock fpLock = new ReentrantLock();

	private Future<?> flyRestoreTask;
	private Future<?> flyReduceTask;

	public PlayerLifeStats(Player owner) {
		super(owner, owner.getGameStats().getMaxHp().getCurrent(), owner.getGameStats().getMaxMp().getCurrent());
		this.currentFp = owner.getGameStats().getFlyTime().getCurrent();
	}

	@Override
	protected void onReduceHp() {
		sendHpPacketUpdate();
		triggerRestoreTask();
		sendGroupPacketUpdate();
	}

	@Override
	protected void onReduceMp() {
		sendMpPacketUpdate();
		triggerRestoreTask();
		sendGroupPacketUpdate();
	}

	@Override
	protected void onIncreaseMp(TYPE type, int value, int skillId, LOG log) {
		if (value > 0) {
			sendMpPacketUpdate();
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			sendGroupPacketUpdate();
		}
	}

	@Override
	protected void onIncreaseHp(TYPE type, int value, int skillId, LOG log) {
		if (this.isFullyRestoredHp()) {
			// FIXME: Temp Fix: Reset aggro list when hp is full.
			this.owner.getAggroList().clear();
		}
		if (value > 0) {
			sendHpPacketUpdate();
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			sendGroupPacketUpdate();
		}
	}

	private void sendGroupPacketUpdate() {
		if (owner.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(owner);
		}
	}

	@Override
	public void synchronizeWithMaxStats() {
		if (isAlreadyDead())
			return;

		super.synchronizeWithMaxStats();
		int maxFp = getMaxFp();
		if (currentFp != maxFp)
			currentFp = maxFp;
	}

	@Override
	public void updateCurrentStats() {
		super.updateCurrentStats();

		if (getMaxFp() < currentFp)
			currentFp = getMaxFp();

		if (!owner.isFlying() && !owner.isInSprintMode())
			triggerFpRestore();
	}

	public void sendHpPacketUpdate() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_HP_STAT);
	}

	public void sendHpPacketUpdateImpl() {
		PacketSendUtility.sendPacket(owner, new SM_STATUPDATE_HP(currentHp, getMaxHp()));
	}

	public void sendMpPacketUpdate() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_MP_STAT);
	}

	public void sendMpPacketUpdateImpl() {
		PacketSendUtility.sendPacket(owner, new SM_STATUPDATE_MP(currentMp, getMaxMp()));
	}

	/**
	 * @return the currentFp
	 */
	@Override
	public int getCurrentFp() {
		return this.currentFp;
	}

	@Override
	public int getMaxFp() {
		return owner.getGameStats().getFlyTime().getCurrent();
	}

	/**
	 * @return FP percentage 0 - 100
	 */
	public int getFpPercentage() {
		return 100 * currentFp / getMaxFp();
	}

	/**
	 * This method is called whenever caller wants to restore creatures's FP
	 *
	 * @param value
	 * @return
	 */
	@Override
	public int increaseFp(TYPE type, int value) {
		return this.increaseFp(type, value, 0, LOG.REGULAR);
	}
	public int increaseFp(TYPE type, int value, int skillId, LOG log) {
		fpLock.lock();

		try {
			if (isAlreadyDead()) {
				return 0;
			}
			int newFp = this.currentFp + value;
			if (newFp > getMaxFp()) {
				newFp = getMaxFp();
			}
			if (currentFp != newFp) {
				onIncreaseFp(type, newFp - currentFp, skillId, log);
				this.currentFp = newFp;
			}
		}
		finally {
			fpLock.unlock();
		}

		return currentFp;

	}

	/**
	 * This method is called whenever caller wants to reduce creatures's MP
	 *
	 * @param value
	 * @return
	 */
	public int reduceFp(int value) {
		fpLock.lock();
		try {
			int newFp = this.currentFp - value;

			if (newFp < 0)
				newFp = 0;

			this.currentFp = newFp;
		}
		finally {
			fpLock.unlock();
		}

		onReduceFp();

		return currentFp;
	}

	public int setCurrentFp(int value) {
		fpLock.lock();
		try {
			int newFp = value;

			if (newFp < 0)
				newFp = 0;

			this.currentFp = newFp;
		}
		finally {
			fpLock.unlock();
		}

		onReduceFp();

		return currentFp;
	}

	protected void onIncreaseFp(TYPE type, int value, int skillId, LOG log) {
		if (value > 0) {
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_FLY_TIME);
		}
	}

	protected void onReduceFp() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_FLY_TIME);
	}

	public void sendFpPacketUpdateImpl() {
		if (owner == null)
			return;

		PacketSendUtility.sendPacket(owner, new SM_FLY_TIME(currentFp, getMaxFp()));
	}

	/**
	 * this method should be used only on FlyTimeRestoreService
	 */
	public void restoreFp() {
		// how much fly time restoring per 2 second.
		increaseFp(TYPE.NATURAL_FP, 1);
	}

    public void specialrestoreFp() {
        if (owner.getGameStats().getStat(StatEnum.REGEN_FP, 0).getCurrent() != 0)
            increaseFp(TYPE.NATURAL_FP, owner.getGameStats().getStat(StatEnum.REGEN_FP, 0).getCurrent() / 3);
    }

	public void triggerFpRestore() {
		cancelFpReduce();

		restoreLock.lock();
		try {
			if (flyRestoreTask == null && !alreadyDead && !isFlyTimeFullyRestored()) {
				this.flyRestoreTask = LifeStatsRestoreService.getInstance().scheduleFpRestoreTask(this);
			}
		}
		finally {
			restoreLock.unlock();
		}
	}

	public void cancelFpRestore() {
		restoreLock.lock();
		try {
			if (flyRestoreTask != null && !flyRestoreTask.isCancelled()) {
				flyRestoreTask.cancel(false);
				this.flyRestoreTask = null;
			}
		}
		finally {
			restoreLock.unlock();
		}
	}

	public void triggerFpReduceByCost(Integer costFp) {
		triggerFpReduce(costFp);
	}

	public void triggerFpReduce() {
		triggerFpReduce(null);
	}

	private void triggerFpReduce(Integer costFp) {
		cancelFpRestore();
		restoreLock.lock();
		try {
			if (flyReduceTask == null && !alreadyDead && owner.getAccessLevel() < AdminConfig.GM_FLIGHT_UNLIMITED
				&& !owner.isUnderNoFPConsum()) {
				this.flyReduceTask = LifeStatsRestoreService.getInstance().scheduleFpReduceTask(this, costFp);
			}
		}
		finally {
			restoreLock.unlock();
		}
	}

	public void cancelFpReduce() {
		restoreLock.lock();
		try {
			if (flyReduceTask != null && !flyReduceTask.isCancelled()) {
				flyReduceTask.cancel(false);
				this.flyReduceTask = null;
			}
		}
		finally {
			restoreLock.unlock();
		}
	}

	public boolean isFlyTimeFullyRestored() {
		return getMaxFp() == currentFp;
	}

	@Override
	public void cancelAllTasks() {
		super.cancelAllTasks();
		cancelFpReduce();
		cancelFpRestore();
	}

	public void triggerRestoreOnRevive() {
		this.triggerRestoreTask();
		triggerFpRestore();
	}
}
