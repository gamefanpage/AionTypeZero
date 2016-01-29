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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.templates.world.WorldMapTemplate;

/**
 * Object of this class is containing <tt>WorldMapTemplate</tt> objects for all world maps. World maps are defined in
 * data/static_data/world_maps.xml file.
 *
 * @author Luno
 */
@XmlRootElement(name = "world_maps")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapsData implements Iterable<WorldMapTemplate> {

	@XmlElement(name = "map")
	protected List<WorldMapTemplate> worldMaps;

	protected TIntObjectHashMap<WorldMapTemplate> worldIdMap = new TIntObjectHashMap<WorldMapTemplate>();

	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		for (WorldMapTemplate map : worldMaps) {
			worldIdMap.put(map.getMapId(), map);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<WorldMapTemplate> iterator() {
		return worldMaps.iterator();
	}

	/**
	 * Returns the count of maps.
	 *
	 * @return worldMaps.size()
	 */
	public int size() {
		return worldMaps == null ? 0 : worldMaps.size();
	}

	/**
	 * @param worldId
	 * @return
	 */
	public WorldMapTemplate getTemplate(int worldId) {
		return worldIdMap.get(worldId);
	}
}
