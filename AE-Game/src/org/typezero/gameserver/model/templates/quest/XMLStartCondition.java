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
package org.typezero.gameserver.model.templates.quest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.QuestStateList;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * Checks quest start conditions, listed in quest_data.xml
 *
 * @author antness
 * @reworked vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartConditions")
public class XMLStartCondition {

	@XmlElement(name = "finished")
	protected List<FinishedQuestCond> finished;
	@XmlList
	@XmlElement(name = "unfinished", type = Integer.class)
	protected List<Integer> unfinished;
	@XmlList
	@XmlElement(name = "noacquired", type = Integer.class)
	protected List<Integer> noacquired;
	@XmlList
	@XmlElement(name = "acquired", type = Integer.class)
	protected List<Integer> acquired;
	@XmlList
	@XmlElement(name = "equipped", type = Integer.class)
	protected List<Integer> equipped;

	/** Check, if the player has finished listed quests */
	private boolean checkFinishedQuests(QuestStateList qsl) {
		if (finished != null && finished.size() > 0) {
			for (FinishedQuestCond fqc : finished) {
				int questId = fqc.getQuestId();
				int reward = fqc.getReward();
				QuestState qs = qsl.getQuestState(questId);
				if (qs == null || qs.getStatus() != QuestStatus.COMPLETE || !checkReward(questId, reward, qs.getReward())) {
					return false;
				}
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
				if (template != null && template.isRepeatable()) {
					if (qs.getCompleteCount() != template.getMaxRepeatCount()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/** Check, if the player has not finished listed quests */
	private boolean checkUnfinishedQuests(QuestStateList qsl) {
		if (unfinished != null && unfinished.size() > 0) {
			for (Integer questId : unfinished) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs != null && qs.getStatus() == QuestStatus.COMPLETE)
					return false;
			}
		}
		return true;
	}

	/** Check, if the player has not acquired listed quests */
	private boolean checkNoAcquiredQuests(QuestStateList qsl) {
		if (noacquired != null && noacquired.size() > 0) {
			for (Integer questId : noacquired) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs != null
					&& (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD))
					return false;
			}
		}
		return true;
	}

	/** Check, if the player has acquired listed quests */
	private boolean checkAcquiredQuests(QuestStateList qsl) {
		if (acquired != null && acquired.size() > 0) {
			for (Integer questId : acquired) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.LOCKED)
					return false;
			}
		}
		return true;
	}

	private boolean checkEquippedItems(Player player, boolean warn) {
		if (!warn)
			return true;
		if (equipped != null && equipped.size() > 0) {
			for (int itemId : equipped) {
				if (!player.getEquipment().getEquippedItemIds().contains(itemId)) {
					int requiredItemNameId = DataManager.ITEM_DATA.getItemTemplate(itemId).getNameId();
					PacketSendUtility.sendPacket(player,
						SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_EQUIP_ITEM(new DescriptionId(requiredItemNameId)));
					return false;
				}
			}
		}
		return true;
	}

	/** Check all conditions */
	public boolean check(Player player, boolean warn) {
		QuestStateList qsl = player.getQuestStateList();
		return checkFinishedQuests(qsl) && checkUnfinishedQuests(qsl) && checkAcquiredQuests(qsl)
			&& checkNoAcquiredQuests(qsl) && checkEquippedItems(player, warn);
	}

	private boolean checkReward(int questId, int neededReward, int currentReward) {
		// Temporary exceptions-quests till abyss entry quests work with correct reward
		if (neededReward != currentReward && questId != 2947 && questId != 1922) {
			return false;
		}
		return true;
	}

	public List<FinishedQuestCond> getFinishedPreconditions() {
		return finished;
	}
}
