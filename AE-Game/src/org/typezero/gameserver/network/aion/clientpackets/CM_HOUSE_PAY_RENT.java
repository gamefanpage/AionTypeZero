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

import java.sql.Timestamp;

import org.joda.time.DateTime;

import org.typezero.gameserver.configs.main.HousingConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.MaintenanceTask;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class CM_HOUSE_PAY_RENT extends AionClientPacket {

	int weekCount;

	public CM_HOUSE_PAY_RENT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		weekCount = readC();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		if (!HousingConfig.ENABLE_HOUSE_PAY) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_F2P_CASH_HOUSE_FEE_FREE);
			return;
		}

		House house = player.getActiveHouse();
		long toPay = house.getLand().getMaintenanceFee() * weekCount;
		if (toPay <= 0) {
			return;
		}
		if (player.getInventory().getKinah() < toPay) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		long payTime = house.getNextPay() != null ? house.getNextPay().getTime() : (long) MaintenanceTask.getInstance().getRunTime() * 1000;
		while ((--weekCount) >= 0) {
			payTime += MaintenanceTask.getInstance().getPeriod();
		}

		DateTime nextRun = new DateTime((long) MaintenanceTask.getInstance().getRunTime() * 1000);
		if (nextRun.plusWeeks(4).isBefore(payTime)) { //client cap
			return;
		}

		player.getInventory().decreaseKinah(toPay);
		house.setNextPay(new Timestamp(payTime));
		house.setFeePaid(true);
		house.save();
		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, house));
	}
}
