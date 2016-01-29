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

package org.typezero.gameserver.model.stats.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.IdianStone;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.model.items.RandomStats;
import org.typezero.gameserver.model.stats.calc.functions.StatAddFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatFunction;
import org.typezero.gameserver.model.stats.container.CreatureGameStats;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.WeaponStats;
import org.typezero.gameserver.model.templates.item.WeaponType;
import org.typezero.gameserver.model.templates.itemset.FullBonus;
import org.typezero.gameserver.model.templates.itemset.ItemSetTemplate;
import org.typezero.gameserver.model.templates.itemset.PartBonus;
import org.typezero.gameserver.services.EnchantService;
import org.typezero.gameserver.services.StigmaService;

/**
 * @author xavier modified by Wakizashi
 */
public class ItemEquipmentListener {

	/**
	 * @param item
	 * @param cgs
	 */
	public static void onItemEquipment(Item item, Player owner) {
		owner.getController().cancelUseItem();
		ItemTemplate itemTemplate = item.getItemTemplate();

		onItemEquipment(item, owner.getGameStats());

		// Check if belongs to ItemSet
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}
		if (item.hasManaStones())
			addStonesStats(item, item.getItemStones(), owner.getGameStats());

		if (item.hasFusionStones())
			addStonesStats(item, item.getFusionStones(), owner.getGameStats());
		IdianStone idianStone = item.getIdianStone();

