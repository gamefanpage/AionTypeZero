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

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.templates.stats.SummonStatsTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class SummonGameStats extends CreatureGameStats<Summon> {

	private int cachedSpeed;
	private final SummonStatsTemplate statsTemplate;

	/**
	 * @param owner
	 * @param statsTemplate
	 */
	public SummonGameStats(Summon owner, SummonStatsTemplate statsTemplate) {
		super(owner);
		this.statsTemplate = statsTemplate;
	}

	@Override
	protected void onStatsChange() {
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		if (current != cachedSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
	}

	@Override
	public Stat2 getAllSpeed() {
		int base = 7500; //TODO current value
		return getStat(StatEnum.ALLSPEED, base);
	}

	@Override
	public Stat2 getStat(StatEnum statEnum, int base) {
		Stat2 stat = super.getStat(statEnum, base);
		if (owner.getMaster() == null)
			return stat;
		switch (statEnum) {
			case MAXHP:
				stat.setBonusRate(0.5f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case BOOST_MAGICAL_SKILL:
			case MAGICAL_ACCURACY:
				stat.setBonusRate(0.8f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_DEFENSE:
				stat.setBonusRate(0.3f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case EVASION:
			case PARRY:
			case MAGICAL_RESIST:
			case MAGICAL_CRITICAL:
				stat.setBonusRate(0.5f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ACCURACY:
				stat.setBonusRate(0.5f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_ACCURACY, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_CRITICAL:
				stat.setBonusRate(0.5f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_CRITICAL, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
		}
		return stat;
	}

	@Override
	public Stat2 getMaxHp() {
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxHp());
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
	public Stat2 getMaxMp() {
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxMp());
	}

	@Override
	public Stat2 getAttackSpeed() {
		return getStat(StatEnum.ATTACK_SPEED, owner.getObjectTemplate().getAttackDelay());
	}

	@Override
	public Stat2 getMovementSpeed() {
		int bonusSpeed = 0;
		Player master = owner.getMaster();
		if (master != null && (master.isInFlyingState() || master.isInState(CreatureState.GLIDING))) {
			bonusSpeed += 3000;
		}
		return getStat(StatEnum.SPEED, Math.round(statsTemplate.getRunSpeed() * 1000) + bonusSpeed);
	}

	@Override
	public Stat2 getAttackRange() {
		return getStat(StatEnum.ATTACK_RANGE, owner.getObjectTemplate().getAttackRange() * 1000);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, statsTemplate.getPdefense());
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, statsTemplate.getMresist());
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
		return getStat(StatEnum.PHYSICAL_ACCURACY, 100);
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
		return getStat(StatEnum.EVASION, statsTemplate.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		return getStat(StatEnum.PARRY, statsTemplate.getParry());
	}

	@Override
	public Stat2 getBlock() {
		return getStat(StatEnum.BLOCK, statsTemplate.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		return getStat(StatEnum.PHYSICAL_ATTACK, statsTemplate.getMainHandAttack());
	}

	@Override
	public Stat2 getMainHandPCritical() {
		return getStat(StatEnum.PHYSICAL_CRITICAL, statsTemplate.getMainHandCritRate());
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		return getStat(StatEnum.PHYSICAL_ACCURACY, statsTemplate.getMainHandAccuracy());
	}

	@Override
	public Stat2 getMainHandMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, 100);
	}

	@Override
	public Stat2 getMBoost() {
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 0);
	}

	@Override
	public Stat2 getMainHandMAccuracy() {
		return getStat(StatEnum.MAGICAL_ACCURACY, statsTemplate.getMagicAccuracy());
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, statsTemplate.getMcrit());
	}

	@Override
	public Stat2 getHpRegenRate() {
		int base = (int) (owner.getLifeStats().getMaxHp() * (owner.getMode().getId() == 2 ? 0.05f : 0.025f));
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public Stat2 getMpRegenRate() {
		throw new IllegalStateException("No mp regen for Summon");
	}

	@Override
	public void updateStatInfo() {
		Player master = owner.getMaster();
		if (master != null) {
			PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(owner));
		}
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}

}
