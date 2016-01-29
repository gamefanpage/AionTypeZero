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

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.templates.chest.ChestTemplate;

/**
 * @author Wakizashi
 */
@XmlRootElement(name = "chest_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChestData {

	@XmlElement(name = "chest")
	private List<ChestTemplate> chests;

	/** A map containing all npc templates */
	private TIntObjectHashMap<ChestTemplate> chestData = new TIntObjectHashMap<ChestTemplate>();
	private TIntObjectHashMap<ArrayList<ChestTemplate>> instancesMap = new TIntObjectHashMap<ArrayList<ChestTemplate>>();
	private THashMap<String, ChestTemplate> namedChests = new THashMap<String, ChestTemplate>();

	/**
	 * - Inititialize all maps for subsequent use - Don't nullify initial chest list as it will be used during reload
	 *
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		chestData.clear();
		instancesMap.clear();
		namedChests.clear();

		for (ChestTemplate chest : chests) {
			chestData.put(chest.getNpcId(), chest);
			if (chest.getName() != null && !chest.getName().isEmpty())
				namedChests.put(chest.getName(), chest);
		}
	}

	public int size() {
		return chestData.size();
	}

	/**
	 * @param npcId
	 * @return
	 */
	public ChestTemplate getChestTemplate(int npcId) {
		return chestData.get(npcId);
	}

	/**
	 * @return the chests
	 */
	public List<ChestTemplate> getChests() {
		return chests;
	}

	/**
	 * @param chests
	 *          the chests to set
	 */
	public void setChests(List<ChestTemplate> chests) {
		this.chests = chests;
		afterUnmarshal(null, null);
	}
}
