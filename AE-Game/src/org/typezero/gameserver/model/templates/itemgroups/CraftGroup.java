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

package org.typezero.gameserver.model.templates.itemgroups;

import javolution.util.FastMap;
import org.apache.commons.lang.math.IntRange;
import org.typezero.gameserver.model.templates.rewards.CraftReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Rolandas
 */
public abstract class CraftGroup extends BonusItemGroup {

	private FastMap<Integer, FastMap<IntRange, List<CraftReward>>> dataHolder;

	public ItemRaceEntry[] getRewards(Integer skillId) {
		if (!dataHolder.containsKey(skillId))
			return new ItemRaceEntry[0];
		List<ItemRaceEntry> result = new ArrayList<ItemRaceEntry>();
		for (List<CraftReward> items : dataHolder.get(skillId).values())
			result.addAll(items);
		return result.toArray(new ItemRaceEntry[0]);
	}

	public ItemRaceEntry[] getRewards(Integer skillId, Integer skillPoints) {
		if (!dataHolder.containsKey(skillId))
			return new ItemRaceEntry[0];
		List<ItemRaceEntry> result = new ArrayList<ItemRaceEntry>();
		for (Entry<IntRange, List<CraftReward>> entry : dataHolder.get(skillId).entrySet()) {
			if (!entry.getKey().containsInteger(skillPoints))
				continue;
			result.addAll(entry.getValue());
		}
		return result.toArray(new ItemRaceEntry[0]);
	}

	/**
	 * @return the dataHolder
	 */
	public FastMap<Integer, FastMap<IntRange, List<CraftReward>>> getDataHolder() {
		return dataHolder;
	}

	/**
	 * @param dataHolder the dataHolder to set
	 */
	public void setDataHolder(FastMap<Integer, FastMap<IntRange, List<CraftReward>>> dataHolder) {
		this.dataHolder = dataHolder;
	}
}
