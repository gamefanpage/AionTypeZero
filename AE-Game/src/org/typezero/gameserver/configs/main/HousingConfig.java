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

package org.typezero.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author Rolandas
 */
public class HousingConfig {

	/**
	 * Distance Visibility
	 */
	@Property(key = "gameserver.housing.visibility.distance", defaultValue = "200")
	public static float VISIBILITY_DISTANCE = 200f;

	/**
	 * Show house door editor id
	 */
	@Property(key = "gameserver.housedoor.showid", defaultValue = "true")
	public static boolean ENABLE_SHOW_HOUSE_DOORID;

	@Property(key = "gameserver.housedoor.accesslevel", defaultValue = "3")
	public static int ENTER_HOUSE_ACCESSLEVEL;

	@Property(key = "gameserver.housing.auction.enable", defaultValue = "false")
	public static boolean ENABLE_HOUSE_AUCTIONS;

	@Property(key = "gameserver.housing.pay.enable", defaultValue = "false")
	public static boolean ENABLE_HOUSE_PAY;

	@Property(key = "gameserver.housing.auction.time", defaultValue = "0 5 12 ? * SUN")
	public static String HOUSE_AUCTION_TIME;

	@Property(key = "gameserver.housing.auction.registerend", defaultValue = "0 0 0 ? * SAT")
	public static String HOUSE_REGISTER_END;

	@Property(key = "gameserver.housing.maintain.time", defaultValue = "0 0 0 ? * MON")
	public static String HOUSE_MAINTENANCE_TIME;

	/** Auction default bid prices **/
	@Property(key = "gameserver.housing.auction.default_bid.house", defaultValue = "12000000")
	public static int HOUSE_MIN_BID;
	@Property(key = "gameserver.housing.auction.default_bid.mansion", defaultValue = "112000000")
	public static int MANSION_MIN_BID;
	@Property(key = "gameserver.housing.auction.default_bid.estate", defaultValue = "335000000")
	public static int ESTATE_MIN_BID;
	@Property(key = "gameserver.housing.auction.default_bid.palace", defaultValue = "1000000000")
	public static int PALACE_MIN_BID;

	/** Auction minimal level required for bidding **/
	@Property(key = "gameserver.housing.auction.bidding.min_level.house", defaultValue = "21")
	public static int HOUSE_MIN_BID_LEVEL;
	@Property(key = "gameserver.housing.auction.bidding.min_level.mansion", defaultValue = "30")
	public static int MANSION_MIN_BID_LEVEL;
	@Property(key = "gameserver.housing.auction.bidding.min_level.estate", defaultValue = "40")
	public static int ESTATE_MIN_BID_LEVEL;
	@Property(key = "gameserver.housing.auction.bidding.min_level.palace", defaultValue = "50")
	public static int PALACE_MIN_BID_LEVEL;

	@Property(key = "gameserver.housing.auction.default_refund", defaultValue = "0.3f")
	public static float BID_REFUND_PERCENT;

	@Property(key = "gameserver.housing.auction.steplimit", defaultValue = "100")
	public static float HOUSE_AUCTION_BID_LIMIT;

	@Property(key = "gameserver.housing.scripts.debug", defaultValue = "false")
	public static boolean HOUSE_SCRIPT_DEBUG;

	@Property(key = "gameserver.housing.auction.fill.auto", defaultValue = "false")
	public static boolean FILL_HOUSE_BIDS_AUTO;
	@Property(key = "gameserver.housing.auction.fill.auto.houses", defaultValue = "20")
	public static int FILL_AUTO_HOUSES_COUNT;
	@Property(key = "gameserver.housing.auction.fill.auto.mansion", defaultValue = "10")
	public static int FILL_AUTO_MANSION_COUNT;
	@Property(key = "gameserver.housing.auction.fill.auto.estate", defaultValue = "5")
	public static int FILL_AUTO_ESTATE_COUNT;
	@Property(key = "gameserver.housing.auction.fill.auto.palace", defaultValue = "1")
	public static int FILL_AUTO_PALACE_COUNT;
}
