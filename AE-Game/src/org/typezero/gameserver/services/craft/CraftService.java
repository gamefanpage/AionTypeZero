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

package org.typezero.gameserver.services.craft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.StaticObject;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RewardType;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.recipe.Component;
import org.typezero.gameserver.model.templates.recipe.RecipeTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.item.ItemService.ItemUpdatePredicate;
import org.typezero.gameserver.skillengine.task.CraftingTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author MrPoke, sphinx, synchro2
 */
public class CraftService {

	private static final Logger log = LoggerFactory.getLogger("CRAFT_LOG");

	/**
	 * @param player
	 * @param recipetemplate
	 * @param critCount
	 */
	public static void finishCrafting(final Player player, RecipeTemplate recipetemplate, int critCount, int bonus) {

		if (recipetemplate.getMaxProductionCount() != null) {
			player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
			if (critCount == 0) {
				QuestEngine.getInstance().onFailCraft(new QuestEnv(null, player, 0, 0), recipetemplate.getComboProduct(1) == null? 0 : recipetemplate.getComboProduct(1));
			}
		}

		int xpReward = (int) ((0.008 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60));
		xpReward = xpReward + (xpReward * bonus / 100); // bonus
		int productItemId = critCount > 0 ? recipetemplate.getComboProduct(critCount) : recipetemplate.getProductid();

		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate() {

			@Override
            public boolean changeItem(Item item) {
                if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
                    item.setItemCreator(player.getName());
                }
                return true;
            }
		});

		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
		if (LoggingConfig.LOG_CRAFT) {
			log.info((critCount > 0 ? "[CRAFT][Critical] ID/Count" : "[CRAFT][Normal] ID/Count") + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + productItemId + "/" + recipetemplate.getQuantity() + "/" + itemTemplate.getName() : " - " + productItemId + "/" + recipetemplate.getQuantity()) +
			" to player " + player.getName());
		}

		int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);

		if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
			player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
		}
		else {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
		}

		if (recipetemplate.getCraftDelayId() != null) {
			player.getCraftCooldownList().addCraftCooldown(recipetemplate.getCraftDelayId(),
				recipetemplate.getCraftDelayTime());
		}
	}

	/**
	 * @param player
	 * @param targetTemplateId
	 * @param recipeId
	 * @param targetObjId
	 */
	public static void startCrafting(Player player, int recipeId, int targetObjId, int craftType) {

		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		int skillId = recipeTemplate.getSkillid();
		VisibleObject target = player.getKnownList().getObject(targetObjId);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());

		if (!checkCraft(player, recipeTemplate, skillId, target, itemTemplate, craftType)) {
			sendCancelCraft(player, skillId, targetObjId, itemTemplate);
			return;
		}

		if (recipeTemplate.getDp() != null)
			player.getCommonData().addDp(-recipeTemplate.getDp());

		int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
		player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, skillLvlDiff, craftType == 1 ? 15 : 0));

		if(skillId == 40009)
			player.getCraftingTask().setInterval(200);

		player.getCraftingTask().start();
	}

	private static boolean checkCraft(Player player, RecipeTemplate recipeTemplate, int skillId, VisibleObject target,
		ItemTemplate itemTemplate, int craftType) {

		if (recipeTemplate == null) {
			return false;
		}

		if (itemTemplate == null) {
			return false;
		}

		if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
			return false;
		}

		// morphing dont need static object/npc to use
		if ((skillId != 40009) && (target == null || !(target instanceof StaticObject))) {
			AuditLogger.info(player, " tried to craft incorrect target.");
			return false;
		}

		if (recipeTemplate.getDp() != null && (player.getCommonData().getDp() < recipeTemplate.getDp())) {
			AuditLogger.info(player, " try craft without required DP count.");
			return false;
		}

        if (player.getInventory().isFull()) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMBINE_INVENTORY_IS_FULL);
            return false;
        }

        if (!player.getRecipeList().isRecipePresent(recipeTemplate.getId())) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMBINE_CAN_NOT_FIND_RECIPE);
            return false;
        }
		if (recipeTemplate.getCraftDelayId() != null) {
			if (!player.getCraftCooldownList().isCanCraft(recipeTemplate.getCraftDelayId())) {
				AuditLogger.info(player, " try craft item before cooldown expire.");
				return false;
			}
		}

        for (Component component : recipeTemplate.getComponent()) {
            if (!player.getInventory().decreaseByItemId(component.getItemid(), component.getQuantity())) {
                AuditLogger.info(player, " tried craft without required items.");
                return false;
            }
        }

		if (craftType == 1 && !player.getInventory().decreaseByItemId(getBonusReqItem(skillId), 1)) {
			AuditLogger.info(player, " tried craft without 169401079.");
			return false;
		}

		if (!player.getSkillList().isSkillPresent(skillId)
			|| player.getSkillList().getSkillLevel(skillId) < recipeTemplate.getSkillpoint()) {
			AuditLogger.info(player, " tried craft without required skill.");
			return false;
		}

		return true;
	}

	private static void sendCancelCraft(Player player, int skillId, int targetObjId, ItemTemplate itemTemplate) {

		PacketSendUtility.sendPacket(player, new SM_CRAFT_UPDATE(skillId, itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(player, new SM_CRAFT_ANIMATION(player.getObjectId(), targetObjId, 0, 2), true);
	}

	private static int getBonusReqItem(int skillId) {
		switch (skillId) {
			case 40001: // Cooking
				return 169401081;
			case 40002: // Weaponsmithing
				return 169401076;
			case 40003: // Armorsmithing
				return 169401077;
			case 40004: // Tailoring
				return 169401078;
			case 40007: // Alchemy
				return 169401080;
			case 40008: // Handicrafting
				return 169401079;
			case 40010: // Menusier
				return 169401082;
		}
		return 0;
	}

}
