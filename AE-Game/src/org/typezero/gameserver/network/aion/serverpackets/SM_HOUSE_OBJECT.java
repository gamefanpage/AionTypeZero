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

package org.typezero.gameserver.network.aion.serverpackets;

import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.NpcObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Rolandas
 */
public class SM_HOUSE_OBJECT extends AionServerPacket {

	HouseObject<?> houseObject;

	public SM_HOUSE_OBJECT(HouseObject<?> owner) {
		this.houseObject = owner;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		if (player == null)
			return;

		House house = houseObject.getOwnerHouse();
		int templateId = houseObject.getObjectTemplate().getTemplateId();

		writeD(house.getAddress().getId()); // if painted 0 ?
		writeD(house.getOwnerId()); // player which owns house
		writeD(houseObject.getObjectId()); // <outlet[X]> data in house scripts
		writeD(houseObject.getObjectId()); // <outDB[X]> data in house scripts (probably DB id), where [X] is number
		
		writeD(templateId);
		writeF(houseObject.getX());
		writeF(houseObject.getY());
		writeF(houseObject.getZ());
		writeH(houseObject.getRotation());

		writeD(player.getHouseObjectCooldownList().getReuseDelay(houseObject.getObjectId()));
		if (houseObject.getUseSecondsLeft() > 0)
			writeD(houseObject.getUseSecondsLeft());
		else
			writeD(0);
		
		Integer color = null;
		if (houseObject != null)
			color = houseObject.getColor();
			
		if (color != null && color > 0 ) {
			writeC(1); // Is dyed (True)
			writeC((color & 0xFF0000) >> 16);
			writeC((color & 0xFF00) >> 8);
			writeC((color & 0xFF));
		}
		else {
			writeC(0); // Is dyed (False)
 			writeC(0);
 			writeC(0);
 			writeC(0);
 		}
		writeD(0); // expiration as for armor ?

		byte typeId = houseObject.getObjectTemplate().getTypeId();
		writeC(typeId);

		switch (typeId) {
			case 1: // Use item
				((UseableItemObject) houseObject).writeUsageData(getBuf());
				break;
			case 7: // Npc type
				NpcObject npcObj = (NpcObject) houseObject;
				writeD(npcObj.getNpcObjectId());
			default:
				break;
		}
	}

}
