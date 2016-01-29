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

import java.sql.Timestamp;

import org.typezero.gameserver.model.gameobjects.player.HousingFlags;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.MaintenanceTask;
import org.typezero.gameserver.model.town.Town;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.TownService;
import org.joda.time.DateTime;

/**
 * @author Rolandas
 */
public class SM_HOUSE_OWNER_INFO extends AionServerPacket {

	private Player player;
	private House activeHouse;

	public SM_HOUSE_OWNER_INFO(Player player, House activeHouse) {
		this.player = player;
		this.activeHouse = activeHouse;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (activeHouse == null) {
			writeD(0);
			writeD(player.isInHousingStatus(HousingFlags.BUY_STUDIO_ALLOWED) ? 355000 : 0); // studio building id
		}
		else {
			writeD(activeHouse.getAddress().getId());
			writeD(activeHouse.getBuilding().getId());
		}
		writeC(player.getHousingStatus());
		int townLevel = 1;
		if (activeHouse != null && activeHouse.getAddress().getTownId() != 0) {
			Town town = TownService.getInstance().getTownById(activeHouse.getAddress().getTownId());
			townLevel = town.getLevel();
		}
		writeC(townLevel);
		// Maintenance bill weeks left ?, if 0 maintenance date is in red
		if (activeHouse == null || !activeHouse.isFeePaid()) {
			writeC(0);
		}
		else {
			Timestamp nextPay = activeHouse.getNextPay();
			float diff;
			if (nextPay == null) {
				// See MaintenanceTask.updateMaintainedHouses()
				// all just obtained houses have fee paid true and time is null;
				// means they should pay next week
				diff = MaintenanceTask.getInstance().getPeriod();
			}
			else {
				long paytime = activeHouse.getNextPay().getTime();
				diff = paytime - ((long) MaintenanceTask.getInstance().getRunTime() * 1000);
			}
			if (diff < 0) {
				writeC(0);
			}
			else {
				int weeks = (int) (Math.round(diff / MaintenanceTask.getInstance().getPeriod()));
				if (DateTime.now().getDayOfWeek() != 7) // Hack for auction Day, client counts sunday to new week
					weeks++;
				writeC(weeks);
			}
		}
		writeH(0); // unk

		// Second house info ?
		writeD(0);
		writeD(0);
		writeC(0);
		writeC(0);
		writeC(0); // 3.5
		writeH(0); // 3.5 unk

	}
}
