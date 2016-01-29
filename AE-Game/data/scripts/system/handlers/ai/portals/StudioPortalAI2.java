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

package ai.portals;

import ai.ActionItemNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Rolandas
 */
@AIName("studioportal")
public class StudioPortalAI2 extends ActionItemNpcAI2 {

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		return true;
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		int ownerId = player.getPosition().getWorldMapInstance().getOwnerId();
		House studio = HousingService.getInstance().getPlayerStudio(player.getObjectId());
		if (studio == null && ownerId == 0) // Doesn't own studio and not in studio
		{
			PacketSendUtility.sendPacket(player, STR_HOUSING_ENTER_NEED_HOUSE);
			return;
		}

		int exitMapId = 0;
		float x = 0, y = 0, z = 0;
		byte heading = 0;
		int instanceId = 0;

		if (ownerId > 0) { // leaving
			studio = HousingService.getInstance().getPlayerStudio(ownerId);
			exitMapId = studio.getAddress().getExitMapId();
			instanceId = World.getInstance().getWorldMap(exitMapId).getWorldMapInstance().getInstanceId();
			x = studio.getAddress().getExitX();
			y = studio.getAddress().getExitY();
			z = studio.getAddress().getExitZ();
		}
		else if (studio == null) {
			return; // doesn't own studio
		}
		else { // entering own studio
			exitMapId = studio.getAddress().getMapId();
			WorldMapInstance instance = InstanceService.getPersonalInstance(exitMapId, player.getObjectId());
			if (instance == null) {
				instance = InstanceService.getNextAvailableInstance(exitMapId, player.getObjectId());
				InstanceService.registerPlayerWithInstance(instance, player);
			}
			instanceId = instance.getInstanceId();
			x = studio.getAddress().getX();
			y = studio.getAddress().getY();
			z = studio.getAddress().getZ();
			if (exitMapId == 710010000) {
				heading = 36;
			}
		}
		TeleportService2.teleportTo(player, exitMapId, instanceId, x, y, z, heading, TeleportAnimation.BEAM_ANIMATION);
	}
}
