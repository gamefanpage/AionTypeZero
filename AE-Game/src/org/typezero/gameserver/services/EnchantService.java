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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.EnchantsConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.SkillTreeData;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.items.ManaStone;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatEnchantFunction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.stats.listeners.ItemEquipmentListener;
import org.typezero.gameserver.model.templates.item.*;
import org.typezero.gameserver.model.templates.item.actions.EnchantItemAction;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.item.ItemSocketService;
import org.typezero.gameserver.skillengine.model.SkillLearnTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 * @modified Wakizashi, Source, vlog
 */
public class EnchantService {

   private static final Logger log = LoggerFactory.getLogger(EnchantService.class);

   /**
    * @param player
    * @param targetItem
    * @param parentItem
    */
   public static boolean breakItem(Player player, Item targetItem, Item parentItem) {
	  Storage inventory = player.getInventory();

	  if (inventory.getItemByObjId(targetItem.getObjectId()) == null)
		 return false;
	  if (inventory.getItemByObjId(parentItem.getObjectId()) == null)
		 return false;

	  ItemTemplate itemTemplate = targetItem.getItemTemplate();
	  int quality = itemTemplate.getItemQuality().getQualityId();

	  if (!itemTemplate.isArmor() && !itemTemplate.isWeapon()) {
		 AuditLogger.info(player, "Player try break dont compatible item type.");
		 return false;
	  }

	  if (!itemTemplate.isArmor() && !itemTemplate.isWeapon()) {
		 AuditLogger.info(player, "Break item hack, armor/weapon iD changed.");
		 return false;
	  }

	  // Quality modifier
	  if (itemTemplate.isSoulBound() && !itemTemplate.isArmor())
		 quality += 1;
	  else if (!itemTemplate.isSoulBound() && itemTemplate.isArmor())
		 quality -= 1;

	  int number = 0;
	  int level = 1;
	  switch (quality) {
		 case 0: // JUNK
		 case 1: // COMMON
			number = Rnd.get(1, 2);
			level = Rnd.get(-4, 10);
			break;
		 case 2: // RARE
			number = Rnd.get(1, 4);
			level = Rnd.get(0, 20);
			break;
		 case 3: // LEGEND
			number = Rnd.get(1, 6);
			level = Rnd.get(0, 30);
			break;
		 case 4: // UNIQUE
			number = Rnd.get(1, 8);
			level = Rnd.get(0, 50);
			break;
            case 5: // EPIC
                number = Rnd.get(1, 10);
                level = Rnd.get(0, 70);
                break;
		 case 6: // MYTHIC
		 case 7:
			number = Rnd.get(1, 12);
			level = Rnd.get(0, 90);
			break;
	  }

	  // You can't add stone < 166000000
	  if (level < 1)
		 level = 1;
	  int enchantItemLevel = targetItem.getItemTemplate().getLevel() + level;
	  int enchantItemId = 166000000 + enchantItemLevel;

	  if (inventory.delete(targetItem) != null) {
		 if (inventory.decreaseByObjectId(parentItem.getObjectId(), 1))
			ItemService.addItem(player, enchantItemId, number);
	  }
	  else
		 AuditLogger.info(player, "Possible break item hack, do not remove item.");
	  return true;
   }

