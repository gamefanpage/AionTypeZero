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

package org.typezero.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.items.RandomBonusResult;
import org.typezero.gameserver.model.templates.item.bonuses.RandomBonus;
import org.typezero.gameserver.model.templates.item.bonuses.StatBonusType;
import org.typezero.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * @author Rolandas
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "randomBonuses" })
@XmlRootElement(name = "random_bonuses")
public class ItemRandomBonusData {

	@XmlElement(name = "random_bonus", required = true)
	protected List<RandomBonus> randomBonuses;

	@XmlTransient
	private TIntObjectHashMap<RandomBonus> inventoryRandomBonusData = new TIntObjectHashMap<RandomBonus>();

	@XmlTransient
	private TIntObjectHashMap<RandomBonus> polishRandomBonusData = new TIntObjectHashMap<RandomBonus>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (RandomBonus bonus : randomBonuses) {
			getBonusMap(bonus.getBonusType()).put(bonus.getId(), bonus);
		}
		randomBonuses.clear();
		randomBonuses = null;
	}

	private TIntObjectHashMap<RandomBonus> getBonusMap(StatBonusType bonusType) {
		if (bonusType == StatBonusType.INVENTORY)
			return inventoryRandomBonusData;
		return polishRandomBonusData;
	}

	/**
	 * Gets a randomly chosen modifiers from bonus list.
	 *
	 * @param item
	 *          rnd_bonus from the item template
	 * @return null if not a chance
	 */
	public RandomBonusResult getRandomModifiers(StatBonusType bonusType, int rndOptionSet) {
		RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
		if (bonus == null)
			return null;

		List<ModifiersTemplate> modifiersGroup = bonus.getModifiers();

		int chance = Rnd.get(10000);
		int current = 0;
		ModifiersTemplate template = null;
		int number = 0;

		for (int i = 0; i < modifiersGroup.size(); i++) {
			ModifiersTemplate modifiers = modifiersGroup.get(i);

			current += modifiers.getChance() * 100;
			if (current >= chance) {
				template = modifiers;
				number = i + 1;
				break;
			}
		}
		return template == null ? null : new RandomBonusResult(template, number);
	}

	public ModifiersTemplate getTemplate(StatBonusType bonusType, int rndOptionSet, int number) {
		RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
		if (bonus == null)
			return null;
		return bonus.getModifiers().get(number - 1);
	}

	public int size() {
		return inventoryRandomBonusData.size() + polishRandomBonusData.size();
	}

}
