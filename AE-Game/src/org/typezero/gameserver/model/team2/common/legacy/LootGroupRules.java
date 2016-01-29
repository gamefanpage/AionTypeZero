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

package org.typezero.gameserver.model.team2.common.legacy;

import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.player.InRoll;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemQuality;
import org.typezero.gameserver.services.drop.DropDistributionService;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.Collection;
import javolution.util.FastList;

/**
 * @author ATracer, xTz
 */
public class LootGroupRules {

	private LootRuleType lootRule;
	private LootDistribution autodistribution;
	private int common_item_above;
	private int superior_item_above;
	private int heroic_item_above;
	private int fabled_item_above;
	private int ethernal_item_above;
	private int misc;
	private int nrMisc;
	private int nrRoundRobin;
	private FastList<DropItem> itemsToBeDistributed = new FastList<DropItem>();

	public LootGroupRules() {
		lootRule = LootRuleType.ROUNDROBIN;
		autodistribution = LootDistribution.ROLL_DICE;
		common_item_above = 0;
		superior_item_above = 2;
		heroic_item_above = 2;
		fabled_item_above = 2;
		ethernal_item_above = 2;
	}

	public LootGroupRules(LootRuleType lootRule, LootDistribution autodistribution, int commonItemAbove,
		int superiorItemAbove, int heroicItemAbove, int fabledItemAbove, int ethernalItemAbove, int misc) {
		super();
		this.lootRule = lootRule;
		this.autodistribution = autodistribution;
		this.misc = misc;
		common_item_above = commonItemAbove;
		superior_item_above = superiorItemAbove;
		heroic_item_above = heroicItemAbove;
		fabled_item_above = fabledItemAbove;
		ethernal_item_above = ethernalItemAbove;
	}

	/**
	 * @param quality
	 * @return
	 */
	public boolean getQualityRule(ItemQuality quality) {
		switch (quality) {
			case COMMON: // White
				return common_item_above != 0;
			case RARE: // Green
				return superior_item_above != 0;
			case LEGEND: // Blue
				return heroic_item_above != 0;
			case UNIQUE: // Yellow
				return fabled_item_above != 0;
			case MYTHIC: // Orange
				return ethernal_item_above != 0;
			case EPIC: // Purple
				return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * @param quality
	 * @return
	 */
	public boolean isMisc(ItemQuality quality) {
		return quality.equals(ItemQuality.JUNK) && misc == 1;
	}

	/**
	 * @return the lootRule
	 */
	public LootRuleType getLootRule() {
		return lootRule;
	}

	/**
	 * @return the autodistribution
	 */
	public LootDistribution getAutodistribution() {
		return autodistribution;
	}

	/**
	 * @return the common_item_above
	 */
	public int getCommonItemAbove() {
		return common_item_above;
	}

	/**
	 * @return the superior_item_above
	 */
	public int getSuperiorItemAbove() {
		return superior_item_above;
	}

	/**
	 * @return the heroic_item_above
	 */
	public int getHeroicItemAbove() {
		return heroic_item_above;
	}

	/**
	 * @return the fabled_item_above
	 */
	public int getFabledItemAbove() {
		return fabled_item_above;
	}

	/**
	 * @return the ethernal_item_above
	 */
	public int getEthernalItemAbove() {
		return ethernal_item_above;
	}

	/**
	 * @return the nrMisc
	 */
	public int getNrMisc() {
		return nrMisc;
	}

	/**
	 * @param nrMisc
	 *          .
	 */
	public void setNrMisc(int nrMisc) {
		this.nrMisc = nrMisc;
	}

	public void setPlayersInRoll(final Collection<Player> players, int time, final int index, final int npcId) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				for (Player player : players) {
					if (player.isInPlayerMode(PlayerMode.IN_ROLL)) {
						InRoll inRoll = player.inRoll;
						switch (inRoll.getRollType()) {
							case 2:
								if (inRoll.getIndex() == index && inRoll.getNpcId() == npcId)
									DropDistributionService.getInstance().handleRoll(player, 0, inRoll.getItemId(), inRoll.getNpcId(),
										inRoll.getIndex());
								break;
							case 3:
								if (inRoll.getIndex() == index && inRoll.getNpcId() == npcId)
									DropDistributionService.getInstance().handleBid(player, 0, inRoll.getItemId(), inRoll.getNpcId(),
										inRoll.getIndex());
								break;
						}
					}
				}
			}
		}, time);
	}

	/**
	 * @return the nrRoundRobin
	 */
	public int getNrRoundRobin() {
		return nrRoundRobin;
	}

	/**
	 * @param nrRoundRobin
	 *          .
	 */
	public void setNrRoundRobin(int nrRoundRobin) {
		this.nrRoundRobin = nrRoundRobin;
	}

	public int getMisc() {
		return misc;
	}

	public void addItemToBeDistributed(DropItem dropItem) {
		itemsToBeDistributed.add(dropItem);
	}

	public boolean containDropItem(DropItem dropItem) {
		return itemsToBeDistributed.contains(dropItem);
	}

	public void removeItemToBeDistributed(DropItem dropItem) {
		itemsToBeDistributed.remove(dropItem);
	}

	public FastList<DropItem> getItemsToBeDistributed() {
		return itemsToBeDistributed;
	}
}
