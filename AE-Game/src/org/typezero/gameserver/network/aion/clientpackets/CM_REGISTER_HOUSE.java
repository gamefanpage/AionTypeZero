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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.configs.main.HousingConfig;
import org.typezero.gameserver.controllers.HouseController;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.HousingBidService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class CM_REGISTER_HOUSE extends AionClientPacket {

	long bidKinah;
	long unk1;

	public CM_REGISTER_HOUSE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		bidKinah = readQ();
		unk1 = readQ(); // 100000
	}

	@Override
	protected void runImpl() {
		if (!HousingConfig.ENABLE_HOUSE_AUCTIONS)
			return;

		Player player = getConnection().getActivePlayer();
		House house = player.getActiveHouse();
		if (house == null || house.getHouseType() == HouseType.STUDIO)
			return; // should not happen
		
		if (house.getStatus() == HouseStatus.SELL_WAIT) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_FAIL_ALREADY_REGISTED);
			return;
		}

		if (!HousingBidService.getInstance().isRegisteringAllowed()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_AUCTION_TIMEOUT);
			return;
		}

		if (!house.isFeePaid() && HousingConfig.ENABLE_HOUSE_PAY) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_AUCTION_OVERDUE);
			return;
		}

		long fee = (long) (bidKinah * 0.3f);

		if (player.getInventory().getKinah() < fee) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		player.getInventory().decreaseKinah(fee);
		HousingBidService.getInstance().addHouseToAuction(house, bidKinah);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_MY_HOUSE(house.getAddress().getId()));
		((HouseController) house.getController()).updateAppearance();

		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, house));
	}

}
