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

import org.typezero.gameserver.model.templates.housing.Building;
import org.typezero.gameserver.model.templates.housing.HouseAddress;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.model.templates.housing.HousingLand;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "lands" })
@XmlRootElement(name = "house_lands")
public class HouseData {

	@XmlElement(name = "land")
	protected List<HousingLand> lands;

	@XmlTransient
	Map<Integer, HousingLand> landsById = new HashMap<Integer, HousingLand>();

	@XmlTransient
	Map<Integer, Set<HousingLand>> landsByEntryWorldId = new HashMap<Integer, Set<HousingLand>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (lands == null)
			return;

		for (HousingLand land : lands) {
			landsById.put(land.getId(), land);
			for (HouseAddress address : land.getAddresses()) {
				Integer exitMapId = address.getExitMapId();
				if (exitMapId == null)
					exitMapId = address.getMapId();
				Set<HousingLand> landList = landsByEntryWorldId.get(exitMapId);
				if (landList == null) {
					landList = new HashSet<HousingLand>();
					landsByEntryWorldId.put(exitMapId, landList);
				}
				landList.add(land);
			}
		}

		lands.clear();
		lands = null;
	}

	public Set<HousingLand> getLandsForWorldId(int worldId) {
		return landsByEntryWorldId.get(worldId);
	}

	public HousingLand getLandForHouse(int worldId, HouseType houseSize) {
		Set<HousingLand> worldHouseAreas = landsByEntryWorldId.get(worldId);
		if (worldHouseAreas == null)
			return null;
		for (HousingLand land : worldHouseAreas) {
			for (Building building : land.getBuildings()) {
				if (houseSize.value().equals(building.getSize()))
					return land;
			}
		}
		return null;
	}

	public HousingLand getLand(int landId) {
		return landsById.get(landId);
	}

	public Collection<HousingLand> getLands() {
		return landsById.values();
	}

	public int size() {
		return landsById.size();
	}
}
