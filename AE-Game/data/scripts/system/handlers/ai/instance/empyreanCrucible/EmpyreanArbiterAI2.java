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

package ai.instance.empyreanCrucible;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;
import org.typezero.gameserver.model.instance.playerreward.CruciblePlayerReward;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("empyreanarbiter")
public class EmpyreanArbiterAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getInventory().getFirstItemByItemId(186000124) != null) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else {
			// to do
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		}
	}

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();

		if (dialogId == DialogAction.SETPRO1.id() && player.getInventory().decreaseByItemId(186000124, 1)) {
			switch (getNpcId()) {
				case 799573:
					TeleportService2.teleportTo(player, 300300000, instanceId, 358.2547f, 349.26443f, 96.09108f, (byte) 59);
					break;
				case 205426:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1260.15f, 812.34f, 358.6056f, (byte) 90);
					break;
				case 205427:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1616.0248f, 154.43837f, 126f, (byte) 10);
					break;
				case 205428:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1793.9233f, 796.92f, 469.36542f, (byte) 60);
					break;
				case 205429:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1776.4169f, 1749.9952f, 303.69553f, (byte) 0);
					break;
				case 205430:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1328.935f, 1742.0771f, 316.74188f, (byte) 0);
					break;
				case 205431:
					TeleportService2.teleportTo(player, 300300000, instanceId, 1760.9441f, 1278.033f, 394.23764f, (byte) 0);
					break;
			}
			InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
			if (instance != null) {
				CruciblePlayerReward reward = (CruciblePlayerReward) instance.getPlayerReward(player.getObjectId());
				if (reward != null) {
					reward.setPlayerDefeated(false);
				}
			}
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));

			getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player p) {
				PacketSendUtility.sendPacket(p, new SM_SYSTEM_MESSAGE(1400964, player.getName()));
			}

		});
		}
		return true;
	}
}
