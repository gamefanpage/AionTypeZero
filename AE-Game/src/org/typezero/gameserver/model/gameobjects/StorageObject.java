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


package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.templates.housing.HousingStorage;
import org.typezero.gameserver.network.aion.serverpackets.SM_OBJECT_USE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public final class StorageObject extends HouseObject<HousingStorage> {

	public StorageObject(House owner, int objId, int templateId) {
		super(owner, objId, templateId);
	}

	@Override
	public void onUse(Player player) {
		if (player.getObjectId() != this.getOwnerHouse().getOwnerId()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_IS_ONLY_FOR_OWNER_VALID);
			return;
		}
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_USE(getObjectTemplate().getNameId()));
		PacketSendUtility.sendPacket(player, new SM_OBJECT_USE_UPDATE(player.getObjectId(), 0, 0, this));

		for (HouseObject<?> ho : getOwnerHouse().getRegistry().getSpawnedObjects()) {
			if (ho instanceof StorageObject) {
				int warehouseId = ((HousingStorage) ho.getObjectTemplate()).getWarehouseId() + StorageType.HOUSE_WH_MIN - 1;
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(warehouseId).getItemsWithKinah(),
					warehouseId, 0, true, player));
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, warehouseId, 0, false, player));
			}
		}
	}

	@Override
	public boolean canExpireNow() {
		// FIXME: if player is using mailbox, should not expire immediately
		return false;
	}
}