   /**
    * @param player
    * @param parentItem the enchantment stone
    * @param targetItem the item to enchant
    * @param supplementItem the item, giving additional chance
    * @return true, if successful
    */
    public static boolean enchantItem(Player player, Item parentItem, Item targetItem, Item supplementItem) {
        ItemTemplate enchantStone = parentItem.getItemTemplate();
        int enchantStoneLevel = enchantStone.getLevel();
        int targetItemLevel = targetItem.getItemTemplate().getLevel();
        int enchantitemLevel = targetItem.getEnchantLevel() + 1;

        // Modifier, depending on the quality of the item
        // Decreases the chance of enchant
        int qualityCap = 0;

        ItemQuality quality = targetItem.getItemTemplate().getItemQuality();

        switch (quality) {
            case JUNK:
            case COMMON:
                qualityCap = 0;
                break;
            case RARE:
                qualityCap = 5;
                break;
            case LEGEND:
                qualityCap = 10;
                break;
            case UNIQUE:
            case EPIC:
                qualityCap = 15;
                break;
            case MYTHIC:
                qualityCap = 20;
                break;
        }

        // Start value of success
        float success = EnchantsConfig.ENCHANT_STONE;

        // Extra success chance
        // The greater the enchant stone level, the greater the
        // level difference modifier
        int levelDiff = enchantStoneLevel - targetItemLevel;
        success += levelDiff > 0 ? levelDiff * 3f / qualityCap : 0;

        // Level difference
        // Can be negative, if the item quality too hight
        // And the level difference too small
        success += levelDiff - qualityCap;

        // Enchant next level difficulty
        // The greater item enchant level,
        // the lower start success chance
        success -= targetItem.getEnchantLevel() * qualityCap / (enchantitemLevel > 10 ? 4f : 5f);

        // Supplement is used
        if (supplementItem != null) {
            // Amount of supplement items
            int supplementUseCount = 1;
            // Additional success rate for the supplement
            ItemTemplate supplementTemplate = supplementItem.getItemTemplate();
            float addSuccessRate = 0f;

            EnchantItemAction action = supplementTemplate.getActions().getEnchantAction();
            if (action != null) {
                if (action.isManastoneOnly())
                    return false;
                addSuccessRate = action.getChance() * 2;
            }

            action = enchantStone.getActions().getEnchantAction();
            if (action != null)
                supplementUseCount = action.getCount();

            // Beginning from the level 11 of the enchantment of the item,
            // There will be 2 times more supplements required
            if (enchantitemLevel > 10)
                supplementUseCount = supplementUseCount * 2;

            // Check the required amount of the supplements
            if (player.getInventory().getItemCountByItemId(supplementTemplate.getTemplateId()) < supplementUseCount)
                return false;

            // Adjust addsuccessrate to rates in config
            switch (parentItem.getItemTemplate().getItemQuality()) {
                case LEGEND:
                    addSuccessRate *= EnchantsConfig.LESSER_SUP;
                    break;
                case UNIQUE:
                    addSuccessRate *= EnchantsConfig.REGULAR_SUP;
                    break;
                case EPIC:
                    addSuccessRate *= EnchantsConfig.GREATER_SUP;
                    break;
                case MYTHIC:
                    addSuccessRate *= EnchantsConfig.MYTHIC_SUP;
                    break;
                default:
                    break;
            }

            // Add success rate of the supplement to the overall chance
            success += addSuccessRate;

            // Put supplements to wait for update
            player.subtractSupplements(supplementUseCount, supplementTemplate.getTemplateId());
        }

        // The overall success chance can't be more, than 95
        if (success >= 95)
            success = 95;

        boolean result = false;
        float random = Rnd.get(1, 1000) / 10f;

        // If the random number < or = overall success rate,
        // The item will be successfully enchanted
        if (random <= success)
            result = true;

        // For test purpose. To use by administrator
        if (player.getAccessLevel() > 2)
            PacketSendUtility.sendMessage(player, (result ? "Success:" : "Failure:") + " Fail:" + random + " Luck:" + success);

        return result;
    }

