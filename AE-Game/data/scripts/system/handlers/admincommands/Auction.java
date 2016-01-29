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

package admincommands;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseBidEntry;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.services.HousingBidService;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.zone.ZoneName;
import java.util.ArrayList;
import java.util.List;
import javolution.util.FastList;

/**
 * @author Rolandas
 * @modified Luzien
 */
public class Auction extends AdminCommand {

	public Auction() {
		super("auction");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			onFail(admin, null);
			return;
		}

		if ("remove".equals(params[0])) {
			if (params.length < 2) {
				onFail(admin, null);
				return;
			}
			String param = params[1].toUpperCase();
			List<House> housesToRemove = new ArrayList<House>();

			if ("HOUSE".equals(param.split("_")[0])) {
				House house = HousingService.getInstance().getHouseByName(params[1].toUpperCase());
				if (house == null || house.getStatus() != HouseStatus.SELL_WAIT) {
					PacketSendUtility.sendMessage(admin, "No such house!");
				}
				housesToRemove.add(house);
			} else {
				ZoneName zoneName = ZoneName.get(params[1]);
				if (zoneName.name().equals(ZoneName.NONE)) {
					PacketSendUtility.sendMessage(admin, "No such zone!");
					return;
				}
				for (House house : HousingService.getInstance().getCustomHouses()) {
					if (house.getStatus() != HouseStatus.SELL_WAIT) {
						continue;
					}
					float x = house.getX();
					float y = house.getY();
					float z = house.getZ();
					if (house.getPosition().getMapRegion().isInsideZone(zoneName, x, y, z)) {
						housesToRemove.add(house);
					}
				}
			}

			if (housesToRemove.size() == 0) {
				PacketSendUtility.sendMessage(admin, "Nothing to remove!");
				return;
			}

			boolean noSale = false;
			if (params.length == 3) {
				if (!"nosale".equals(params[2])) {
					onFail(admin, null);
					return;
				}
				noSale = true;
			}

			for (House house : housesToRemove) {
				if (HousingBidService.getInstance().removeHouseFromAuction(house, noSale)) {
					PacketSendUtility.sendMessage(admin, "Succesfully removed house " + house.getName());
				} else {
					PacketSendUtility.sendMessage(admin, "Failed to remove house " + house.getName());
				}
			}
		} else if ("add".equals(params[0])) {

			if (params.length < 3 || params.length > 4) {
				onFail(admin, null);
				return;
			}

			ZoneName zoneName = ZoneName.get(params[1]);
			if (zoneName.name().equals(ZoneName.NONE)) {
				PacketSendUtility.sendMessage(admin, "No such zone!");
				return;
			}

			HouseType houseType = null;
			try {
				houseType = HouseType.fromValue(params[2].toUpperCase());
			} catch (Exception e) {
			}

			if (houseType == null) {
				PacketSendUtility.sendMessage(admin, "No such house type!");
				return;
			}

			long bidPrice = 0;
			if (params.length == 4) {
				try {
					bidPrice = Long.parseLong(params[3]);
					if (bidPrice <= 0) {
						throw new IllegalArgumentException();
					}
				} catch (Exception e) {
					PacketSendUtility.sendMessage(admin, "Only positive numbers for the bid price!");
					return;
				}
			}

			boolean found = false;
			int counter = 0;

			for (House house : HousingService.getInstance().getCustomHouses()) {
				if (house.getOwnerId() != 0 || house.getHouseType() != houseType) {
					continue;
				}
				if (house.getStatus() == HouseStatus.INACTIVE) {
					continue;
				}
				if (house.getStatus() == HouseStatus.SELL_WAIT) {
					// check to see if the bid entry exists
					HouseBidEntry entry = HousingBidService.getInstance().getHouseBid(house.getObjectId());
					if (entry == null) {
						// reset status
						house.setStatus(HouseStatus.ACTIVE);
					} else {
						continue;
					}
				}
				float x = house.getX();
				float y = house.getY();
				float z = house.getZ();
				if (house.getPosition().getMapRegion().isInsideZone(zoneName, x, y, z)) {
					found = true;
					long price = bidPrice > 0 ? bidPrice : house.getDefaultAuctionPrice();
					if (HousingBidService.getInstance().addHouseToAuction(house, price)) {
						house.save();
						counter++;
					}
				}
			}

			if (found) {
				PacketSendUtility.sendMessage(admin, "Added " + counter + " houses of type " + houseType);
			} else {
				PacketSendUtility.sendMessage(admin, "No houses, all are occupied or already in auction!");
			}
		} else if ("addrandom".equals(params[0])) {
			if (params.length < 4 || params.length > 5) {
				onFail(admin, null);
				return;
			}

			String param = params[1].toUpperCase();
			Race race;
			if ("ALL".equals(param) || "PC_ALL".equals(param))
				race = Race.PC_ALL;
			else if ("ELYOS".equals(param))
				race = Race.ELYOS;
			else if ("ASMODIANS".equals(param))
				race = Race.ASMODIANS;
			else {
				PacketSendUtility.sendMessage(admin, "Race not found! Use ALL | ELYOS | ASMODIANS!");
				return;
			}


			HouseType houseType = null;
			try {
				houseType = HouseType.fromValue(params[2].toUpperCase());
			} catch (Exception e) {
			}

			if (houseType == null) {
				PacketSendUtility.sendMessage(admin, "No such house type!");
				return;
			}


			int count = 0;
			try {
				count = Integer.parseInt(params[3]);
				if (count <= 0) {
					throw new IllegalArgumentException();
				}
			} catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Invalid count. Only positive numbers!");
				return;
			}
			long bidPrice = 0;
			if (params.length == 5) {
				try {
					bidPrice = Long.parseLong(params[4]);
					if (bidPrice <= 0) {
						throw new IllegalArgumentException();
					}
				} catch (Exception e) {
					PacketSendUtility.sendMessage(admin, "Only positive numbers for the bid price!");
					return;
				}
			}

