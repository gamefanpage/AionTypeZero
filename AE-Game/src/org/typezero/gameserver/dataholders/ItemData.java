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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemMask;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.restriction.ItemCleanupTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Luno
 */
@XmlRootElement(name = "item_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData extends ReloadableData {

	@XmlElement(name = "item_template")
	private List<ItemTemplate> its;

	@XmlTransient
	private TIntObjectHashMap<ItemTemplate> items;

	@XmlTransient
	Map<Integer, List<ItemTemplate>> manastones = new HashMap<Integer, List<ItemTemplate>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		items = new TIntObjectHashMap<ItemTemplate>();
		for (ItemTemplate it : its) {
			items.put(it.getTemplateId(), it);
			if (it.getCategory().equals(ItemCategory.MANASTONE)) {
				int level = it.getLevel();
				if (!manastones.containsKey(level)) {
					manastones.put(level, new ArrayList<ItemTemplate>());
				}
				manastones.get(level).add(it);
			}
			/** NOT USED
			if (it.getActions() == null)
				continue;
			AdoptPetAction adoptAction = it.getActions().getAdoptPetAction();
			if (adoptAction != null) {
				petEggs.put(adoptAction.getPetId(), it);
			}
			*/
		}
		its = null;
	}

	public void cleanup() {
		for (ItemCleanupTemplate ict : DataManager.ITEM_CLEAN_UP.getList()) {
			ItemTemplate template = items.get(ict.getId());
			applyCleanup(template, ict.resultTrade(), ItemMask.TRADEABLE);
			applyCleanup(template, ict.resultSell(), ItemMask.SELLABLE);
			applyCleanup(template, ict.resultWH(), ItemMask.STORABLE_IN_WH);
			applyCleanup(template, ict.resultAccountWH(), ItemMask.STORABLE_IN_AWH);
			applyCleanup(template, ict.resultLegionWH(), ItemMask.STORABLE_IN_LWH);
		}
	}

	private void applyCleanup(ItemTemplate item, byte result, int mask) {
		if (result != -1) {
			switch (result) {
				case 1:
					item.modifyMask(true, mask);
					break;
				case 0:
					item.modifyMask(false, mask);
					break;
			}
		}
	}

	public ItemTemplate getItemTemplate(int itemId) {
		return items.get(itemId);
	}

	/**
	 * @return items.size()
	 */
	public int size() {
		return items.size();
	}

	public Map<Integer, List<ItemTemplate>> getManastones() {
		return manastones;
	}

	//public ItemTemplate getPetEggTemplate(int petId) {
	//	return petEggs.get(petId);
	//}


	@Override
	public void reload(Player admin) {
		try {
			JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema("./data/static_data/static_data.xsd"));
			List<ItemTemplate> newTemplates = new ArrayList<ItemTemplate>();
			ItemData data = (ItemData) un.unmarshal(new File("./data/static_data/items/item_templates.xml"));
			if (data != null && data.getData() != null)
					newTemplates.addAll(data.getData());
			DataManager.ITEM_DATA.setData(newTemplates);
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "Item templates reload failed!");
			log.error("Item templates reload failed!", e);
		}
		finally {
			PacketSendUtility.sendMessage(admin, "Item templates reload Success! Total loaded: "+DataManager.ITEM_DATA.size());
		}
	}

	@Override
	protected List<ItemTemplate> getData() {
		return its;
	}

    public TIntObjectHashMap<ItemTemplate> getItemData() {
        return items;
    }

	@SuppressWarnings("unchecked")
	@Override
	protected void setData(List<?> data) {
		this.its = (List<ItemTemplate>) data;
		this.afterUnmarshal(null, null);
	}
}
