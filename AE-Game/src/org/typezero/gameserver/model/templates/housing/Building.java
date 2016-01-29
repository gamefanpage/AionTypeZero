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

package org.typezero.gameserver.model.templates.housing;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.typezero.gameserver.dataholders.DataManager;
import com.mysql.jdbc.StringUtils;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "parts" })
@XmlRootElement(name = "building")
public class Building {

	private Parts parts;

	@XmlAttribute(name = "default")
	protected boolean isDefault;

	@XmlAttribute(name = "parts_match")
	protected String partsMatch;

	@XmlAttribute
	protected String size;

	@XmlAttribute
	protected BuildingType type;

	@XmlAttribute(required = true)
	protected int id;

	public boolean isDefault() {
		return isDefault;
	}

	@XmlTransient
	Map<PartType, Integer> partsByType = new HashMap<PartType, Integer>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parts == null)
			return;
		if (parts.getDoor() != 0)
			partsByType.put(PartType.DOOR, parts.getDoor());
		if (parts.getFence() != null)
			partsByType.put(PartType.FENCE, parts.getFence());
		if (parts.getFrame() != null)
			partsByType.put(PartType.FRAME, parts.getFrame());
		if (parts.getGarden() != null)
			partsByType.put(PartType.GARDEN, parts.getGarden());
		if (parts.getInfloor() != 0)
			partsByType.put(PartType.INFLOOR_ANY, parts.getInfloor());
		if (parts.getInwall() != 0)
			partsByType.put(PartType.INWALL_ANY, parts.getInwall());
		if (parts.getOutwall() != null)
			partsByType.put(PartType.OUTWALL, parts.getOutwall());
		if (parts.getRoof() != null)
			partsByType.put(PartType.ROOF, parts.getRoof());
	}

	// All methods for DataManager call are just to ensure integrity
	// if called from housing land templates, because it only has id and isDefault
	// for the buildings. Buildings template has full info though, except isDefault
	// value for the land.

	public String getPartsMatchTag() {
		if (StringUtils.isNullOrEmpty(partsMatch))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getPartsMatchTag();
		return partsMatch;
	}

	public String getSize() {
		if (StringUtils.isNullOrEmpty(size))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getSize();
		return size;
	}

	public BuildingType getType() {
		if (type == null)
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getType();
		return type;
	}

	public int getId() {
		return id;
	}

	public Integer getDefaultPartId(PartType partType) {
		return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).partsByType.get(partType);
	}

}
