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

package org.typezero.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.services.toypet.PetFeedCalculator;
import org.typezero.gameserver.services.toypet.PetFeedProgress;
import org.typezero.gameserver.services.toypet.PetHungryLevel;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetFlavour", propOrder = { "food" })
public class PetFlavour {

	@XmlElement(required = true)
	protected List<PetRewards> food;

	@XmlAttribute(required = true)
	protected int id;

	@XmlAttribute(name = "full_count")
	protected int fullCount = 1;

	@XmlAttribute(name = "loved_limit")
	protected int lovedFoodLimit = 0;

	@XmlAttribute(name = "cd", required = true)
	protected int cooldown = 0;

	public List<PetRewards> getFood() {
		if (food == null) {
			food = new ArrayList<PetRewards>();
		}
		return this.food;
	}

	/**
	 * Returns a food group for the itemId. Null if doesn't match
	 * @param itemId
	 */
	public FoodType getFoodType(int itemId) {
		for (PetRewards rewards : getFood()) {
			if (DataManager.ITEM_GROUPS_DATA.isFood(itemId, rewards.getType()))
				return rewards.getType();
		}
		return null;
	}

	/**
	 * Returns reward details if earned, otherwise null. Updates progress automatically
	 * @param progress
	 * @param itemId
	 * @return
	 */
	public PetFeedResult processFeedResult(PetFeedProgress progress, FoodType foodType, int itemLevel, int playerLevel) {
		PetRewards rewardGroup = null;
		for (PetRewards rewards : getFood()) {
			if (rewards.getType() == foodType) {
				rewardGroup = rewards;
				break;
			}
		}
		if (rewardGroup == null)
			return null;

		int maxFeedCount = 1;
		if (rewardGroup.isLoved()) {
			progress.setIsLovedFeeded();
		} else {
			maxFeedCount = fullCount;
		}

		PetFeedCalculator.updatePetFeedProgress(progress, itemLevel, maxFeedCount);
		if (progress.getHungryLevel() != PetHungryLevel.FULL)
			return null;

		return PetFeedCalculator.getReward(maxFeedCount, rewardGroup, progress, playerLevel);
	}

	public boolean isLovedFood(FoodType foodType, int itemId) {
		PetRewards rewardGroup = null;
		for (PetRewards rewards : getFood()) {
			if (rewards.getType() == foodType) {
				rewardGroup = rewards;
				break;
			}
		}
		if (rewardGroup == null)
			return false;
		return rewardGroup.isLoved();
	}

	public int getId() {
		return id;
	}

	public int getFullCount() {
		return fullCount;
	}

	public int getLovedFoodLimit() {
		return lovedFoodLimit;
	}

	public int getCooldDown() {
		return cooldown;
	}

}
