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

import java.util.List;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseBidEntry;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.HousingBidService;

/**
 * @author Rolandas
 */
public class SM_HOUSE_BIDS extends AionServerPacket {

	private boolean isFirst;
	private boolean isLast;
	private HouseBidEntry playerBid;
	private List<HouseBidEntry> houseBids;

	public SM_HOUSE_BIDS(boolean isFirstPacket, boolean isLastPacket, HouseBidEntry playerBid, List<HouseBidEntry> houseBids) {
		isFirst = isFirstPacket;
		isLast = isLastPacket;
		this.playerBid = playerBid;
		this.houseBids = houseBids;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		int secondsTillAuction = HousingBidService.getInstance().getSecondsTillAuction();

		writeC(isFirst ? 1 : 0);
		writeC(isLast ? 1 : 0);

		if (playerBid == null) {
			writeD(0);
			writeQ(0);
		}
		else {
			writeD(playerBid.getEntryIndex());
			writeQ(playerBid.getBidPrice());
		}

		List<House> playerHouses = player.getHouses();
		House sellHouse = null;
		for (House house : playerHouses) {
			if (house.getStatus() == HouseStatus.SELL_WAIT) {
				sellHouse = house;
				break;
			}
		}

		HouseBidEntry sellData = null;
		if (sellHouse != null) {
			sellData = HousingBidService.getInstance().getHouseBid(sellHouse.getObjectId());
			writeD(sellData.getEntryIndex());
			writeQ(sellData.getBidPrice());
		}
		else {
			writeD(0);
			writeQ(0);
		}

		writeH(houseBids.size());
		for (int n = 0; n < houseBids.size(); n++) {
			HouseBidEntry entry = houseBids.get(n);
			writeD(entry.getEntryIndex());
			writeD(entry.getLandId());
			writeD(entry.getAddress());
			writeD(entry.getBuildingId());
			if (sellData != null && entry.getEntryIndex() == sellData.getEntryIndex())
				writeD(0);
			else if (HousingBidService.canBidHouse(player, entry.getMapId(), entry.getLandId()))
				writeD(entry.getHouseType().getId());
			else
				writeD(0);
			writeQ(entry.getBidPrice());
			writeQ(entry.getUnk2());
			writeD(entry.getBidCount());
			writeD(secondsTillAuction);
		}
	}

}
