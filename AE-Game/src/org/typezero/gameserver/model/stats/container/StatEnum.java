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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.items.ItemSlot;

/**
 * @author xavier
 * @author ATracer
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum {

	MAXDP(22), // Maximum DP
	MAXHP(18), // HP
	MAXMP(20), // MP

	AGILITY(9, true),
	BLOCK(33),
	EVASION(31),
	CONCENTRATION(41),
	WILL(11, true),
	HEALTH(7, true),
	ACCURACY(8, true),
	KNOWLEDGE(10, true),
	PARRY(32),
	POWER(6, true),
	SPEED(36, true),
	ALLSPEED,
	WEIGHT(39, true),
	HIT_COUNT(35, true),

	ATTACK_RANGE(38, true), // Atk Range
	ATTACK_SPEED(29, -1, true), // Atk Speed
	PHYSICAL_ATTACK(25), // Attack
	PHYSICAL_ACCURACY(30), // Accuracy
	PHYSICAL_CRITICAL(34), // Critical Strike
	PHYSICAL_DEFENSE(26), // Physical Def
	MAIN_HAND_HITS,
	MAIN_HAND_ACCURACY,
	MAIN_HAND_CRITICAL,
	MAIN_HAND_POWER,
	MAIN_HAND_ATTACK_SPEED,
	OFF_HAND_HITS,
	OFF_HAND_ACCURACY,
	OFF_HAND_CRITICAL,
	OFF_HAND_POWER,
	OFF_HAND_ATTACK_SPEED,

	MAGICAL_ATTACK(27), // Magical Attack
	MAIN_HAND_MAGICAL_ATTACK,
	OFF_HAND_MAGICAL_ATTACK,
	MAGICAL_ACCURACY(105),
	MAIN_HAND_MAGICAL_ACCURACY,
	OFF_HAND_MAGICAL_ACCURACY,
	MAGICAL_CRITICAL(40), // Critical Spell
	MAGICAL_RESIST(28), // Magic Resist
	MAX_DAMAGES,
	MIN_DAMAGES,

	EARTH_RESISTANCE(14),
	FIRE_RESISTANCE(15),
	WIND_RESISTANCE(13),
	WATER_RESISTANCE(12),
	DARK_RESISTANCE(17),
	LIGHT_RESISTANCE(16),

	BOOST_MAGICAL_SKILL(104),
	BOOST_SPELL_ATTACK,
	BOOST_CASTING_TIME(108), // Casting Speed
	BOOST_CASTING_TIME_HEAL,
	BOOST_CASTING_TIME_TRAP,
	BOOST_CASTING_TIME_ATTACK,
	BOOST_CASTING_TIME_SKILL,
	BOOST_CASTING_TIME_SUMMONHOMING,
	BOOST_CASTING_TIME_SUMMON,
	BOOST_HATE(109), // Enmity Boost

	FLY_TIME(23),
	FLY_SPEED(37),

	DAMAGE_REDUCE,
	DAMAGE_REDUCE_MAX,

	BLEED_RESISTANCE(44), // Bleed Resist
	BLIND_RESISTANCE(48), // Blind Resist
	BIND_RESISTANCE,
	CHARM_RESISTANCE(49), // Charm Resist
	CONFUSE_RESISTANCE(54), // Confusion Resist
	CURSE_RESISTANCE(53), // Curse Resist
	DISEASE_RESISTANCE(50), // Disease Resist
	DEFORM_RESISTANCE,
	FEAR_RESISTANCE(52), // Fear Resist
	OPENAREIAL_RESISTANCE(59), // Aether's Hold Resist
	PARALYZE_RESISTANCE(45), // Paralysis Resistance
	PERIFICATION_RESISTANCE(56), // Petrification Resist
	POISON_RESISTANCE(43), // Poison Resist
	PULLED_RESISTANCE, // TODO: Find ID !!!
	ROOT_RESISTANCE(47), // Immobilization Resist
	SILENCE_RESISTANCE(51),
	SLEEP_RESISTANCE(46), // Sleep Resist
	SLOW_RESISTANCE(60), // Reduce Speed Resist
	SNARE_RESISTANCE(61), // Reduce Attack Speed Resist
	SPIN_RESISTANCE(62), // Spin Resist
	STAGGER_RESISTANCE(58), // Knock Back Resist
	STUMBLE_RESISTANCE(57), // Stumble Resist
	STUN_RESISTANCE(55), // Stun Resist

	SILENCE_RESISTANCE_PENETRATION(77), // Silence Resistance Penetration
	PARALYZE_RESISTANCE_PENETRATION(71), // Paralysis Resistance Penetration
	POISON_RESISTANCE_PENETRATION(69), // Poisoning Penetration
	BLEED_RESISTANCE_PENETRATION(70), // Bleeding Penetration
	SLEEP_RESISTANCE_PENETRATION(72), // Sleep Penetration
	ROOT_RESISTANCE_PENETRATION(73), // Immobilization Penetration
	BLIND_RESISTANCE_PENETRATION(74), // Blindness Penetration
	CHARM_RESISTANCE_PENETRATION(75), // Char Penetration
	DISEASE_RESISTANCE_PENETRATION(76), // Disease Penetration
	FEAR_RESISTANCE_PENETRATION(78), // Fear Penetration
	SPIN_RESISTANCE_PENETRATION(88), // Spin Penetration
	CURSE_RESISTANCE_PENETRATION(79), // Curse Penetration
	CONFUSE_RESISTANCE_PENETRATION(80), // Confusion Penetration
	STUN_RESISTANCE_PENETRATION(81), // Stun Penetration
	PERIFICATION_RESISTANCE_PENETRATION(82), // Petrification Penetration
	STUMBLE_RESISTANCE_PENETRATION(83), // Stumble Penetration
	STAGGER_RESISTANCE_PENETRATION(84), // Knock Back Penetration
	OPENAREIAL_RESISTANCE_PENETRATION(85), // Aether's Hold Penetration
	SNARE_RESISTANCE_PENETRATION(87), // Reduce Attack Speed Penetration
	SLOW_RESISTANCE_PENETRATION(86), // Reduce Movement Speed Penetration

	REGEN_MP(21), // Natural Mana Treatment
	REGEN_HP(19), // Natural Healing
	REGEN_FP(24), // Natural Flight Serum

	HEAL_BOOST(110), // Healing Boost, not BOOST_CASTING_TIME_HEAL ?
	ALLRESIST(2), // All Stats ?
	STUNLIKE_RESISTANCE,
	ELEMENTAL_RESISTANCE_DARK,
	ELEMENTAL_RESISTANCE_LIGHT,
	MAGICAL_CRITICAL_RESIST(116), // Spell Resist
	MAGICAL_CRITICAL_DAMAGE_REDUCE(118), // Spell Fortitude
	PHYSICAL_CRITICAL_RESIST(115), // Strike Resist
	PHYSICAL_CRITICAL_DAMAGE_REDUCE(117), // Strike Fortitude
	ERFIRE,
	ERAIR,
	EREARTH,
	ERWATER,
	ABNORMAL_RESISTANCE_ALL(1), // All Altered State Resist ?
	ALLPARA,
	KNOWIL(4), // Knowledge and Will
	AGIDEX(5), // Accuracy and Agility
	STRVIT(3), // Power and Health

	MAGICAL_DEFEND(125), // Magical Defense
	MAGIC_SKILL_BOOST_RESIST(126), // Magic Supression

  //Effects stats (bossts, deboosts)
	HEAL_SKILL_BOOST,
	HEAL_SKILL_DEBOOST,
	BOOST_HUNTING_XP_RATE,
	BOOST_GROUP_HUNTING_XP_RATE,
	BOOST_QUEST_XP_RATE,

	BOOST_CRAFTING_XP_RATE, // for all craft skills
	BOOST_COOKING_XP_RATE,
	BOOST_WEAPONSMITHING_XP_RATE,
	BOOST_ARMORSMITHING_XP_RATE,
	BOOST_TAILORING_XP_RATE,
	BOOST_ALCHEMY_XP_RATE,
	BOOST_HANDICRAFTING_XP_RATE,
	BOOST_MENUISIER_XP_RATE,

	BOOST_GATHERING_XP_RATE, // for all gathering skills
	BOOST_AETHERTAPPING_XP_RATE,
	BOOST_ESSENCETAPPING_XP_RATE,

	BOOST_DROP_RATE,
	BOOST_MANTRA_RANGE,
	BOOST_DURATION_BUFF, // extend_duration
	BOOST_RESIST_DEBUFF,

	// 3.5
	ELEMENTAL_FIRE,

	// PvP and PvE
	PVP_PHYSICAL_ATTACK(111),
	PVP_PHYSICAL_DEFEND(112),
	PVP_MAGICAL_ATTACK(113),
	PVP_MAGICAL_DEFEND(114),

	PVP_ATTACK_RATIO(106),
	PVP_ATTACK_RATIO_MAGICAL,
	PVP_ATTACK_RATIO_PHYSICAL,
	PVP_DEFEND_RATIO(107),
	PVP_DEFEND_RATIO_PHYSICAL,
	PVP_DEFEND_RATIO_MAGICAL,

	PVE_ATTACK_RATIO,
	PVE_ATTACK_RATIO_MAGICAL,
	PVE_ATTACK_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO,
	PVE_DEFEND_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO_MAGICAL,

	AP_BOOST,
        DR_BOOST,
        // 4.3
        PVPATTACKRATIO_MAGICAL,
        PVPATTACKRATIO_PHYSICAL,
        BOOST_CHARGE_TIME,
        // 4.5
        PROC_REDUCE_RATE,
        PHYSICAL_DAMAGE,
        MAGICAL_DAMAGE,
        PHYSICAL_CRITICAL_REDUCE_RATE,
        UNK_STAT,
        // 4.7
        PVP_EVASION,
        PVP_BLOCK,
        PVP_PARRY,
        PVP_PHYSICAL_ACCURACY,
        PVP_MAGICAL_RESIST,
        PVP_MAGICAL_ACCURACY,
        BLOCK_PENETRATION;
	// If STAT id = 135 - Shrewd Cloth Set oOo
	// Checked up to 160 in 3.5

	private boolean replace;
	private int sign;

	private int itemStoneMask;

	private StatEnum() {
		this(0);
	}

	private StatEnum(int stoneMask) {
		this(stoneMask, 1, false);
	}

	private StatEnum(int stoneMask, boolean replace) {
		this(stoneMask, 1, replace);
	}

	private StatEnum(int stoneMask, int sign) {
		this(stoneMask, sign, false);
	}

	private StatEnum(int stoneMask, int sign, boolean replace) {
		this.itemStoneMask = stoneMask;
		this.replace = replace;
		this.sign = sign;
	}

	public int getSign() {
		return sign;
	}

	/**
	 * @return the itemStoneMask
	 */
	public int getItemStoneMask() {
		return itemStoneMask;
	}

	/**
	 * Used to find specific StatEnum by its item stone mask
	 *
	 * @param mask
	 * @return StatEnum
	 */
	public static StatEnum findByItemStoneMask(int mask) {
		for (StatEnum sEnum : values()) {
			if (sEnum.getItemStoneMask() == mask) {
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for stone mask: " + mask);
	}

	public StatEnum getHandStat(long itemSlot) {
		switch (this) {
			case MAGICAL_ATTACK:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ATTACK : OFF_HAND_MAGICAL_ATTACK;
			case MAGICAL_ACCURACY:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ACCURACY : OFF_HAND_MAGICAL_ACCURACY;
			case PHYSICAL_ATTACK:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_POWER : OFF_HAND_POWER;
			case PHYSICAL_ACCURACY:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_ACCURACY : OFF_HAND_ACCURACY;
			case PHYSICAL_CRITICAL:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_CRITICAL : OFF_HAND_CRITICAL;
			default:
				return this;
		}
	}

	public boolean isMainOrSubHandStat() {
		switch (this) {
			case MAGICAL_ATTACK:
			case MAGICAL_ACCURACY:
			case PHYSICAL_ATTACK:
			case POWER:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
				return true;

			default:
				return false;
		}
	}

	public boolean isReplace() {
		return replace;
	}

	public static StatEnum getModifier(int skillId) {
	  switch (skillId) {
	   case 30001:
	   case 30002:
	    return BOOST_ESSENCETAPPING_XP_RATE;
	   case 30003:
	    return BOOST_AETHERTAPPING_XP_RATE;
	   case 40001:
	    return BOOST_COOKING_XP_RATE;
	   case 40002:
	    return BOOST_WEAPONSMITHING_XP_RATE;
	   case 40003:
	    return BOOST_ARMORSMITHING_XP_RATE;
	   case 40004:
	    return BOOST_TAILORING_XP_RATE;
	   case 40007:
	    return BOOST_ALCHEMY_XP_RATE;
	   case 40008:
	    return BOOST_HANDICRAFTING_XP_RATE;
	   case 40010:
	    return BOOST_MENUISIER_XP_RATE;
	   default:
	    return null;
	  }
	 }
}
