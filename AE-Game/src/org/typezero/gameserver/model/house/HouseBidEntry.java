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

package org.typezero.gameserver.model.house;

import org.typezero.gameserver.configs.main.HousingConfig;
import org.typezero.gameserver.model.templates.housing.HouseType;

/**
 * @author Rolandas
 */
public class HouseBidEntry implements Cloneable {

	private int entryIndex;
	private int landId;
	private int address;
	private int buildingId;
	private HouseType houseType;
	private long bidPrice;
	private final long unk2 = 100000;
	private int bidCount;
	private int mapId;
	private int lastBiddingPlayer;
	private long lastBidTime;

	public HouseBidEntry(House house, int index, long initialBid) {
		entryIndex = index;
		landId = house.getLand().getId();
		address = house.getAddress().getId();
		mapId = house.getAddress().getMapId();
		buildingId = house.getBuilding().getId();
		houseType = house.getHouseType();
		bidPrice = initialBid;
		lastBiddingPlayer = 0;
		lastBidTime = 0;
	}

	private HouseBidEntry() {
	}

	public int getEntryIndex() {
		return entryIndex;
	}

	public void setEntryIndex(int entryIndex) {
		this.entryIndex = entryIndex;
	}

	public int getLandId() {
		return landId;
	}

	public int getAddress() {
		return address;
	}

	public int getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}

	public long getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(long bidPrice) {
		this.bidPrice = bidPrice;
	}

	public int getBidCount() {
		return bidCount;
	}

	public void incrementBidCount() {
		this.bidCount++;
	}

	public final long getUnk2() {
		return unk2;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public int getMapId() {
		return mapId;
	}

	public int getLastBiddingPlayer() {
		return lastBiddingPlayer;
	}

	public void setLastBiddingPlayer(int lastBiddingPlayer) {
		this.lastBiddingPlayer = lastBiddingPlayer;
	}

	public long getRefundKinah() {
		return (long) (bidPrice * (float)HousingConfig.BID_REFUND_PERCENT);
	}

	public long getLastBidTime() {
		return lastBidTime;
	}

	public void setLastBidTime(long lastBidTime) {
		this.lastBidTime = lastBidTime;
	}

	public Object Clone() {
		HouseBidEntry cloned = new HouseBidEntry();
		cloned.address = this.address;
		cloned.bidCount = this.bidCount;
		cloned.bidPrice = this.bidPrice;
		cloned.buildingId = this.buildingId;
		cloned.entryIndex = this.entryIndex;
		cloned.houseType = this.houseType;
		cloned.landId = this.landId;
		cloned.mapId = this.mapId;
		cloned.lastBiddingPlayer = this.lastBiddingPlayer;
		return cloned;
	}

}
