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

import org.typezero.gameserver.model.gameobjects.HouseDecoration;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Rolandas
 */
public class SM_HOUSE_EDIT extends AionServerPacket {

	private int action;
	private int storeId;
	private int itemObjectId;
	private float x, y, z;
	private int rotation;

	public SM_HOUSE_EDIT(int action) {
		this.action = action;
	}

	public SM_HOUSE_EDIT(int action, int storeId, int itemObjectId) {
		this(action);
		this.itemObjectId = itemObjectId;
		this.storeId = storeId;
	}

	public SM_HOUSE_EDIT(int action, int itemObjectId, float x, float y, float z, int rotation) {
		this.action = action;
		this.itemObjectId = itemObjectId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		if (player == null || player.getHouseRegistry() == null)
			return;

		HouseObject<?> obj = player.getHouseRegistry().getObjectByObjId(itemObjectId);

		if (action == 3) { // Add item
			int templateId = 0;
			int typeId = 0;
			if (obj == null) {
				HouseDecoration deco = player.getHouseRegistry().getCustomPartByObjId(itemObjectId);
				templateId = deco.getTemplate().getId();
			}
			else {
				templateId = obj.getObjectTemplate().getTemplateId();
				typeId = obj.getObjectTemplate().getTypeId();
			}
			writeC(action);
			writeC(storeId);
			writeD(itemObjectId);
			writeD(templateId);
			if (obj != null && obj.getUseSecondsLeft() > 0)
				writeD(obj.getUseSecondsLeft());
			else
				writeD(0);

			Integer color = null;
			if (obj != null)
				color = obj.getColor();
		
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

			writeC(typeId);
			// Additional info about the usage
			if (obj != null && obj instanceof UseableItemObject) {
				writeD(player.getObjectId());
				((UseableItemObject) obj).writeUsageData(getBuf());
			}
		}
		else if (action == 4) { // Remove from inventory
			writeC(action);
			writeC(storeId);
			writeD(itemObjectId);
		}
		else if (action == 5) { // Spawn or move object
			writeC(action);
			writeD(player.getHouseOwnerId()); // if painted 0 ?
			writeD(player.getCommonData().getPlayerObjId());
			writeD(itemObjectId);
			writeD(obj.getObjectTemplate().getTemplateId());
			writeF(x);
			writeF(y);
			writeF(z);
			writeH(rotation);
			writeD(player.getHouseObjectCooldownList().getReuseDelay(itemObjectId));
			if (obj.getUseSecondsLeft() > 0)
				writeD(obj.getUseSecondsLeft());
			else
				writeD(0);

			Integer color = obj.getColor();
			writeC(color == null ? 0 : 1); // Is dyed
			if (color == null) {
				writeC(0);
				writeC(0);
				writeC(0);
			}
			else {
				writeC((color & 0xFF0000) >> 16);
				writeC((color & 0xFF00) >> 8);
				writeC((color & 0xFF));
			}
			writeD(0); // expiration as for armor ?

			writeC(obj.getObjectTemplate().getTypeId());

			if (obj instanceof UseableItemObject) {
				((UseableItemObject) obj).writeUsageData(getBuf());
			}
		}
		else if (action == 7) { // Despawn object
			writeC(action);
			writeD(itemObjectId);
		}
		else
			writeC(action);
	}

}
