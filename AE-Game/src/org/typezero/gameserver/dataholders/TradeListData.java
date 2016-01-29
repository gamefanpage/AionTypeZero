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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 *
 * @author Luno
 */
@XmlRootElement(name = "npc_trade_list")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeListData {

	@XmlElement(name = "tradelist_template")
	private List<TradeListTemplate> tlist;

	@XmlElement(name = "trade_in_list_template")
	private List<TradeListTemplate> tInlist;

	@XmlElement(name = "purchase_template")
	private List<TradeListTemplate> plist;

	/** A map containing all trade list templates */
	private TIntObjectHashMap<TradeListTemplate> npctlistData = new TIntObjectHashMap<TradeListTemplate>();

	private TIntObjectHashMap<TradeListTemplate> npcTradeInlistData = new TIntObjectHashMap<TradeListTemplate>();

	private TIntObjectHashMap<TradeListTemplate> npcPurchaseTemplateData = new TIntObjectHashMap<TradeListTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (TradeListTemplate npc : tlist) {
			npctlistData.put(npc.getNpcId(), npc);
		}

		for (TradeListTemplate npc : tInlist) {
			npcTradeInlistData.put(npc.getNpcId(), npc);
		}
		for (TradeListTemplate npc : plist) {
			npcPurchaseTemplateData.put(npc.getNpcId(), npc);
		}
	}

	public int size() {
		return npctlistData.size();
	}

	/**
	 * Returns an {@link TradeListTemplate} object with given id.
	 *
	 * @param id
	 *          id of NPC
	 * @return TradeListTemplate object containing data about NPC with that id.
	 */
	public TradeListTemplate getTradeListTemplate(int id) {
		return npctlistData.get(id);
	}

	public TradeListTemplate getTradeInListTemplate(int id) {
		return npcTradeInlistData.get(id);
	}

	public TradeListTemplate getPurchaseTemplate(int id) {
		return npcPurchaseTemplateData.get(id);
	}

	/**
	 * @return id of NPC.
	 */
	public TIntObjectHashMap<TradeListTemplate> getTradeListTemplate() {
		return npctlistData;
	}

}
