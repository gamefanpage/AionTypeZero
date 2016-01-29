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
@InstanceID(300130000)
public class MirenInstance extends GeneralInstanceHandler {

	private boolean rewarded = false;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 215222: // bosses
			case 215221:
				spawnChests(npc);
				break;
			case 215415: // artifact spawns weak boss
				Npc boss = getNpc(215222);
				if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
					spawn(215221, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
					boss.getController().onDelete();
				}
		}
	}

	private void spawnChests(Npc npc) {
		if (!rewarded) {
			rewarded = true; //safety mechanism
			if (npc.getAi2().getRemainigTime() != 0) {
				long rtime = (600000 - npc.getAi2().getRemainigTime()) / 30000;
					spawn(700543, 478.7917f, 815.5538f, 199.70894f, (byte) 8);
					if (rtime > 1)
						spawn(700543, 471, 853, 199f, (byte) 115);
					if (rtime > 2)
						spawn(700543, 477, 873, 199.7f, (byte) 109);
					if (rtime > 3)
						spawn(700543, 507, 899, 199.7f, (byte) 96);
					if (rtime > 4)
						spawn(700543, 548, 889, 199.7f, (byte) 83);
					if (rtime > 5)
						spawn(700543, 565, 889, 199.7f, (byte) 76);
					if (rtime > 6)
						spawn(700543, 585, 855, 199.7f, (byte) 63);
					if (rtime > 7)
						spawn(700543, 578, 874, 199.7f, (byte) 11);
					if (rtime > 8)
						spawn(700543, 528, 903, 199.7f, (byte) 30);
					if (rtime > 9)
						spawn(700543, 490, 899, 199.7f, (byte) 44);
					if (rtime > 10)
						spawn(700561, 470, 834, 199.7f, (byte) 63);
					if (rtime > 11 && npc.getNpcId() == 215222)
						spawn(700544, 576.8508f, 836.40424f, 199.7f, (byte) 44);
			}
		}
	}
    @Override
    public boolean onReviveEvent(Player player) {
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        return TeleportService2.teleportTo(player, mapId, instanceId, 527.6408f, 119.9414f, 175.50763f, (byte) 75);
    }
}