        if (idianStone != null && (owner.getEquipment().getOffHandWeapon() != item || item.getItemTemplate().isTwoHandWeapon()) ) {
			idianStone.onEquip(owner);
		}
		addGodstoneEffect(owner, item);
		RandomStats randomStats = item.getRandomStats();
		if (randomStats != null) {
			randomStats.onEquip(owner);
		}
		if (item.getConditioningInfo() != null) {
			owner.getObserveController().addObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(owner);
		}
        if (item.getItemTemplate().isStigma())
            StigmaService.recheckHiddenStigma(owner);
		EnchantService.onItemEquip(owner, item);
	}

	/**
	 * @param item
	 * @param owner
	 */
	public static void onItemUnequipment(Item item, Player owner) {
		owner.getController().cancelUseItem();

		ItemTemplate itemTemplate = item.getItemTemplate();
		// Check if belongs to ItemSet
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}

		owner.getGameStats().endEffect(item);

		if (item.hasManaStones())
			removeStoneStats(item.getItemStones(), owner.getGameStats());

		if (item.hasFusionStones())
			removeStoneStats(item.getFusionStones(), owner.getGameStats());

		if (item.getConditioningInfo() != null) {
			owner.getObserveController().removeObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(null);
		}
		IdianStone idianStone = item.getIdianStone();
		if (idianStone != null) {
			idianStone.onUnEquip(owner);
		}
		removeGodstoneEffect(owner, item);
		RandomStats randomStats = item.getRandomStats();

		if (randomStats != null) {
			randomStats.onUnEquip(owner);
		}

	}

	/**
	 * @param itemTemplate
	 * @param slot
	 * @param cgs
	 */
	private static void onItemEquipment(Item item, CreatureGameStats<?> cgs) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		long slot = item.getEquipmentSlot();
		List<StatFunction> modifiers = itemTemplate.getModifiers();
		if (modifiers == null) {
			return;
		}

		List<StatFunction> allModifiers = null;

		if ((slot & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0) {
			allModifiers = wrapModifiers(item, modifiers);
			if (item.hasFusionedItem()) {
				// add all bonus modifiers according to rules
				ItemTemplate fusionedItemTemplate = item.getFusionedItemTemplate();
				WeaponType weaponType = fusionedItemTemplate.getWeaponType();
				List<StatFunction> fusionedItemModifiers = fusionedItemTemplate.getModifiers();
				if (fusionedItemModifiers != null) {
					allModifiers.addAll(wrapModifiers(item, fusionedItemModifiers));
				}
				// add 10% of Magic Boost and Attack
				WeaponStats weaponStats = fusionedItemTemplate.getWeaponStats();
				if (weaponStats != null) {
					int boostMagicalSkill = Math.round(0.1f * weaponStats.getBoostMagicalSkill());
					int attack = Math.round(0.1f * weaponStats.getMeanDamage());
					if (weaponType == WeaponType.ORB_2H || weaponType == WeaponType.BOOK_2H || weaponType == WeaponType.GUN_1H || weaponType == WeaponType.KEYBLADE_2H || weaponType == WeaponType.CANNON_2H || weaponType == WeaponType.HARP_2H) {
						allModifiers.add(new StatAddFunction(StatEnum.MAGICAL_ATTACK, attack, false));
						allModifiers.add(new StatAddFunction(StatEnum.BOOST_MAGICAL_SKILL, boostMagicalSkill, false));
					}
					else
						allModifiers.add(new StatAddFunction(StatEnum.PHYSICAL_ATTACK, attack, false));
				}
			}
		}
		else {
            WeaponStats weaponStat = itemTemplate.getWeaponStats();
            if  (weaponStat != null)
            {
                allModifiers = wrapModifiersW(item, modifiers);
            }
        else
            allModifiers = modifiers;
        }

		item.setCurrentModifiers(allModifiers);
		cgs.addEffect(item, allModifiers);
	}

	/**
	 * Filter stats based on the following rules:<br>
	 * 1) don't include fusioned stats which will be taken only from 1 weapon <br>
	 * 2) wrap stats which are different for MAIN and OFF hands<br>
	 * 3) add the rest<br>
	 *
	 * @param item
	 * @param modifiers
	 * @return
	 */
	private static List<StatFunction> wrapModifiers(Item item, List<StatFunction> modifiers) {
		List<StatFunction> allModifiers = new ArrayList<StatFunction>();
		for (StatFunction modifier : modifiers) {
			switch (modifier.getName()) {
				// why they are removed look at DuplicateStatFunction
				case ATTACK_SPEED:
				case PVP_ATTACK_RATIO:
				case BOOST_CASTING_TIME:
					continue;
				default:
					allModifiers.add(modifier);
			}
		}
		return allModifiers;
	}

    private static List<StatFunction> wrapModifiersW(Item item, List<StatFunction> modifiers) {
        List<StatFunction> allModifiers = new ArrayList<StatFunction>();
        ItemTemplate itemTemplate = item.getItemTemplate();
        WeaponStats weaponStats = itemTemplate.getWeaponStats();
        for (StatFunction modifier : modifiers) {
            switch (modifier.getName()) {
                default:
                    allModifiers.add(modifier);
            }
        }
        allModifiers.add(new StatAddFunction(StatEnum.PARRY, weaponStats.getParry(), false));
        allModifiers.add(new StatAddFunction(StatEnum.MAGICAL_ACCURACY, weaponStats.getMagicalAccuracy(), false));
        allModifiers.add(new StatAddFunction(StatEnum.PHYSICAL_ACCURACY, weaponStats.getPhysicalAccuracy(), false));

        return allModifiers;
    }

	/**
	 * @param itemSetTemplate
	 * @param player
	 * @param isWeapon
	 */
	private static void recalculateItemSet(ItemSetTemplate itemSetTemplate, Player player, boolean isWeapon) {
		if (itemSetTemplate == null)
			return;

		// TODO quite
		player.getGameStats().endEffect(itemSetTemplate);
		// 1.- Check equipment for items already equip with this itemSetTemplate id
		int itemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId());

		// If main hand and off hand is same , no bonus
		int mainHandItemId = 0;
		int offHandItemId = 0;
		if (player.getEquipment().getMainHandWeapon() != null)
			mainHandItemId = player.getEquipment().getMainHandWeapon().getItemId();
		if (player.getEquipment().getOffHandWeapon() != null)
			offHandItemId = player.getEquipment().getOffHandWeapon().getItemId();
		boolean mainAndOffNotSame = mainHandItemId != offHandItemId;

		// 2.- Check Item Set Parts and add effects one by one if not done already
		for (PartBonus itempartbonus : itemSetTemplate.getPartbonus()) {
			if (mainAndOffNotSame && isWeapon) {
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
					player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
				}
			}
			else if (!isWeapon) {
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
					player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
				}
			}
		}

		// 3.- Finally check if all items are applied and set the full bonus if not already applied
		FullBonus fullbonus = itemSetTemplate.getFullbonus();
		if (fullbonus != null && itemSetPartsEquipped == fullbonus.getCount()) {
			// Add the full bonus with index = total parts + 1 to avoid confusion with part bonus equal to number of
			// objects
			player.getGameStats().addEffect(itemSetTemplate, fullbonus.getModifiers());
		}
	}

	/**
	 * All modifiers of stones will be applied to character
	 *
	 * @param item
	 * @param cgs
	 */
	private static void addStonesStats(Item item, Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			addStoneStats(item, stone, cgs);
		}
	}

	/**
	 * Used when socketing of equipped item
	 *
	 * @param item
	 * @param stone
	 * @param cgs
	 */
	public static void addStoneStats(Item item, ManaStone stone, CreatureGameStats<?> cgs) {
		List<StatFunction> modifiers = stone.getModifiers();
		if (modifiers == null) {
			return;
		}

		cgs.addEffect(stone, modifiers);
	}

	/**
	 * All modifiers of stones will be removed
	 *
	 * @param itemStones
	 * @param cgs
	 */
	public static void removeStoneStats(Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			List<StatFunction> modifiers = stone.getModifiers();
			if (modifiers != null) {
				cgs.endEffect(stone);
			}
		}
	}

	/**
	 * @param item
	 */
	private static void addGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onEquip(player);
		}
	}

	/**
	 * @param item
	 */
	private static void removeGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onUnEquip(player);
		}
	}

}
