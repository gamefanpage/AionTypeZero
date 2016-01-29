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

import org.typezero.gameserver.configs.main.CraftConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.craft.*;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.templates.CraftLearnTemplate;
import org.typezero.gameserver.model.templates.recipe.RecipeTemplate;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author synchro2
 */
public class RelinquishCraftStatus {

	private static final int expertMinValue = 399;
	private static final int expertMaxValue = 499;
	private static final int masterMinValue = 499;
	private static final int masterMaxValue = 549;
	private static final int expertPrice = 120895;
	private static final int masterPrice = 3497448;
	private static final int systemMessageId = 1300388;
	private static final int skillMessageId = 1401127;

	public static final RelinquishCraftStatus getInstance() {
		return SingletonHolder.instance;
	}

	public static void relinquishExpertStatus(Player player, Npc npc) {
		CraftLearnTemplate craftLearnTemplate = CraftSkillUpdateService.npcBySkill.get(npc.getNpcId());
		final int skillId = craftLearnTemplate.getSkillId();
		PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if(!canRelinquishCraftStatus(player, skill, craftLearnTemplate, expertMinValue, expertMaxValue)) {
			return;
		}
		if (!successDecreaseKinah(player, expertPrice)) {
			return;
		}
		skill.setSkillLvl(expertMinValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		removeRecipesAbove(player, skillId, expertMinValue);
		deleteCraftStatusQuests(skillId, player, true);
	}

	public static void relinquishMasterStatus(Player player, Npc npc) {
		CraftLearnTemplate craftLearnTemplate = CraftSkillUpdateService.npcBySkill.get(npc.getNpcId());
		final int skillId = craftLearnTemplate.getSkillId();
		PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if(!canRelinquishCraftStatus(player, skill, craftLearnTemplate, masterMinValue, masterMaxValue)) {
			return;
		}
		if (!successDecreaseKinah(player, masterPrice)) {
			return;
		}
		skill.setSkillLvl(masterMinValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		removeRecipesAbove(player, skillId, masterMinValue);
		deleteCraftStatusQuests(skillId, player, false);
	}

	private static boolean canRelinquishCraftStatus(Player player, PlayerSkillEntry skill, CraftLearnTemplate craftLearnTemplate, int minValue, int maxValue) {
		if (craftLearnTemplate == null || !craftLearnTemplate.isCraftSkill()) {
			return false;
		}
		if (skill == null || skill.getSkillLevel() < minValue || skill.getSkillLevel() > maxValue) {
			return false;
		}
		return true;
	}

	private static boolean successDecreaseKinah(Player player, int basePrice) {
		if (!player.getInventory().tryDecreaseKinah(PricesService.getPriceForService(basePrice, player.getRace()))) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(systemMessageId));
			return false;
		}
		return true;
	}

	public static void removeRecipesAbove(Player player, int skillId, int level) {
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getRecipeTemplates().valueCollection()) {
			if (recipe.getSkillid() != skillId || recipe.getSkillpoint() < level) {
				continue;
			}
			player.getRecipeList().deleteRecipe(player, recipe.getId());
		}
	}

	public static void deleteCraftStatusQuests(int skillId, Player player, boolean isExpert) {
		for (int questId : MasterQuestsList.getSkillsIds(skillId, player.getRace())) {
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null) {
				qs.setQuestVar(0);
				qs.setCompleteCount(0);
				qs.setStatus(null);
				qs.setPersistentState(PersistentState.DELETED);
			}
		}
		if (isExpert) {
			for (int questId : ExpertQuestsList.getSkillsIds(skillId, player.getRace())) {
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs != null) {
					qs.setQuestVar(0);
					qs.setCompleteCount(0);
					qs.setStatus(null);
					qs.setPersistentState(PersistentState.DELETED);
				}
			}
		}
		PacketSendUtility.sendPacket(player, new SM_QUEST_COMPLETED_LIST(player.getQuestStateList().getAllFinishedQuests()));
		player.getController().updateNearbyQuests();
	}

	public static void removeExcessCraftStatus(Player player, boolean isExpert) {
		int minValue = isExpert ? expertMinValue : masterMinValue;
		int maxValue = isExpert ? expertMaxValue : masterMaxValue;
		int skillId;
		int skillLevel;
		int maxCraftStatus = isExpert ? CraftConfig.MAX_EXPERT_CRAFTING_SKILLS : CraftConfig.MAX_MASTER_CRAFTING_SKILLS;
		int countCraftStatus;
		for (PlayerSkillEntry skill : player.getSkillList().getBasicSkills()) {
			countCraftStatus = isExpert ? CraftSkillUpdateService.getTotalMasterCraftingSkills(player) +
					CraftSkillUpdateService.getTotalExpertCraftingSkills(player) :
					CraftSkillUpdateService.getTotalMasterCraftingSkills(player);
			if (countCraftStatus > maxCraftStatus) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				if (CraftSkillUpdateService.isCraftingSkill(skillId) && skillLevel > minValue && skillLevel <= maxValue) {
					skill.setSkillLvl(minValue);
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
					removeRecipesAbove(player, skillId, minValue);
					deleteCraftStatusQuests(skillId, player, isExpert);
				}
				continue;
			}
			break;
		}
		if (!isExpert) {
			removeExcessCraftStatus(player, true);
		}
	}

	public static int getExpertMinValue() {
		return expertMinValue;
	}

	public static int getExpertMaxValue() {
		return expertMaxValue;
	}

	public static int getMasterMinValue() {
		return masterMinValue;
	}

	public static int getMasterMaxValue() {
		return masterMaxValue;
	}

	public static int getSkillMessageId() {
		return skillMessageId;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final RelinquishCraftStatus instance = new RelinquishCraftStatus();
	}
}