			int counter = 0;
			FastList<House> houses = HousingService.getInstance().getCustomHouses();
			while (!houses.isEmpty() && counter < count) {
				House house = houses.get(Rnd.get(houses.size()));
				houses.remove(house);
				if (house.getOwnerId() != 0 || house.getHouseType() != houseType) {
					continue;
				}
				if (race != Race.PC_ALL) {
					int mapId = house.getAddress().getMapId();
					if (race.equals(Race.ELYOS)) {
						if (mapId != 210050000 && mapId != 700010000 && mapId != 210040000) {
							continue;
						}
					}
					else if (race.equals(Race.ASMODIANS)) {
						if (mapId != 710010000 && mapId != 220040000 && mapId != 220070000) {
							continue;
						}
					}
				}
				if (house.getStatus() == HouseStatus.INACTIVE) {
					continue;
				}
				if (house.getStatus() == HouseStatus.SELL_WAIT) {
					// check to see if the bid entry exists
					HouseBidEntry entry = HousingBidService.getInstance().getHouseBid(house.getObjectId());
					if (entry == null) {
						// reset status
						house.setStatus(HouseStatus.ACTIVE);
					} else {
						continue;
					}
				}

				long price = bidPrice > 0 ? bidPrice : house.getDefaultAuctionPrice();
				if (HousingBidService.getInstance().addHouseToAuction(house, price)) {
					house.save();
					counter++;
				}
			}

			if (counter > 0) {
				PacketSendUtility.sendMessage(admin, "Added " + counter + " houses of type " + houseType);
			} else {
				PacketSendUtility.sendMessage(admin, "No houses, all are occupied or already in auction!");
			}

		} else {
			onFail(admin, null);
		}

	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax:\n"
						+ " //auction add <zone_name> <house_type> [initial_bid]\n"
						+ " //auction remove <HOUSE_id|zone_name> [nosale]\n"
						+ " //auction addrandom <race> <house_type> <count> [initial_bid]\n"
						+ "   zone_name = from zones xml files\n"
						+ "   house_type = house, mansion, estate, palace\n"
						+ "   initial_bid = initial bid price (if omitted, default is used)");
	}
}
