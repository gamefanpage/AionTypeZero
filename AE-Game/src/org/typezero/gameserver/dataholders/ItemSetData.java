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

import org.typezero.gameserver.model.templates.itemset.ItemPart;
import org.typezero.gameserver.model.templates.itemset.ItemSetTemplate;

/**
 * @author ATracer
 */
@XmlRootElement(name = "item_sets")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemSetData {

	@XmlElement(name = "itemset")
	protected List<ItemSetTemplate> itemsetList;

	private TIntObjectHashMap<ItemSetTemplate> sets;

	// key: item id, value: associated item set template
	// This should provide faster search of the item template set by item id
	private TIntObjectHashMap<ItemSetTemplate> setItems;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		sets = new TIntObjectHashMap<ItemSetTemplate>();
		setItems = new TIntObjectHashMap<ItemSetTemplate>();

		for (ItemSetTemplate set : itemsetList) {
			sets.put(set.getId(), set);

			// Add reference to the ItemSetTemplate from
			for (ItemPart part : set.getItempart()) {
				setItems.put(part.getItemid(), set);
			}
		}
		itemsetList = null;
	}

	/**
	 * @param itemSetId
	 * @return
	 */
	public ItemSetTemplate getItemSetTemplate(int itemSetId) {
		return sets.get(itemSetId);
	}

	/**
	 * @param itemId
	 * @return
	 */
	public ItemSetTemplate getItemSetTemplateByItemId(int itemId) {
		return setItems.get(itemId);
	}

	/**
	 * @return itemSets.size()
	 */
	public int size() {
		return sets.size();
	}
}
