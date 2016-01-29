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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Logger;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.SummonedObject;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.templates.npc.NpcRating;
import org.typezero.gameserver.model.templates.stats.NpcStatsTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class NpcGameStats extends CreatureGameStats<Npc> {

	int currentRunSpeed = 0;
	private long lastAttackTime = 0;
	private long lastAttackedTime = 0;
	private long nextAttackTime = 0;
	private long lastSkillTime = 0;
	private long fightStartingTime = 0;
	private int cachedState;
	private Stat2 cachedSpeedStat;
	private long lastGeoZUpdate;
	private long lastChangeTarget = 0;
	private int pAccuracy = 0;
	private int mRes = 0;

	public NpcGameStats(Npc owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		checkSpeedStats();
	}

	private void checkSpeedStats() {
		Stat2 oldSpeed = cachedSpeedStat;
		cachedSpeedStat = null;
		Stat2 newSpeed = getMovementSpeed();
		cachedSpeedStat = newSpeed;
		if (oldSpeed == null || oldSpeed.getCurrent() != newSpeed.getCurrent()) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
	}

	@Override
	public Stat2 getMaxHp() {
		return getStat(StatEnum.MAXHP, owner.getObjectTemplate().getStatsTemplate().getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		return getStat(StatEnum.MAXMP, owner.getObjectTemplate().getStatsTemplate().getMaxMp());
	}

	@Override
	public Stat2 getAttackSpeed() {
		return getStat(StatEnum.ATTACK_SPEED, owner.getObjectTemplate().getAttackDelay());
	}

	@Override
	public Stat2 getPCR() {
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
	}

	@Override
	public Stat2 getMCR() {
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
	}

	@Override
	public Stat2 getHealBoost() {
		return getStat(StatEnum.HEAL_BOOST, 0);
	}

	@Override
	public Stat2 getAllSpeed() {
		int base = 7500; //TODO current value
		return getStat(StatEnum.ALLSPEED, base);
	}

	@Override
	public Stat2 getMovementSpeed() {
		int currentState = owner.getState();
		Stat2 cachedSpeed = cachedSpeedStat;
		if (cachedSpeed != null && cachedState == currentState) {
			return cachedSpeed;
		}
		Stat2 newSpeedStat = null;
		if (owner.isFlying()) {
			newSpeedStat = getStat(StatEnum.FLY_SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1.3f * 1000));
		}
		else if (owner.isInState(CreatureState.WEAPON_EQUIPPED)) {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeedFight() * 1000));
		}
		else if (owner.isInState(CreatureState.WALKING)) {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed() * 1000));
		}
		else {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1000));
		}
		cachedState = currentState;
		cachedSpeedStat = newSpeedStat;
		return newSpeedStat;
	}

	@Override
	public Stat2 getAttackRange() {
		return getStat(StatEnum.ATTACK_RANGE, owner.getObjectTemplate().getAttackRange() * 1000);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, owner.getObjectTemplate().getStatsTemplate().getPdef());
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}

	@Override
	public Stat2 getMResist() {
		if (mRes == 0){
			mRes = Math.round(owner.getLevel()*17.5f+75);
		}
		return getStat(StatEnum.MAGICAL_RESIST, mRes);
	}

	@Override
	public Stat2 getMBResist() {
		int base = 0;
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getPower() {
		return getStat(StatEnum.POWER, 100);
	}

	@Override
	public Stat2 getHealth() {
		return getStat(StatEnum.HEALTH, 100);
	}

	@Override
	public Stat2 getAccuracy() {
		return getStat(StatEnum.ACCURACY, 100);
	}

	@Override
	public Stat2 getAgility() {
		return getStat(StatEnum.AGILITY, 100);
	}

	@Override
	public Stat2 getKnowledge() {
		return getStat(StatEnum.KNOWLEDGE, 100);
	}

	@Override
	public Stat2 getWill() {
		return getStat(StatEnum.WILL, 100);
	}

	@Override
	public Stat2 getEvasion() {
		if (pAccuracy == 0)
			calcStats();
		return getStat(StatEnum.EVASION, pAccuracy);
	}

	@Override
	public Stat2 getParry() {
		return getStat(StatEnum.PARRY, 100);
	}

	@Override
	public Stat2 getBlock() {
		return getStat(StatEnum.BLOCK, 0);
	}

	@Override
	public Stat2 getMainHandPAttack() {
		return getStat(StatEnum.PHYSICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMainHandAttack());
	}

	@Override
	public Stat2 getMainHandPCritical() {
		return getStat(StatEnum.PHYSICAL_CRITICAL, 10);
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		if (pAccuracy == 0)
			calcStats();
		return getStat(StatEnum.PHYSICAL_ACCURACY, pAccuracy);
	}

	@Override
	public Stat2 getMainHandMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getPower());
	}

	@Override
	public Stat2 getMBoost() {
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 100);
	}

	@Override
	public Stat2 getMainHandMAccuracy() {
		if (pAccuracy == 0)
			calcStats();
		// Trap's MAccuracy is being calculated into TrapGameStats and is related to master's MAccuracy
		if (owner instanceof SummonedObject)
			return getStat(StatEnum.MAGICAL_ACCURACY, pAccuracy);
		return getMainHandPAccuracy();
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}

	@Override
	public Stat2 getHpRegenRate() {
		NpcStatsTemplate nst = owner.getObjectTemplate().getStatsTemplate();
		return getStat(StatEnum.REGEN_HP, nst.getMaxHp() / 4);
	}

	@Override
	public Stat2 getMpRegenRate() {
		throw new IllegalStateException("No mp regen for NPC");
	}

	public int getLastAttackTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackTime) / 1000f);
	}

	public int getLastAttackedTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackedTime) / 1000f);
	}

	public void renewLastAttackTime() {
		this.lastAttackTime = System.currentTimeMillis();
	}

	public void renewLastAttackedTime() {
		this.lastAttackedTime = System.currentTimeMillis();
	}

	public boolean isNextAttackScheduled() {
		return nextAttackTime - System.currentTimeMillis() > 50;
	}

        public void setFightStartingTime() {
                this.fightStartingTime = System.currentTimeMillis();
        }

        public long getFightStartingTime() {
                return this.fightStartingTime;
        }

	public void setNextAttackTime(long nextAttackTime) {
		this.nextAttackTime = nextAttackTime;
	}

	/**
	 * @return next possible attack time depending on stats
	 */
	public int getNextAttackInterval() {
		long attackDelay = System.currentTimeMillis() - lastAttackTime;
		int attackSpeed = getAttackSpeed().getCurrent();
		if (attackSpeed == 0) {
			attackSpeed = 2000;
		}
		if (owner.getAi2().isLogging()) {
			AI2Logger.info(owner.getAi2(), "adelay = " + attackDelay + " aspeed = " + attackSpeed);
		}
		int nextAttack = 0;
		if (attackDelay < attackSpeed) {
			nextAttack = (int) (attackSpeed - attackDelay);
		}
		return nextAttack;
	}

	/**
	 * @return next possible skill time depending on time
	 */

	public void renewLastSkillTime() {
		this.lastSkillTime = System.currentTimeMillis();
	}

        //not used at the moment
	/*public void renewLastSkilledTime() {
		this.lastSkilledTime = System.currentTimeMillis();
	}*/

	public void renewLastChangeTargetTime() {
		this.lastChangeTarget = System.currentTimeMillis();
	}

	public int getLastSkillTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastSkillTime) / 1000f);
	}

        //not used at the moment
	/*public int getLastSkilledTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastSkilledTime) / 1000f);
	}*/

	public int getLastChangeTargetTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastChangeTarget) / 1000f);
	}

        //only use skills after a minimum cooldown of 3 to 9 seconds
        //TODO: Check wether this is a suitable time or not
	public boolean canUseNextSkill() {
		if (getLastSkillTimeDelta() >= 6 + Rnd.get(-3,3))
			return true;
		else
			return false;
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}

	public final long getLastGeoZUpdate() {
		return lastGeoZUpdate;
	}

	/**
	 * @param lastGeoZUpdate the lastGeoZUpdate to set
	 */
	public void setLastGeoZUpdate(long lastGeoZUpdate) {
		this.lastGeoZUpdate = lastGeoZUpdate;
	}

	private void calcStats() {
		int lvl = owner.getLevel();
		double accuracy = lvl * (33.6f - (0.16 * lvl)) + 5;

        NpcRating npcRating = owner.getObjectTemplate().getRating();
        /** switch (owner.getObjectTemplate().getRating()) Potentially dangerous use, u need to check the return value **/
        if(npcRating != null)
        {
            switch (npcRating) {
                case ELITE:
                    accuracy *= 1.15f;
                    break;
                case HERO:
                    accuracy *= 1.25f;
                    break;
                case LEGENDARY:
                    accuracy *= 1.35f;
                    break;
            }
        }

        /** mb need default value for accuracy multiplication ??? **/

		this.pAccuracy = Math.round(owner.getAi2().modifyMaccuracy((int) accuracy));  /** (int)Math.round(some) No need cast Math.round return value it is always (int) **/
	}
}
