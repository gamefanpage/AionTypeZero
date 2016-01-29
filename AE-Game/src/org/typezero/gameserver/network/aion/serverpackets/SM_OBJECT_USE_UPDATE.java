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
import org.typezero.gameserver.model.gameobjects.PostboxObject;
import org.typezero.gameserver.model.gameobjects.StorageObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.templates.housing.UseItemAction;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Rolandas
 */
public class SM_OBJECT_USE_UPDATE extends AionServerPacket {

	private int usingPlayerId;
	private int ownerPlayerId;
	private int useCount;
	private UseItemAction action = null;
	HouseObject<?> object;

	public SM_OBJECT_USE_UPDATE(int usingPlayerId, int ownerPlayerId, int useCount, HouseObject<?> object) {
		this.usingPlayerId = usingPlayerId;
		this.ownerPlayerId = ownerPlayerId;
		this.useCount = useCount;
		this.object = object;
		if (object instanceof UseableItemObject)
			this.action = ((UseableItemObject) object).getObjectTemplate().getAction();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(object.getObjectTemplate().getTypeId());
		if (object instanceof PostboxObject ||
				object instanceof StorageObject) {
			writeD(usingPlayerId);
			writeC(1); // unk
			writeD(object.getObjectId());
		}
		else if (object instanceof UseableItemObject) {
			writeD(usingPlayerId);
			writeD(ownerPlayerId);
			writeD(object.getObjectId());
			writeD(useCount);
			int checkType = 0;
			if (action != null && action.getCheckType() != null)
				checkType = action.getCheckType();
			writeC(checkType);
		}
	}

}
