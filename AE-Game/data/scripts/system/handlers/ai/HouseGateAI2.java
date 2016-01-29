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

package ai;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Request;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.BuildingType;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_TELEPORT;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author xTz, Rolandas
 */
@AIName("housegate")
public class HouseGateAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		final int creatorId = getCreatorId();
		// Only group member and creator may use gate
		if (!player.getObjectId().equals(creatorId)) {
			if (player.getCurrentGroup() == null || !player.getCurrentGroup().hasMember(creatorId))
				return;
		}

		House house = HousingService.getInstance().getPlayerStudio(creatorId);
		if (house == null) {
			int address = HousingService.getInstance().getPlayerAddress(creatorId);
			house = HousingService.getInstance().getHouseByAddress(address);
		}
		// Uses skill but doesn't have house
		if (house == null)
			return;

		if (house.getLevelRestrict() > player.getLevel())
		   //msg
		   return;

		AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, 0, 9, new AI2Request() {
			private boolean decided = false;

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (decided)
					return;

				House house = HousingService.getInstance().getPlayerStudio(creatorId);
				if (house == null) {
					int address = HousingService.getInstance().getPlayerAddress(creatorId);
					house = HousingService.getInstance().getHouseByAddress(address);
				}
				int instanceOwnerId = responder.getPosition().getWorldMapInstance().getOwnerId();

				int exitMapId = 0;
				float x = 0, y = 0, z = 0;
				byte heading = 0;
				int instanceId = 0;

				if (instanceOwnerId > 0) { // leaving
					house = HousingService.getInstance().getPlayerStudio(instanceOwnerId);
					exitMapId = house.getAddress().getExitMapId();
					instanceId = World.getInstance().getWorldMap(exitMapId).getWorldMapInstance().getInstanceId();
					x = house.getAddress().getExitX();
					y = house.getAddress().getExitY();
					z = house.getAddress().getExitZ();
				}
				else { // entering house
					exitMapId = house.getAddress().getMapId();
					if (house.getBuilding().getType() == BuildingType.PERSONAL_INS) { // entering studio
						WorldMapInstance instance = InstanceService.getPersonalInstance(exitMapId, creatorId);
						if (instance == null) {
							instance = InstanceService.getNextAvailableInstance(exitMapId, creatorId);
							InstanceService.registerPlayerWithInstance(instance, responder);
						}
						instanceId = instance.getInstanceId();
					}
					else { // entering ordinary house
						instanceId = house.getInstanceId();
					}
					x = house.getAddress().getX();
					y = house.getAddress().getY();
					z = house.getAddress().getZ();
					if (exitMapId == 710010000) {
						heading = 36;
					}
				}
				boolean canRecall = true;
				for (ZoneInstance zone : responder.getPosition().getMapRegion().getZones(responder)) {
					if (!zone.canRecall()) {
						canRecall = false;
						break;
					}
				}
				if (!canRecall) {
					responder.setBattleReturnCoords(0, null);
				}
				else {
					PacketSendUtility.sendPacket(responder, new SM_HOUSE_TELEPORT(house.getAddress().getId(), responder.getObjectId()));
					responder.setBattleReturnCoords(responder.getWorldId(), new float[] {responder.getX(), responder.getY(), responder.getZ()});
				}
				TeleportService2.teleportTo(responder, exitMapId, instanceId, x, y, z, heading,
					TeleportAnimation.JUMP_AIMATION_3);
				decided = true;
			}


			@Override
      public void denyRequest(Creature requester, Player responder) {
        decided = true;
			}

		});

	}

}
