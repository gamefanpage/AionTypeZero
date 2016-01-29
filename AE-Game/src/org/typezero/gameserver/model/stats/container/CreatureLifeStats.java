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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.services.LifeStatsRestoreService;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public abstract class CreatureLifeStats<T extends Creature> {

	private static final Logger log = LoggerFactory.getLogger(CreatureLifeStats.class);
	protected int currentHp;
	protected int currentMp;
	protected boolean alreadyDead = false;
	protected T owner;
	private final Lock hpLock = new ReentrantLock();
	private final Lock mpLock = new ReentrantLock();
	protected final Lock restoreLock = new ReentrantLock();
	protected volatile Future<?> lifeRestoreTask;

	public CreatureLifeStats(T owner, int currentHp, int currentMp) {
		this.owner = owner;
		this.currentHp = currentHp;
		this.currentMp = currentMp;
	}

	public T getOwner() {
		return owner;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public int getCurrentMp() {
		return currentMp;
	}

	/**
	 * @return maxHp of creature according to stats
	 */
	public int getMaxHp() {
		int maxHp = this.getOwner().getGameStats().getMaxHp().getCurrent();
		if (maxHp == 0) {
			maxHp = 1;
			//log.warn("CHECKPOINT: maxhp is 0 :" + this.getOwner().getGameStats());
		}
		return maxHp;
	}

	/**
	 * @return maxMp of creature according to stats
	 */
	public int getMaxMp() {
		return this.getOwner().getGameStats().getMaxMp().getCurrent();
	}

	/**
	 * @return the alreadyDead There is no setter method cause life stats should
	 * be completely renewed on revive
	 */
	public boolean isAlreadyDead() {
		return alreadyDead;
	}

	/**
	 * This method is called whenever caller wants to absorb creatures's HP
	 *
	 * @param value
	 * @param attacker attacking creature or self
	 * @return currentHp
	 */
	public int reduceHp(int value, @Nonnull Creature attacker) {
		if (attacker == null)
			throw new NullArgumentException("attacker");

		boolean isDied = false;
		hpLock.lock();
		try {
			if (!alreadyDead) {
				int newHp = this.currentHp - value;

				if (newHp < 0) {
					newHp = 0;
					this.currentMp = 0;
					alreadyDead = true;
					isDied = true;
				}
				this.currentHp = newHp;
			}
		}
		finally {
			hpLock.unlock();
		}
		if (value != 0) {
			onReduceHp();
		}
		if (isDied) {
			getOwner().getController().onDie(attacker);
		}
		return currentHp;
	}

	/**
	 * This method is called whenever caller wants to absorb creatures's HP
	 *
	 * @param value
	 * @return currentMp
	 */
	public int reduceMp(int value) {
		mpLock.lock();
		try {
			int newMp = this.currentMp - value;

			if (newMp < 0)
				newMp = 0;

			this.currentMp = newMp;
		}
		finally {
			mpLock.unlock();
		}
		if (value != 0) {
			onReduceMp();
		}
		return currentMp;
	}

	protected void sendAttackStatusPacketUpdate(TYPE type, int value, int skillId, LOG log) {
		if (owner == null)// possible?
			return;
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_ATTACK_STATUS(owner, type, skillId, value, log));
	}

	/**
	 * This method is called whenever caller wants to restore creatures's HP
	 *
	 * @param value
	 * @return currentHp
	 */
	public int increaseHp(TYPE type, int value) {
		return this.increaseHp(type, value, 0, LOG.REGULAR);
	}

	public int increaseHp(TYPE type, int value, int skillId, LOG log) {
		boolean hpIncreased = false;

		if (this.getOwner().getEffectController().isAbnormalSet(AbnormalState.DISEASE))
			return currentHp;

		hpLock.lock();
		try {
			if (isAlreadyDead()) {
				return 0;
			}
			int newHp = this.currentHp + value;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (currentHp != newHp) {
				this.currentHp = newHp;
				hpIncreased = true;
			}
		}
		finally {
			hpLock.unlock();
		}

		if (hpIncreased) {
			onIncreaseHp(type, value, skillId, log);
		}
		return currentHp;
	}

	/**
	 * This method is called whenever caller wants to restore creatures's MP
	 *
	 * @param value
	 * @return currentMp
	 */
	public int increaseMp(TYPE type, int value) {
		return this.increaseMp(type, value, 0, LOG.REGULAR);
	}

	public int increaseMp(TYPE type, int value, int skillId, LOG log) {
		boolean mpIncreased = false;
		mpLock.lock();
		try {
			if (isAlreadyDead()) {
				return 0;
			}
			int newMp = this.currentMp + value;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}
			if (currentMp != newMp) {
				this.currentMp = newMp;
				mpIncreased = true;
			}
		}
		finally {
			mpLock.unlock();
		}

		if (mpIncreased) {
			onIncreaseMp(type, value, skillId, log);
		}
		return currentMp;
	}

	/**
	 * Restores HP with value set as HP_RESTORE_TICK
	 */
	public final void restoreHp() {
		increaseHp(TYPE.NATURAL_HP, getOwner().getGameStats().getHpRegenRate().getCurrent());
	}

	/**
	 * Restores HP with value set as MP_RESTORE_TICK
	 */
	public final void restoreMp() {
		increaseMp(TYPE.NATURAL_MP, getOwner().getGameStats().getMpRegenRate().getCurrent());
	}

	/**
	 * Will trigger restore task if not already
	 */
	public void triggerRestoreTask() {
		restoreLock.lock();
		try {
			if (lifeRestoreTask == null && !alreadyDead) {
				lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
			}
		}
		finally {
			restoreLock.unlock();
		}

	}

	/**
	 * Cancel currently running restore task
	 */
	public void cancelRestoreTask() {
		restoreLock.lock();
		try {
			if (lifeRestoreTask != null) {
				lifeRestoreTask.cancel(false);
				lifeRestoreTask = null;
			}
		}
		finally {
			restoreLock.unlock();
		}
	}

	/**
	 * @return true or false
	 */
	public boolean isFullyRestoredHpMp() {
		return getMaxHp() == currentHp && getMaxMp() == currentMp;
	}

	/**
	 * @return
	 */
	public boolean isFullyRestoredHp() {
		return getMaxHp() == currentHp;
	}

	public boolean isFullyRestoredMp() {
		return getMaxMp() == currentMp;
	}

	/**
	 * The purpose of this method is synchronize current HP and MP with updated
	 * MAXHP and MAXMP stats This method should be called only on creature load
	 * to game or player level up
	 */
	public void synchronizeWithMaxStats() {
		int maxHp = getMaxHp();
		if (currentHp != maxHp)
			currentHp = maxHp;
		int maxMp = getMaxMp();
		if (currentMp != maxMp)
			currentMp = maxMp;
	}

	/**
	 * The purpose of this method is synchronize current HP and MP with MAXHP
	 * and MAXMP when max stats were decreased below current level
	 */
	public void updateCurrentStats() {
		int maxHp = getMaxHp();
		if (maxHp < currentHp)
			currentHp = maxHp;

		int maxMp = getMaxMp();
		if (maxMp < currentMp)
			currentMp = maxMp;

		if (!isFullyRestoredHpMp())
			triggerRestoreTask();
	}

	/**
	 * @return HP percentage 0 - 100
	 */
	public int getHpPercentage() {
		return (int) (100f * currentHp / getMaxHp());
	}

	/**
	 * @return MP percentage 0 - 100
	 */
	public int getMpPercentage() {
		return (int) (100f * currentMp / getMaxMp());
	}

	protected abstract void onIncreaseMp(TYPE type, int value, int skillId, LOG log);

	protected abstract void onReduceMp();

	protected abstract void onIncreaseHp(TYPE type, int value, int skillId, LOG log);

	protected abstract void onReduceHp();

	/**
	 * @param type
	 * @param value
	 * @return
	 */
	public int increaseFp(TYPE type, int value) {
		return 0;
	}

	public int getMaxFp() {
		return 0;
	}

	/**
	 * @return
	 */
	public int getCurrentFp() {
		return 0;
	}

	/**
	 * Cancel all tasks when player logout
	 */
	public void cancelAllTasks() {
		cancelRestoreTask();
	}

	/**
	 * This method can be used for Npc's to fully restore its HP and remove dead
	 * state of lifestats
	 *
	 * @param hpPercent
	 */
	public void setCurrentHpPercent(int hpPercent) {
		hpLock.lock();
		try {
			this.currentHp = (int) (hpPercent / 100f * getMaxHp());

			if (this.currentHp > 0)
				this.alreadyDead = false;
		}
		finally {
			hpLock.unlock();
		}
	}

	/**
	 * @param hp
	 */
	public void setCurrentHp(int hp) {
		boolean hpNotAtMaxValue = false;
		hpLock.lock();
		try {
			this.currentHp = hp;

			if (this.currentHp > 0)
				this.alreadyDead = false;

			if (this.currentHp < getMaxHp())
				hpNotAtMaxValue = true;
		}
		finally {
			hpLock.unlock();
		}
		if (hpNotAtMaxValue) {
			onReduceHp();
		}
	}

	public int setCurrentMp(int value) {
		mpLock.lock();
		try {
			int newMp = value;

			if (newMp < 0)
				newMp = 0;

			this.currentMp = newMp;
		}
		finally {
			mpLock.unlock();
		}
		onReduceMp();
		return currentMp;
	}

	/**
	 * This method can be used for Npc's to fully restore its MP
	 *
	 * @param mpPercent
	 */
	public void setCurrentMpPercent(int mpPercent) {
		mpLock.lock();
		try {
			this.currentMp = (int) (mpPercent / 100f * getMaxMp());
		}
		finally {
			mpLock.unlock();
		}
	}

}
