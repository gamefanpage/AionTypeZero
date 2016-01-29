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

import java.sql.Timestamp;
import org.typezero.gameserver.controllers.HouseController;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.HousingFlags;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.templates.housing.BuildingType;
import org.typezero.gameserver.model.templates.housing.HouseAddress;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;

/**
 * @author Rolandas
 */
public class HouseCommand extends AdminCommand {

	public HouseCommand() {
		super("house");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length == 0) {
			PacketSendUtility.sendMessage(admin, "Syntax: //house <tp | acquire | revoke>");
			return;
		}

		if (params[0].equals("acquire")) {
			if (params.length == 1) {
				PacketSendUtility.sendMessage(admin, "Syntax: //house acquire <name>");
				return;
			}
			ChangeHouseOwner(admin, params[1].toUpperCase(), true);
		}
		else if (params[0].equals("revoke")) {
			if (params.length == 1) {
				PacketSendUtility.sendMessage(admin, "Syntax: //house revoke <name>");
				return;
			}
			ChangeHouseOwner(admin, params[1].toUpperCase(), false);
		}
		else if (params[0].equals("tp")) {
			if (params.length == 1) {
				PacketSendUtility.sendMessage(admin, "Syntax: //house tp <name>");
				return;
			}
			House house = HousingService.getInstance().getHouseByName(params[1].toUpperCase());
			if (house == null) {
				PacketSendUtility.sendMessage(admin, "No such house!");
				return;
			}
			HouseAddress address = house.getAddress();
			TeleportService2.teleportTo(admin, address.getMapId(), address.getX(), address.getY(), address.getZ());
		}

	}

	private void ChangeHouseOwner(Player admin, String houseName, boolean acquire) {
		Player target = null;
		VisibleObject creature = admin.getTarget();

		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}

		if (acquire) {
			if (target.getHouses().size() == 2) {
				PacketSendUtility.sendMessage(admin, "Player can not own more than 2 houses!");
				return;
			}
			House house = HousingService.getInstance().getHouseByName(houseName);
			if (house == null) {
				PacketSendUtility.sendMessage(admin, "No such house!");
				return;
			}
			if (target.getHouses().size() == 1) {
				House current = target.getHouses().get(0);
				current.revokeOwner();
				if (current.getBuilding().getType() == BuildingType.PERSONAL_INS) {
					target.getHouses().remove(current);
					PacketSendUtility.sendMessage(admin, "Deleted studio.");
				}
				else {
					current.setStatus(HouseStatus.ACTIVE);
					current.setFeePaid(true);
					current.setNextPay(null);
					current.save();
					PacketSendUtility.sendMessage(admin, current.getName() + " status is now " + current.getStatus().toString());
				}
			}
			house.setAcquiredTime(new Timestamp(System.currentTimeMillis()));
			house.setOwnerId(target.getCommonData().getPlayerObjId());
			house.setStatus(HouseStatus.ACTIVE);
			house.setFeePaid(true);
			house.setNextPay(null); // TODO: fix it
      house.reloadHouseRegistry();
			house.save();
			target.getHouses().add(house);
			target.setHouseRegistry(house.getRegistry());
			target.setHousingStatus(HousingFlags.HOUSE_OWNER.getId());
			PacketSendUtility.sendMessage(admin, "House " + house.getName() + " acquired");
			PacketSendUtility.sendPacket(target, new SM_HOUSE_OWNER_INFO(target, house));
			PacketSendUtility
				.sendPacket(target, new SM_HOUSE_ACQUIRE(target.getObjectId(), house.getAddress().getId(), true));
		}
		else {
			if (target.getHouses().size() == 0) {
				PacketSendUtility.sendMessage(admin, "Nothing to revoke!");
				return;
			}
			House revokedHouse = null;
			for (House house : target.getHouses()) {
				if (house.getName().equals(houseName)) {
					revokedHouse = house;
					house.revokeOwner();
				}
				else if (house.getStatus() != HouseStatus.ACTIVE) {
					house.setStatus(HouseStatus.ACTIVE);
					house.setSellStarted(null);
					house.save();
				}
			}
			if (revokedHouse == null) {
				PacketSendUtility.sendMessage(admin, "Target doesn't own this house!");
				return;
			}
			target.getHouses().remove(revokedHouse);
			House oldHouse = null;
			if (target.getHouses().size() != 0)
				oldHouse = target.getHouses().get(0);
			else
				target.setHousingStatus(HousingFlags.BUY_STUDIO_ALLOWED.getId());
			target.setHouseRegistry(oldHouse == null ? null : oldHouse.getRegistry());
			PacketSendUtility.sendMessage(admin, "House " + revokedHouse.getName() + " revoked");
			PacketSendUtility.sendPacket(target, new SM_HOUSE_OWNER_INFO(target, oldHouse));
			PacketSendUtility.sendPacket(target, new SM_HOUSE_ACQUIRE(target.getObjectId(),
				revokedHouse.getAddress().getId(), false));
			((HouseController) revokedHouse.getController()).updateAppearance();
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //house <tp | list | acquire | revoke>");
	}

}
