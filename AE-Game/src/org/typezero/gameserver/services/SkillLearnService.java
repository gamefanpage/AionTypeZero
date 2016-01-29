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

import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.skill.PlayerSkillList;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_REMOVE;
import org.typezero.gameserver.skillengine.model.SkillLearnTemplate;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, xTz
 */
public class SkillLearnService {

	/**
	 * @param player
	 */
	public static void addNewSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();

		if (level == 10 && player.getSkillList().getSkillEntry(30001) != null) {
			int skillLevel = player.getSkillList().getSkillLevel(30001);
			removeSkill(player, 30001);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			// Why adding after the packet ?
			player.getSkillList().addSkill(player, 30002, skillLevel);
		}
		addSkills(player, level, playerClass, playerRace);
	}

	/**
	 * Recursively check missing skills and add them to player
	 *
	 * @param player
	 */
	public static void addMissingSkills(Player player) {
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getRace();

		for (int i = 0; i <= level; i++) {
			addSkills(player, i, playerClass, playerRace);
		}

		if (!playerClass.isStartingClass()) {
			PlayerClass startinClass = PlayerClass.getStartingClassFor(playerClass);

			for (int i = 1; i < 10; i++) {
				addSkills(player, i, startinClass, playerRace);
			}

			if (player.getSkillList().getSkillEntry(30001) != null) {
				int skillLevel = player.getSkillList().getSkillLevel(30001);
				player.getSkillList().removeSkill(30001);
				// Not sure about that, mysterious code
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				for (PlayerSkillEntry stigmaSkill : player.getSkillList().getStigmaSkills())
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, stigmaSkill));
				// Why adding after the packet ?
				player.getSkillList().addSkill(player, 30002, skillLevel);
			}
		}
	}

	/**
	 * Adds skill to player according to the specified level, class and race
	 *
	 * @param player
	 * @param level
	 * @param playerClass
	 * @param playerRace
	 */
	private static void addSkills(Player player, int level, PlayerClass playerClass, Race playerRace) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(playerClass, level, playerRace);
		PlayerSkillList playerSkillList = player.getSkillList();

		for (SkillLearnTemplate template : skillTemplates) {
			if (!checkLearnIsPossible(player, playerSkillList, template))
				continue;

			if (template.isStigma())
				playerSkillList.addStigmaSkill(player, template.getSkillId(), template.getSkillLevel());
			else
				playerSkillList.addSkill(player, template.getSkillId(), template.getSkillLevel());
		}
	}

	/**
	 * Check SKILL_AUTOLEARN property Check skill already learned Check skill template auto-learn attribute
	 *
	 * @param playerSkillList
	 * @param template
	 * @return
	 */
	private static boolean checkLearnIsPossible(Player player, PlayerSkillList playerSkillList,
		SkillLearnTemplate template) {
		if (playerSkillList.isSkillPresent(template.getSkillId()))
			return true;

		if ((player.havePermission(MembershipConfig.SKILL_AUTOLEARN) && !template.isStigma())
			|| (player.havePermission(MembershipConfig.STIGMA_AUTOLEARN) && template.isStigma()))
			return true;

		if (template.isAutolearn())
			return true;

		return false;
	}

	public static void learnSkillBook(Player player, int skillId) {
		SkillLearnTemplate[] skillTemplates = null;
		int maxLevel = 0;
		SkillTemplate passiveSkill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		for (int i = 1; i <= player.getLevel(); i++) {
			skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());

			for (SkillLearnTemplate skill : skillTemplates)
				if (skillId == skill.getSkillId()) {
					if (skill.getSkillLevel() > maxLevel)
						maxLevel = skill.getSkillLevel();
				}
		}
		player.getSkillList().addSkill(player, skillId, maxLevel);
		if(passiveSkill.isPassive())
			player.getController().updatePassiveStats();
	}

	public static void removeSkill(Player player, int skillId) {
		if (player.getSkillList().isSkillPresent(skillId)) {
			Integer skillLevel = player.getSkillList().getSkillLevel(skillId);
			if(skillLevel == null)
				skillLevel = 1;
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillLevel,
				player.getSkillList().getSkillEntry(skillId).isStigma()));
			player.getSkillList().removeSkill(skillId);
		}
	}

	public static int getSkillLearnLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		int learnFinishes = 0;
		int maxLevel = 0;

		for (SkillLearnTemplate template : skillTemplates) {
			if (maxLevel < template.getSkillLevel())
				maxLevel = template.getSkillLevel();
		}

		// no data in skill tree, use as wanted
		if (maxLevel == 0)
			return wantedSkillLevel;

		learnFinishes = playerLevel + maxLevel;

		if (learnFinishes > DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
			learnFinishes = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();

		return Math.max(wantedSkillLevel, Math.min(playerLevel - (learnFinishes - maxLevel) + 1, maxLevel));
	}

	public static int getSkillMinLevel(int skillId, int playerLevel, int wantedSkillLevel) {
		SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		SkillLearnTemplate foundTemplate = null;

		for (SkillLearnTemplate template : skillTemplates) {
			if (template.getSkillLevel() <= wantedSkillLevel && template.getMinLevel() <= playerLevel)
				foundTemplate = template;
		}

		if (foundTemplate == null)
			return playerLevel;

		return foundTemplate.getMinLevel();
	}

}
