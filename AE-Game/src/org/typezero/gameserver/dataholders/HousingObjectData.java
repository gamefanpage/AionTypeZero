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
import javax.xml.bind.annotation.*;

import org.typezero.gameserver.model.templates.housing.HousingChair;
import org.typezero.gameserver.model.templates.housing.HousingEmblem;
import org.typezero.gameserver.model.templates.housing.HousingJukeBox;
import org.typezero.gameserver.model.templates.housing.HousingMoveableItem;
import org.typezero.gameserver.model.templates.housing.HousingMovieJukeBox;
import org.typezero.gameserver.model.templates.housing.HousingNpc;
import org.typezero.gameserver.model.templates.housing.HousingPassiveItem;
import org.typezero.gameserver.model.templates.housing.HousingPicture;
import org.typezero.gameserver.model.templates.housing.HousingPostbox;
import org.typezero.gameserver.model.templates.housing.HousingStorage;
import org.typezero.gameserver.model.templates.housing.HousingUseableItem;
import org.typezero.gameserver.model.templates.housing.PlaceableHouseObject;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "housingObjects" })
@XmlRootElement(name = "housing_objects")
public class HousingObjectData {

	@XmlElements({
		@XmlElement(name = "postbox", type = HousingPostbox.class),
		@XmlElement(name = "use_item", type = HousingUseableItem.class),
		@XmlElement(name = "move_item", type = HousingMoveableItem.class),
		@XmlElement(name = "chair", type = HousingChair.class),
		@XmlElement(name = "picture", type = HousingPicture.class),
		@XmlElement(name = "passive", type = HousingPassiveItem.class),
		@XmlElement(name = "npc", type = HousingNpc.class),
		@XmlElement(name = "storage", type = HousingStorage.class),
		@XmlElement(name = "jukebox", type = HousingJukeBox.class),
		@XmlElement(name = "moviejukebox", type = HousingMovieJukeBox.class),
		@XmlElement(name = "emblem", type = HousingEmblem.class)})
	protected List<PlaceableHouseObject> housingObjects;

	@XmlTransient
	protected TIntObjectHashMap<PlaceableHouseObject> objectTemplatesById = new TIntObjectHashMap<PlaceableHouseObject>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (housingObjects == null)
			return;
		for (PlaceableHouseObject obj : housingObjects) {
			objectTemplatesById.put(obj.getTemplateId(), obj);
		}

		housingObjects.clear();
		housingObjects = null;
	}

	public int size() {
		return objectTemplatesById.size();
	}

	public PlaceableHouseObject getTemplateById(int templateId) {
		return objectTemplatesById.get(templateId);
	}

}
