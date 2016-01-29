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

import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.configs.main.HousingConfig;
import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author Rolandas
 */
public class CM_HOUSE_OPEN_DOOR extends AionClientPacket {

	int address;
	boolean leave = false;

	public CM_HOUSE_OPEN_DOOR(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		address = readD();
		if (readC() != 0)
			leave = true;
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null)
			return;

		if (player.getAccessLevel() >= 3 && HousingConfig.ENABLE_SHOW_HOUSE_DOORID) {
			PacketSendUtility.sendMessage(player, "House door id: " + address);
		}

		House house = HousingService.getInstance().getHouseByAddress(address);
		if (house == null)
			return;

		if (leave) {
			if (house.getAddress().getExitMapId() != null) {
				TeleportService2.teleportTo(player, house.getAddress().getExitMapId(), house.getAddress().getExitX(),
					house.getAddress().getExitY(), house.getAddress().getExitZ(), (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			}
			else {
				if (GeoDataConfig.GEO_ENABLE) {
					Npc sign = house.getCurrentSign();
					byte flags = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
					Vector3f colSign = GeoService.getInstance().getClosestCollision(sign, player.getX(), player.getY(), player.getZ() + 2, false, flags);
					Vector3f colWall = GeoService.getInstance().getClosestCollision(player, colSign.getX(), colSign.getY(), colSign.getZ(), true, flags);
					double radian = Math.toRadians(MathUtil.calculateAngleFrom(player.getX(), player.getY(), colWall.x, colWall.y));
					float x = (float) (Math.cos(radian) * 0.1);
					float y = (float) (Math.sin(radian) * 0.1);
					TeleportService2.teleportTo(player, house.getWorldId(), colWall.getX() + x, colWall.getY() + y, player.getZ(), (byte) 0,
						TeleportAnimation.BEAM_ANIMATION);
				}
				else {
					double radian = Math.toRadians(MathUtil.convertHeadingToDegree(player.getHeading()));
					float x = (float) (Math.cos(radian) * 6);
					float y = (float) (Math.sin(radian) * 6);
					TeleportService2.teleportTo(player, house.getWorldId(), player.getX() + x, player.getY() + y, player.getZ(), (byte) 0,
						TeleportAnimation.BEAM_ANIMATION);
				}
			}
		}
		else {
			if (house.getOwnerId() != player.getObjectId()) {
				int permission = house.getSettingFlags() >> 8;
				boolean allowed = false;
				if (permission == 2) {
					allowed = player.getFriendList().getFriend(house.getOwnerId()) != null
						|| (player.getLegion() != null && player.getLegion().isMember(house.getOwnerId()));
				}
				if (!allowed) {
					if (player.getAccessLevel() < HousingConfig.ENTER_HOUSE_ACCESSLEVEL) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_ENTER_NO_RIGHT2);
						return;
					}
				}
			}
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(player.getHeading()));
			float x = (float) (Math.cos(radian) * 6);
			float y = (float) (Math.sin(radian) * 6);
			TeleportService2.teleportTo(player, house.getWorldId(), player.getX() + x, player.getY() + y, house.getAddress().getZ(), (byte) 0,
				TeleportAnimation.BEAM_ANIMATION);
		}
	}

}
