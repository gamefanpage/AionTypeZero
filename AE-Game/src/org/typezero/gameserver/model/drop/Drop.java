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

package org.typezero.gameserver.model.drop;

import java.nio.ByteBuffer;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.w3c.dom.Node;
import java.util.Collection;

/**
 * @author MrPoke
 *
 */
public class Drop implements DropCalculator {

	private int itemId;
	private int minAmount;
	private int maxAmount;
	private float chance;
	private boolean noReduce = false;
	private boolean eachMember = false;
	private ItemTemplate template;

	public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
		this.noReduce = noReduce;
		template = DataManager.ITEM_DATA.getItemTemplate(itemId);
	}

	/**
	 *
	 */
	public Drop() {
	}

	public ItemTemplate getItemTemplate() {
		return template == null ? DataManager.ITEM_DATA.getItemTemplate(itemId) : template;
	}

	/**
	 * Gets the value of the itemId property.
	 *
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Gets the value of the minAmount property.
	 *
	 */
	public int getMinAmount() {
		return minAmount;
	}

	/**
	 * Gets the value of the maxAmount property.
	 *
	 */
	public int getMaxAmount() {
		return maxAmount;
	}

	/**
	 * Gets the value of the chance property.
	 *
	 */
	public float getChance() {
		return chance;
	}

	public boolean isNoReduction() {
		return noReduce;
	}

	public Boolean isEachMember() {
		return eachMember;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
		float percent = chance;
		if (!noReduce) {
			percent *= dropModifier;
		}
		if (Rnd.get() * 100 < percent) {
			if (eachMember && groupMembers != null && !groupMembers.isEmpty()) {
				for (Player player : groupMembers) {
					DropItem dropitem = new DropItem(this);
					dropitem.calculateCount();
					dropitem.setIndex(index++);
					dropitem.setPlayerObjId(player.getObjectId());
					dropitem.setWinningPlayer(player);
					dropitem.isDistributeItem(true);
					result.add(dropitem);
				}
			}
			else {
				DropItem dropitem = new DropItem(this);
				dropitem.calculateCount();
				dropitem.setIndex(index++);
				result.add(dropitem);
			}
		}
		return index;
	}

	public static Drop load(ByteBuffer buffer){
		Drop drop = new Drop();
		drop.itemId = buffer.getInt();
		drop.chance = buffer.getFloat();
		drop.minAmount = buffer.getInt();
		drop.maxAmount = buffer.getInt();
		drop.noReduce = buffer.get() == 1? true : false;
		drop.eachMember = buffer.get() == 1? true : false;
		return drop;
	}

    public static Drop loadxml(Node node){
        Drop drop = new Drop();
        drop.itemId = Integer.parseInt(node.getAttributes().getNamedItem("item_id").getNodeValue());
        drop.chance = Float.parseFloat(node.getAttributes().getNamedItem("chance").getNodeValue());
        drop.minAmount = Integer.parseInt(node.getAttributes().getNamedItem("min_amount").getNodeValue());
        drop.maxAmount = Integer.parseInt(node.getAttributes().getNamedItem("max_amount").getNodeValue());
        drop.noReduce = node.getAttributes().getNamedItem("no_reduce") != null ? Boolean.parseBoolean(node.getAttributes().getNamedItem("no_reduce").getNodeValue()) : false;
        drop.eachMember = node.getAttributes().getNamedItem("eachmember") != null ? Boolean.parseBoolean(node.getAttributes().getNamedItem("eachmember").getNodeValue()) : false;
        return drop;
    }
	@Override
	public String toString() {
		return "Drop [itemId=" + itemId + ", minAmount=" + minAmount + ", maxAmount=" + maxAmount + ", chance=" + chance
			+ ", noReduce=" + noReduce + ", eachMember=" + eachMember + "]";
	}


}
