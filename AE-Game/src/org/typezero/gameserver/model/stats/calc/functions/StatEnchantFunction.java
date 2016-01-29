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

package org.typezero.gameserver.model.stats.calc.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.item.ArmorType;
import org.typezero.gameserver.model.templates.item.ItemCategory;

/**
 * @author ATracer (based on Mr.Poke EnchantModifier)
 */
public class StatEnchantFunction extends StatAddFunction {

	private static final Logger log = LoggerFactory.getLogger(StatEnchantFunction.class);

	private Item item;

	public StatEnchantFunction(Item owner, StatEnum stat) {
		this.stat = stat;
		this.item = owner;
	}

	@Override
	public final int getPriority() {
		return 30;
	}

	@Override
	public void apply(Stat2 stat) {
		if (!item.isEquipped())
			return;
		int enchantLvl = item.getEnchantLevel();
        if (item.getItemTemplate().isAccessory() || item.getItemTemplate().getCategory() == ItemCategory.HELMET) {
            enchantLvl = item.getAuthorize();
        }
		if (enchantLvl == 0)
			return;
		if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) != 0
			|| (item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) != 0)
			return;
		stat.addToBase(getEnchantAdditionModifier(item.getEnchantLevel(), stat));
        stat.addToBase(getEnchantAdditionModifier(item.getAuthorize(), stat));
	}

	private int getEnchantAdditionModifier(int enchantLvl, Stat2 stat) {
		if (item.getItemTemplate().isWeapon()) {
			return getWeaponModifiers(enchantLvl);
		}
        if (item.getItemTemplate().isAccessory() && !item.getItemTemplate().isPlume()) {
            return getAccessoryModifiers(enchantLvl);
        }
		if (item.getItemTemplate().isArmor() || item.getItemTemplate().isPlume()) {
			return getArmorModifiers(enchantLvl, stat);
		}
		return 0;
	}

	private int getWeaponModifiers(int enchantLvl) {
		switch (stat) {
			case MAIN_HAND_POWER:
			case OFF_HAND_POWER:
			case PHYSICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case DAGGER_1H:
					case SWORD_1H:
						return 2 * enchantLvl;
					case POLEARM_2H:
					case SWORD_2H:
					case BOW:
						return 4 * enchantLvl;
					case MACE_1H:
					case STAFF_2H:
						return 3 * enchantLvl;
				}
				return 0;
			case BOOST_MAGICAL_SKILL:
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case MACE_1H:
					case STAFF_2H:
					case ORB_2H:
					case GUN_1H:
					case CANNON_2H:
					case HARP_2H:
					case KEYBLADE_2H:
						return 20 * enchantLvl;
				}
				return 0;
			case MAGICAL_ATTACK:
			case OFF_HAND_MAGICAL_ATTACK:
			case MAIN_HAND_MAGICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case GUN_1H:
					case BOOK_2H:
					case ORB_2H:
						return 3 * enchantLvl;
					case CANNON_2H:
					case HARP_2H:
					case KEYBLADE_2H:
						return 4 * enchantLvl;
				}
				return 0;
			default:
				return 0;
		}
	}

    private int getAccessoryModifiers(int autorizeLvl) {
        switch (stat) {
            case PVP_ATTACK_RATIO:
                switch (autorizeLvl) {
                    case 1:
                        return 2;
                    case 2:
                        return 7;
                    case 3:
                        return 12;
                    case 4:
                        return 17;
                    case 5:
                        return 25;
                    case 6:
                        return 33;
                    case 7:
                        return 45;
                }
                return 0;
            case PVP_DEFEND_RATIO:
                switch (autorizeLvl) {
                    case 1:
                        return 3;
                    case 2:
                        return 9;
                    case 3:
                        return 15;
                    case 4:
                        return 21;
                    case 5:
                        return 31;
                    case 6:
                        return 41;
                    case 7:
                        return 55;
                }
                return 0;
            }
            return 0;
    }

	private int getArmorModifiers(int enchantLvl, Stat2 applyStat) {
		ArmorType armorType = item.getItemTemplate().getArmorType();
		if (armorType == null) {
			log.warn("Missing item ArmorType itemId: " + item.getItemId() + " EquipmentSlot: " + item.getEquipmentSlot() + " playerObjectId: "
				+ applyStat.getOwner().getObjectId());
			return 0;
		}

		int equipmentSlot = (int) (item.getEquipmentSlot() & 0xFFFFFFFF);
		switch (item.getItemTemplate().getArmorType()) {
			case ROBE:
				switch (equipmentSlot) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 2 * enchantLvl;
							case MAXHP:
								return 12 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 14 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case LEATHER:
				switch (equipmentSlot) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 2 * enchantLvl;
							case MAXHP:
								return 8 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 12 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case CHAIN:
				switch (equipmentSlot) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 6 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 8 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case PLATE:
				switch (equipmentSlot) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 4 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 6 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 6 * enchantLvl;
							case MAXHP:
								return 8 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
						}
						return 0;
				}
				return 0;
            case SHIELD:
                switch (stat) {
                    case DAMAGE_REDUCE:
                        float reduceRate = enchantLvl > 10 ? 0.2f : enchantLvl * 0.02f;
                        return Math.round(reduceRate * applyStat.getBase());
                    case BLOCK:
                        if (enchantLvl > 10) {
                            return 30 * (enchantLvl - 10);
                        }
                        return 0;
                }
            case PLUME:
                switch (this.stat) {
                    case MAXHP:
                        return 150 * enchantLvl;
                    case PHYSICAL_ATTACK:
                        return 4 * enchantLvl;
                    case BOOST_MAGICAL_SKILL:
                        return 20 * enchantLvl;
                }
                break;
        }
        return 0;
    }
}
