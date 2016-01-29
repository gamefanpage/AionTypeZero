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

package org.typezero.gameserver.services.reward;

import com.aionemu.commons.utils.Rnd;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.ItemGroupsData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.itemgroups.*;
import org.typezero.gameserver.model.templates.quest.QuestBonuses;
import org.typezero.gameserver.model.templates.quest.QuestItems;
import org.typezero.gameserver.model.templates.rewards.BonusType;
import org.typezero.gameserver.model.templates.rewards.CraftItem;
import org.typezero.gameserver.model.templates.rewards.MedalItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Rolandas
 */
public class BonusService {

	private static BonusService instance = new BonusService();
	private ItemGroupsData itemGroups = DataManager.ITEM_GROUPS_DATA;
	private static final Logger log = LoggerFactory.getLogger(BonusService.class);

	private BonusService() {
	}

	public static BonusService getInstance() {
		return instance;
	}

	public static BonusService getInstance(ItemGroupsData itemGroups) {
		instance.itemGroups = itemGroups;
		return instance;
	}

	public BonusItemGroup[] getGroupsByType(BonusType type) {
		switch (type) {
			case BOSS:
				return itemGroups.getBossGroups();
			case ENCHANT:
				return itemGroups.getEnchantGroups();
			case FOOD:
				return itemGroups.getFoodGroups();
			case GATHER:
				return (BonusItemGroup[]) ArrayUtils.addAll(itemGroups.getOreGroups(), itemGroups.getGatherGroups());
			case MANASTONE:
				return itemGroups.getManastoneGroups();
			case MEDICINE:
				return itemGroups.getMedicineGroups();
			case TASK:
				return itemGroups.getCraftGroups();
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + type + " is not implemented");
				return null;
		}
	}

	public BonusItemGroup getRandomGroup(BonusItemGroup[] groups) {
		float total = 0;
		if (groups == null)
			return null;

		for (BonusItemGroup gr : groups)
			total += gr.getChance();
		if (total == 0)
			return null;

		BonusItemGroup chosenGroup = null;
		if (groups != null) {
			int percent = 100;
			for (BonusItemGroup gr : groups) {
				float chance = getNormalizedChance(gr.getChance(), total);
				if (Rnd.get(0, percent) <= chance) {
					chosenGroup = gr;
					break;
				}
				else
					percent -= chance;
			}
		}
		return chosenGroup;
	}

	float getNormalizedChance(float chance, float total) {
		return chance * 100f / total;
	}

	public BonusItemGroup getRandomGroup(BonusType type) {
		return getRandomGroup(getGroupsByType(type));
	}

	public QuestItems getQuestBonus(Player player, QuestTemplate questTemplate) {
		List<QuestBonuses> bonuses = questTemplate.getBonus();
		if (bonuses.isEmpty())
			return null;
		// Only one
		QuestBonuses bonus = bonuses.get(0);
		if (bonus.getType() == BonusType.NONE)
			return null;

		switch (bonus.getType()) {
			case TASK:
				return getCraftBonus(player, questTemplate);
			case MANASTONE:
				return getManastoneBonus(player, bonus);
			case MEDAL:
				return getMedalBonus(player, questTemplate);
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + bonus.getType() + " is not implemented");
				return null;
		}
	}

	QuestItems getCraftBonus(Player player, QuestTemplate questTemplate) {
		BonusItemGroup[] groups = itemGroups.getCraftGroups();
		CraftGroup group = null;
		ItemRaceEntry[] allRewards = null;

		while (groups != null && groups.length > 0 && group == null) {
			group = (CraftGroup) getRandomGroup(groups);
			if (group == null)
				break;
			allRewards = group.getRewards(questTemplate.getCombineSkill(), questTemplate.getCombineSkillPoint());
			if (allRewards.length == 0) {
				List<BonusItemGroup> temp = new ArrayList<BonusItemGroup>();
				Collections.addAll(temp, groups);
				temp.remove(group);
				group = null;
				groups = temp.toArray(new BonusItemGroup[0]);
			}
		}

		if (group == null) // probably all chances set to 0
			return null;
		List<ItemRaceEntry> finalList = new ArrayList<ItemRaceEntry>();

		for (int i = 0; i < allRewards.length; i++) {
			ItemRaceEntry r = allRewards[i];
			if (!r.checkRace(player.getCommonData().getRace()))
				continue;
			finalList.add(r);
		}

		if (finalList.isEmpty())
			return null;

		int itemIndex = Rnd.get(finalList.size());
		int itemCount = 1;

		ItemRaceEntry reward = finalList.get(itemIndex);
		if (reward instanceof CraftItem)
			itemCount = Rnd.get(3, 5);

		return new QuestItems(reward.getId(), itemCount);
	}

	QuestItems getMedalBonus(Player player, QuestTemplate template) {
		BonusItemGroup[] groups = itemGroups.getMedalGroups();
		MedalGroup group = (MedalGroup) getRandomGroup(groups);
		int bonusLevel = template.getBonus().get(0).getLevel();

		MedalItem finalReward = null;

		float total = 0;
		for (MedalItem medal : group.getItems()) {
			if (medal.getLevel() == bonusLevel)
				total += medal.getChance();
		}

		if (total == 0)
			return null;

		float rnd = (Rnd.get() * total);
		float luck = 0;
		for (MedalItem medal : group.getItems()) {

			if(medal.getLevel() != bonusLevel)
				continue;
			luck += medal.getChance();

				if (rnd <= luck) {
					finalReward = medal;
					break;
				}
		}
		return finalReward != null ? new QuestItems(finalReward.getId(), finalReward.getCount()) : null;
	}

	QuestItems getManastoneBonus(Player player, QuestBonuses bonus) {
		ManastoneGroup group = (ManastoneGroup) getRandomGroup(BonusType.MANASTONE);
		ItemRaceEntry[] allRewards = group.getRewards();
		List<ItemRaceEntry> finalList = new ArrayList<ItemRaceEntry>();
		for (int i = 0; i < allRewards.length; i++) {
			ItemRaceEntry r = allRewards[i];
			ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(r.getId());
			if (bonus.getLevel() != template.getLevel())
				continue;
			finalList.add(r);
		}
		if (finalList.isEmpty())
			return null;

		int itemIndex = Rnd.get(finalList.size());
		ItemRaceEntry reward = finalList.get(itemIndex);
		return new QuestItems(reward.getId(), 1);
	}
}
