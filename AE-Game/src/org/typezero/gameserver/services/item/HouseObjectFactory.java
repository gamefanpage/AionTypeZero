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

package org.typezero.gameserver.services.item;

import org.apache.commons.lang.IncompleteArgumentException;
import org.joda.time.DateTime;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.ChairObject;
import org.typezero.gameserver.model.gameobjects.EmblemObject;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.JukeBoxObject;
import org.typezero.gameserver.model.gameobjects.MoveableObject;
import org.typezero.gameserver.model.gameobjects.NpcObject;
import org.typezero.gameserver.model.gameobjects.PassiveObject;
import org.typezero.gameserver.model.gameobjects.PictureObject;
import org.typezero.gameserver.model.gameobjects.PostboxObject;
import org.typezero.gameserver.model.gameobjects.StorageObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.*;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.actions.SummonHouseObjectAction;
import org.typezero.gameserver.utils.idfactory.IDFactory;

/**
 * @author Rolandas
 */
public final class HouseObjectFactory {

	/**
	 * For loading data from DB
	 */
	public static HouseObject<?> createNew(House house, int objectId, int objectTemplateId) {
		PlaceableHouseObject template = DataManager.HOUSING_OBJECT_DATA.getTemplateById(objectTemplateId);
		if (template instanceof HousingChair)
			return new ChairObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingJukeBox)
			return new JukeBoxObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingMoveableItem)
			return new MoveableObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingNpc)
			return new NpcObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingPicture)
			return new PictureObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingPostbox)
			return new PostboxObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingStorage)
			return new StorageObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingUseableItem)
			return new UseableItemObject(house, objectId, template.getTemplateId());
		else if (template instanceof HousingEmblem)
			return new EmblemObject(house, objectId, template.getTemplateId());
		return new PassiveObject(house, objectId, template.getTemplateId());
	}

	/**
	 * For transferring item from inventory to house registry
	 */
	public static HouseObject<?> createNew(House house, ItemTemplate itemTemplate) {
		if (itemTemplate.getActions() == null)
			throw new IncompleteArgumentException("template actions null");
		SummonHouseObjectAction action = itemTemplate.getActions().getHouseObjectAction();
		if (action == null)
			throw new IncompleteArgumentException("template actions miss SummonHouseObjectAction");

		int objectTemplateId = action.getTemplateId();
		HouseObject<?> obj = createNew(house, IDFactory.getInstance().nextId() , objectTemplateId);
		if (obj.getObjectTemplate().getUseDays() > 0) {
			int expireEnd = (int) (DateTime.now().plusDays(obj.getObjectTemplate().getUseDays()).getMillis() / 1000);
			obj.setExpireTime(expireEnd);
		}
		return obj;
	}
}
