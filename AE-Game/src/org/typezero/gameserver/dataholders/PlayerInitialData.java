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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.templates.item.ItemTemplate;

/**
 * This table contains all nesessary data for new players. <br/>
 * Created on: 09.08.2009 18:20:41
 *
 * @author Aquanox
 */
@XmlRootElement(name = "player_initial_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerInitialData {

	@XmlElement(name = "player_data")
	private List<PlayerCreationData> dataList = new ArrayList<PlayerCreationData>();

	@XmlElement(name = "elyos_spawn_location", required = true)
	private LocationData elyosSpawnLocation;
	@XmlElement(name = "asmodian_spawn_location", required = true)
	private LocationData asmodianSpawnLocation;

	private THashMap<PlayerClass, PlayerCreationData> data = new THashMap<PlayerClass, PlayerCreationData>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (PlayerCreationData pt : dataList) {
			data.put(pt.getRequiredPlayerClass(), pt);
		}

		dataList.clear();
		dataList = null;
	}

	public PlayerCreationData getPlayerCreationData(PlayerClass cls) {
		return data.get(cls);
	}

	public int size() {
		return data.size();
	}

	public LocationData getSpawnLocation(Race race) {
		switch (race) {
			case ASMODIANS:
				return asmodianSpawnLocation;
			case ELYOS:
				return elyosSpawnLocation;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Player creation data holder.
	 */
	public static class PlayerCreationData {

		@XmlAttribute(name = "class")
		private PlayerClass requiredPlayerClass;

		@XmlElement(name = "items")
		private ItemsType itemsType;

		// @XmlElement(name="shortcuts")
		// private ShortcutType shortcutData;

		PlayerClass getRequiredPlayerClass() {
			return requiredPlayerClass;
		}

		public List<ItemType> getItems() {
			return Collections.unmodifiableList(itemsType.items);
		}

		static class ItemsType {

			@XmlElement(name = "item")
			public List<ItemType> items = new ArrayList<ItemType>();
		}

		public static class ItemType {

			@XmlAttribute(name = "id")
			@XmlIDREF
			public ItemTemplate template;

			@XmlAttribute(name = "count")
			public int count;

			public ItemTemplate getTemplate() {
				return template;
			}

			public int getCount() {
				return count;
			}

			@Override
			public String toString() {
				final StringBuilder sb = new StringBuilder();
				sb.append("ItemType");
				sb.append("{template=").append(template);
				sb.append(", count=").append(count);
				sb.append('}');
				return sb.toString();
			}
		}

		// public static class ShortcutType
		// {
		// public List<Shortcut> shortcuts;
		// }
	}

	/**
	 * Location data holder.
	 */
	public static class LocationData {

		@XmlAttribute(name = "map_id")
		private int mapId;
		@XmlAttribute(name = "x")
		private float x;
		@XmlAttribute(name = "y")
		private float y;
		@XmlAttribute(name = "z")
		private float z;
		@XmlAttribute(name = "heading")
		private byte heading;

		LocationData() {
			//
		}

		public int getMapId() {
			return mapId;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public float getZ() {
			return z;
		}

		public byte getHeading() {
			return heading;
		}
	}

}
