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

package instance.abyss;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author keqi, xTz
 * @reworked Luzien
 */
@InstanceID(300140000)
public class KrotanInstance extends GeneralInstanceHandler {

	private boolean rewarded = false;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 215136: //bosses
			case 215135:
				spawnChests(npc);
				break;
			case 215413: //artifact spawns weak boss
				Npc boss = getNpc(215136);
				if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
					spawn(215135, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
					boss.getController().onDelete();
				}
		}
	}

	private void spawnChests(Npc npc) {
		if (!rewarded) {
			rewarded = true; //safety mechanism
			if (npc.getAi2().getRemainigTime() != 0) {
				long rtime = (600000 - npc.getAi2().getRemainigTime()) / 30000;
					spawn(700539, 471.05634f, 834.5538f, 199.70894f, (byte) 63);
					if (rtime > 1)
						spawn(700539, 490f, 889f, 199f, (byte) 43);
					if (rtime > 2)
						spawn(700539, 528, 903, 199.7f, (byte) 32);
					if (rtime > 3)
						spawn(700539, 578, 874, 199.7f, (byte) 10);
					if (rtime > 4)
						spawn(700539, 477, 814, 199.7f, (byte) 8);
					if (rtime > 5)
						spawn(700539, 470, 854, 199.7f, (byte) 115);
					if (rtime > 6)
						spawn(700539, 478, 873, 199.7f, (byte) 110);
					if (rtime > 7)
						spawn(700539, 507, 898, 199.7f, (byte) 96);
					if (rtime > 8)
						spawn(700539, 547, 899, 199.7f, (byte) 85);
					if (rtime > 9)
						spawn(700539, 564, 889, 199.7f, (byte) 78);
					if (rtime > 10)
						spawn(700559, 584, 855, 199.7f, (byte) 85);
					if (rtime > 11 && npc.getNpcId() == 215136)
						spawn(700540, 576.4634f, 837.3374f, 199.7f, (byte) 99);
			}
		}
	}
    @Override
    public boolean onReviveEvent(Player player) {
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        return TeleportService2.teleportTo(player, mapId, instanceId, 527.6408f, 109.9414f, 175.50763f, (byte) 75);
    }
}
