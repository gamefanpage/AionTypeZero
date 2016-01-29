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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Friend;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.templates.housing.HouseAddress;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * Packet for telporting by using relationship crystal
 * @author Rolandas
 */
public class CM_HOUSE_TELEPORT extends AionClientPacket {

	int actionId;
	int playerId1;
	int playerId2;

	public CM_HOUSE_TELEPORT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readC();
		playerId1 = readD();
		playerId2 = readD();
	}

	@Override
	protected void runImpl() {
		Player player1 = World.getInstance().findPlayer(playerId1);
		if (player1 == null || !player1.isOnline())
			return;

		House house = null;
		if (actionId == 1) {
			playerId2 = playerId1;
		}
		else if (actionId == 3) {
			List<Integer> relationIds = new ArrayList<Integer>();
			Iterator<Friend> friends = player1.getFriendList().iterator();
			int address = 0;

			while (friends.hasNext()) {
				int friendId = friends.next().getOid();
				address = HousingService.getInstance().getPlayerAddress(friendId);
				if (address != 0) {
					house = HousingService.getInstance().getPlayerStudio(friendId);
					if (house == null)
						house = HousingService.getInstance().getHouseByAddress(address);
					if ((house.getSettingFlags() >> 8) == 3 || house.getLevelRestrict() > player1.getLevel())
						continue; // closed doors | level restrict
					relationIds.add(friendId);
				}
			}
			Legion legion = player1.getLegion();
			if (legion != null) {
				for (int memberId : legion.getLegionMembers()) {
					address = HousingService.getInstance().getPlayerAddress(memberId);
					if (address != 0) {
						house = HousingService.getInstance().getPlayerStudio(memberId);
						if (house == null)
							house = HousingService.getInstance().getHouseByAddress(address);
						if ((house.getSettingFlags() >> 8) == 3 || house.getLevelRestrict() > player1.getLevel())
							continue; // closed doors | level restrict
						relationIds.add(memberId);
					}
				}
			}
			if (relationIds.size() == 0) {
				PacketSendUtility.sendPacket(player1, SM_SYSTEM_MESSAGE.STR_MSG_NO_RELATIONSHIP_RECENTLY);
				return;
			}
			playerId2 = relationIds.get(Rnd.get(relationIds.size()));
		}

		if (playerId2 == 0)
			return;

		house = HousingService.getInstance().getPlayerStudio(playerId2);
		HouseAddress address = null;
		int instanceId = 0;
		if (house != null) {
			address = house.getAddress();
			WorldMapInstance instance = InstanceService.getPersonalInstance(address.getMapId(), playerId2);
			if (instance == null) {
				instance = InstanceService.getNextAvailableInstance(address.getMapId(), playerId2);
			}
			instanceId = instance.getInstanceId();
			InstanceService.registerPlayerWithInstance(instance, player1);
		}
		else {
			int addressId = HousingService.getInstance().getPlayerAddress(playerId2);
			house = HousingService.getInstance().getHouseByAddress(addressId);
			if (house == null || house.getLevelRestrict() > player1.getLevel())
				return;
			address = house.getAddress();
			instanceId = house.getInstanceId();
		}
		VisibleObject target = player1.getTarget();
		if (target != null) {
			PacketSendUtility.sendPacket(player1, new SM_DIALOG_WINDOW(target.getObjectId(), 0));
		}
		TeleportService2
			.teleportTo(player1, address.getMapId(), instanceId, address.getX(), address.getY(), address.getZ(), (byte) 0, TeleportAnimation.BEAM_ANIMATION);
	}

}