    public static void enchantStigmaAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result) {
        if (result) {
            // возможно нужна проверка на максимальный уровень заточки
            currentEnchant++;
        } else {
            currentEnchant = 0;
        }

        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            AuditLogger.info(player, "Possible enchant hack, can't remove 2nd stigma.");
            return;
        }

        targetItem.setEnchantLevel(currentEnchant);

        if (targetItem.isEquipped())
            player.getGameStats().updateStatsVisually();

        ItemPacketService.updateItemAfterInfoChange(player, targetItem);

        if (targetItem.isEquipped())
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        else
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);

        if (result) {
            Stigma stigmaInfo = targetItem.getItemTemplate().getStigma();

            for (Stigma.StigmaSkill sSkill : stigmaInfo.getSkills()) {
                String sSkillStack = DataManager.SKILL_DATA.getSkillTemplate(sSkill.getSkillId()).getStack();

                for (PlayerSkillEntry psSkill : player.getSkillList().getStigmaSkills()){
                    if (psSkill.getSkillTemplate().getStack().equals(sSkillStack)) {
                        SkillLearnService.removeSkill(player, psSkill.getSkillId());
                        player.getEffectController().removeEffect(psSkill.getSkillId());
                    }
                }
            }

            // отжимаем скрытую стигму
            player.getSkillList().deleteHiddenStigmaSilent(player);

            //найти и выучить новый скилл в соотв с заточкой скилла
            Integer realSkillId = DataManager.SKILL_TREE_DATA.getStigmaTree().get(player.getRace()).
                    get(DataManager.SKILL_DATA.getSkillTemplate(stigmaInfo.getSkills().get(0).getSkillId()).getStack()).get(targetItem.getEnchantLevel() + 1);
            if (realSkillId != null)
                player.getSkillList().addStigmaSkill(player, realSkillId, 1);
            else {
                log.error("No have Stigma skill for enchanted stigma item.");
            }

            StigmaService.recheckHiddenStigma(player);

            PacketSendUtility.sendPacket(player,
                    SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEED_NEW(new DescriptionId(targetItem.getNameId()), 1));
        }
        else
            PacketSendUtility.sendPacket(player,
                    SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
    }

    public static void enchantItemAct(Player player, Item parentItem, Item targetItem, Item supplementItem, int currentEnchant, boolean result) {
        int addLevel = 1;
        int rnd = Rnd.get(100); //crit modifier
        if (rnd < 5)
            addLevel = 3;
        else if (rnd < 15)
            addLevel = 2;
        ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();

        if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
            AuditLogger.info(player, "Possible enchant hack, do not remove enchant stone.");
            return;
        }
        //Decrease required supplements
        player.updateSupplements();

        // Items that are Fabled or Eternal can get up to +15.
        if (result) {
            switch (targetQuality) {
                case COMMON:
                case RARE:
                case LEGEND:
                    if (currentEnchant > targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        currentEnchant = targetItem.getItemTemplate().getMaxEnchantLevel();
                        AuditLogger.info(player, "Possible enchant hack, send fake packet for enchant up more posible.");
                    }
                    else if (currentEnchant == targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        return;
                    }
                    else if (currentEnchant + addLevel <= targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        currentEnchant += addLevel;
                    }
                    else if (((addLevel - 1) > 1) && ((currentEnchant + addLevel - 1) <= targetItem.getItemTemplate().getMaxEnchantLevel())) {
                        currentEnchant += (addLevel - 1);
                    }
                    else
                        currentEnchant += 1;
                    break;
                case UNIQUE:
                case EPIC:
                case MYTHIC:
                    if (currentEnchant > targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        currentEnchant = targetItem.getItemTemplate().getMaxEnchantLevel();
                        AuditLogger.info(player, "Possible enchant hack, send fake packet for enchant up more posible.");
                    }
                    else if (currentEnchant == targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        return;
                    }
                    else if (currentEnchant + addLevel <= targetItem.getItemTemplate().getMaxEnchantLevel()) {
                        currentEnchant += addLevel;
                    }
                    else if (((addLevel - 1) > 1) && ((currentEnchant + addLevel - 1) <= targetItem.getItemTemplate().getMaxEnchantLevel())) {
                        currentEnchant += (addLevel - 1);
                    }
                    else
                        currentEnchant += 1;
                    break;
                case JUNK:
                    return;
            }
        }
        else {
            // Retail: http://powerwiki.na.aiononline.com/aion/Patch+Notes:+1.9.0.1
            // When socketing fails at +11~+15, the value falls back to +10.
            if (currentEnchant > 10)
                currentEnchant = 10;
            else if (currentEnchant > 0)
                currentEnchant -= 1;
        }

        targetItem.setEnchantLevel(currentEnchant);
        if (targetItem.isEquipped())
            player.getGameStats().updateStatsVisually();

        ItemPacketService.updateItemAfterInfoChange(player, targetItem);

        if (targetItem.isEquipped())
            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        else
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);

        if (result)
            PacketSendUtility.sendPacket(player,
                    SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEED_NEW(new DescriptionId(targetItem.getNameId()), addLevel));
        else
            PacketSendUtility.sendPacket(player,
                    SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
    }

   /**
    * @param player
    * @param parentItem the manastone
    * @param targetItem the item to socket
    * @param supplementItem
    * @param targetWeapon fusioned weapon
    */
   public static boolean socketManastone(Player player, Item parentItem, Item targetItem, Item supplementItem,
		   int targetWeapon) {

	  int targetItemLevel = 1;

	  // Fusioned weapon. Primary weapon level.
	  if (targetWeapon == 1)
		 targetItemLevel = targetItem.getItemTemplate().getLevel();
	  // Fusioned weapon. Secondary weapon level.
	  else
		 targetItemLevel = targetItem.getFusionedItemTemplate().getLevel();

	  int stoneLevel = parentItem.getItemTemplate().getLevel();
	  int slotLevel = (int) (10 * Math.ceil((targetItemLevel + 10) / 10d));
	  boolean result = false;

	  // Start value of success
	  float success = EnchantsConfig.MANA_STONE;

	  // The current amount of socketed stones
	  int stoneCount;

	  // Manastone level shouldn't be greater as 20 + item level
	  // Example: item level: 1 - 10. Manastone level should be <= 20
	  if (stoneLevel > slotLevel)
		 return false;

	  // Fusioned weapon. Primary weapon slots.
	  if (targetWeapon == 1)
		 // Count the inserted stones in the primary weapon
		 stoneCount = targetItem.getItemStones().size();
	  // Fusioned weapon. Secondary weapon slots.
	  else
		 // Count the inserted stones in the secondary weapon
		 stoneCount = targetItem.getFusionStones().size();

	  // Fusioned weapon. Primary weapon slots.
	  if (targetWeapon == 1) {
		 // Find all free slots in the primary weapon
		 if (stoneCount >= targetItem.getSockets(false)) {
			AuditLogger.info(player, "Manastone socket overload");
			return false;
		 }
	  }
	  // Fusioned weapon. Secondary weapon slots.
	  else if (!targetItem.hasFusionedItem() || stoneCount >= targetItem.getSockets(true)) {
		 // Find all free slots in the secondary weapon
		 AuditLogger.info(player, "Manastone socket overload");
		 return false;
	  }

	  // Stone quality modifier
	  success += parentItem.getItemTemplate().getItemQuality() == ItemQuality.COMMON ? 25f : 15f;

	  // Next socket difficulty modifier
	  float socketDiff = stoneCount * 1.25f + 1.75f;

	  // Level difference
	  success += (slotLevel - stoneLevel) / socketDiff;

	  // The supplement item is used
	  if (supplementItem != null) {
		 int supplementUseCount = 0;
		 ItemTemplate manastoneTemplate = parentItem.getItemTemplate();

		 int manastoneCount;
		 // Not fusioned
		 if (targetWeapon == 1)
			manastoneCount = targetItem.getItemStones().size() + 1;
		 // Fusioned
		 else
			manastoneCount = targetItem.getFusionStones().size() + 1;

		 // Additional success rate for the supplement
		 ItemTemplate supplementTemplate = supplementItem.getItemTemplate();
		 float addSuccessRate = 0f;

		 boolean isManastoneOnly = false;
		 EnchantItemAction action = manastoneTemplate.getActions().getEnchantAction();
		 if (action != null)
			supplementUseCount = action.getCount();

		 action = supplementTemplate.getActions().getEnchantAction();
		 if (action != null) {
			addSuccessRate = action.getChance();
			isManastoneOnly = action.isManastoneOnly();
		 }

		 // Adjust addsuccessrate to rates in config
		 switch (parentItem.getItemTemplate().getItemQuality()) {
			case LEGEND:
			   addSuccessRate *= EnchantsConfig.LESSER_SUP;
			   break;
			case UNIQUE:
			   addSuccessRate *= EnchantsConfig.REGULAR_SUP;
			   break;
			case EPIC:
			   addSuccessRate *= EnchantsConfig.GREATER_SUP;
			   break;
			case MYTHIC:
			   addSuccessRate *= EnchantsConfig.MYTHIC_SUP;
			   break;
		default:
			break;
		 }

		 if (isManastoneOnly)
			supplementUseCount = 1;
		 else if (stoneCount > 0)
			supplementUseCount = supplementUseCount * manastoneCount;

		 if (player.getInventory().getItemCountByItemId(supplementTemplate.getTemplateId()) < supplementUseCount)
			return false;

		 // Add successRate
		 success += addSuccessRate;

		 // Put up supplements to wait for update
		 player.subtractSupplements(supplementUseCount, supplementTemplate.getTemplateId());
	  }

	  float random = Rnd.get(1, 1000) / 10f;

	  if (random <= success)
		 result = true;

	  // For test purpose. To use by administrator
	  if (player.getAccessLevel() > 2)
		 PacketSendUtility.sendMessage(player, (result ? "Success:" : "Failure:") + " Fail:" + random + " Luck:" + success);

	  return result;
   }

   public static void socketManastoneAct(Player player, Item parentItem, Item targetItem, Item supplementItem,
		   int targetWeapon, boolean result) {

	  // Decrease required supplements
	  player.updateSupplements();

	  if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1) && result) {
		 PacketSendUtility.sendPacket(player,
				 SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_SUCCEED(new DescriptionId(targetItem.getNameId())));

		 if (targetWeapon == 1) {
			ManaStone manaStone = ItemSocketService.addManaStone(targetItem, parentItem.getItemTemplate().getTemplateId());
			if (targetItem.isEquipped()) {
			   ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
			   player.getGameStats().updateStatsAndSpeedVisually();
			}
		 }
		 else {
			ManaStone manaStone = ItemSocketService.addFusionStone(targetItem, parentItem.getItemTemplate().getTemplateId());
			if (targetItem.isEquipped()) {
			   ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
			   player.getGameStats().updateStatsAndSpeedVisually();
			}
		 }
	  }
	  else {
		 PacketSendUtility.sendPacket(player,
				 SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_FAILED(new DescriptionId(targetItem.getNameId())));
		 if (targetWeapon == 1) {
			Set<ManaStone> manaStones = targetItem.getItemStones();
			if (targetItem.isEquipped()) {
			   ItemEquipmentListener.removeStoneStats(manaStones, player.getGameStats());
			   player.getGameStats().updateStatsAndSpeedVisually();
			}
			ItemSocketService.removeAllManastone(player, targetItem);
		 }
		 else {
			Set<ManaStone> manaStones = targetItem.getFusionStones();

			if (targetItem.isEquipped()) {
			   ItemEquipmentListener.removeStoneStats(manaStones, player.getGameStats());
			   player.getGameStats().updateStatsAndSpeedVisually();
			}

			ItemSocketService.removeAllFusionStone(player, targetItem);
		 }
	  }

	  ItemPacketService.updateItemAfterInfoChange(player, targetItem);
   }

   /**
    * @param player
    * @param item
    */
   public static void onItemEquip(Player player, Item item) {
	  List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
	  try {
		 if (item.getItemTemplate().isWeapon()) {
			switch (item.getItemTemplate().getWeaponType()) {
			   case BOOK_2H:
			   case ORB_2H:
			   case GUN_1H:
			   case CANNON_2H:
			   case HARP_2H:
			   case KEYBLADE_2H:
				  modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
				  modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_ATTACK));
				  break;
			   case MACE_1H:
			   case STAFF_2H:
				  modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
			   case DAGGER_1H:
			   case BOW:
			   case POLEARM_2H:
			   case SWORD_1H:
			   case SWORD_2H:
				  if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask())
					 modifiers.add(new StatEnchantFunction(item, StatEnum.MAIN_HAND_POWER));
				  else
					 modifiers.add(new StatEnchantFunction(item, StatEnum.OFF_HAND_POWER));
			}
		 }
		 else if (item.getItemTemplate().isArmor()) {
			if (item.getItemTemplate().getArmorType() == ArmorType.SHIELD) {
			   modifiers.add(new StatEnchantFunction(item, StatEnum.DAMAGE_REDUCE));
			   modifiers.add(new StatEnchantFunction(item, StatEnum.BLOCK));
                }
				if (item.getItemTemplate().isAccessory() && item.getItemTemplate().getCategory() != ItemCategory.PLUME) {
				    switch (item.getItemTemplate().getCategory()) {
                        case HELMET:
                        case EARRINGS:
                        case NECKLACE:
							modifiers.add(new StatEnchantFunction(item, StatEnum.PVP_ATTACK_RATIO));
                            break;
                        case RINGS:
                        case BELT:
							modifiers.add(new StatEnchantFunction(item, StatEnum.PVP_DEFEND_RATIO));
                    }
				}
                if (item.getItemTemplate().getCategory() == ItemCategory.PLUME) {
                    int id = item.getItemTemplate().getAuthorizeName();
                    switch (id) {
                        case 52:
                            modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_ATTACK));
                            modifiers.add(new StatEnchantFunction(item, StatEnum.MAXHP));
                            break;
                        case 53:
                            modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
                            modifiers.add(new StatEnchantFunction(item, StatEnum.MAXHP));
                    }

                } else {
                    modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_DEFENSE));
                    modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_DEFEND));
                    modifiers.add(new StatEnchantFunction(item, StatEnum.MAXHP));
                    modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_CRITICAL_RESIST));
                }
		 }



		 if (!modifiers.isEmpty())
			player.getGameStats().addEffect(item, modifiers);
	  }
	  catch (Exception ex) {
		 log.error("Error on item equip.", ex);
	  }
   }

    public static int getWeaponBuff(Player player) {
    	int skillId = 0;
    	skillId = Rnd.get(13001, 13037);
    	if (player.getSkillList().getSkillEntry(skillId) != null) {
    		skillId = Rnd.get(13001, 13037);
    	}
    	return skillId;
    }

    public static int getArmorBuff(Player player) {
    	int skillId = 0;
    	skillId = Rnd.get(13038, 13227);
    	if (player.getSkillList().getSkillEntry(skillId) != null) {
    		skillId = Rnd.get(13038, 13227);
    	}
    	return skillId;
    }

    public static void amplifyItemCommand(Player player, Item item) {
    	int buffId = 0;
    	if (item == null || player == null)
    		return;

    	//if (!item.getItemTemplate().getExceedEnchant())
    		//return;

    	if (item.getEnchantLevel() < 15)
    		return;

    	if (item.getItemTemplate().isArmor()) {
    		buffId = getArmorBuff(player);
    	} else if (item.getItemTemplate().isWeapon()) {
    		buffId = getWeaponBuff(player);
    	}

    	//item.setAmplified(true);
    	//item.setBuffSkill(buffId);
    	ItemPacketService.updateItemAfterInfoChange(player, item);
    }

    public static void amplifyItem(Player player, Item targetItem, Item material, Item tool) {
    	int buffId = 0;
    	if (targetItem == null || player == null)
    		return;

    	//if (!targetItem.getItemTemplate().getExceedEnchant())
    		//return;

    	if (targetItem.getEnchantLevel() < 15 && targetItem.getItemTemplate().getMaxEnchantLevel() == 15)
    		return;

    	if (targetItem.getItemId() != material.getItemId() && material.getItemId() != 166500002) {
    		log.warn("[AMPLIFICATION] player " + player.getName() + " tried to amplificate with material " + material.getItemId());
    		return;
    	}

    	if (targetItem.getItemTemplate().isArmor()) {
    		buffId = getArmorBuff(player);
    	} else if (targetItem.getItemTemplate().isWeapon()) {
    		buffId = getWeaponBuff(player);
    	}

    	//targetItem.setAmplified(true);
    	//targetItem.setBuffSkill(buffId);
    	player.getInventory().decreaseByObjectId(material.getObjectId(), 1);
    	player.getInventory().decreaseByObjectId(tool.getObjectId(), 1);
    	//PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_EXCEED_SUCCEED(new DescriptionId(targetItem.getNameId())));
    	ItemPacketService.updateItemAfterInfoChange(player, targetItem);
    }
}
