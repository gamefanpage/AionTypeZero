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
import org.typezero.gameserver.model.templates.housing.HousePart;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "houseParts" })
@XmlRootElement(name = "house_parts")
public class HousePartsData {

	@XmlElement(name = "house_part")
	protected List<HousePart> houseParts;

	@XmlTransient
	Map<String, List<HousePart>> partsByTags = new HashMap<String, List<HousePart>>(5);

	@XmlTransient
	Map<Integer, HousePart> partsById = new HashMap<Integer, HousePart>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (houseParts == null)
			return;

		for (HousePart part : houseParts) {
			partsById.put(part.getId(), part);
			Iterator<String> iterator = part.getTags().iterator();
			while (iterator.hasNext()) {
				String tag = iterator.next();
				List<HousePart> parts = partsByTags.get(tag);
				if (parts == null) {
					parts = new ArrayList<HousePart>();
					partsByTags.put(tag, parts);
				}
				parts.add(part);
			}
		}

		houseParts.clear();
		houseParts = null;
	}

	public HousePart getPartById(int partId) {
		return partsById.get(partId);
	}

	public List<HousePart> getPartsForBuilding(Building building) {
		return partsByTags.get(building.getPartsMatchTag());
	}

	public int size() {
		return partsById.size();
	}

}
