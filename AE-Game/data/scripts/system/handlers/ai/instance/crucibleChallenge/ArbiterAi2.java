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

package ai.instance.crucibleChallenge;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
@AIName("arbiter")
public class ArbiterAi2 extends NpcAI2{

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();
		if (dialogId == DialogAction.SETPRO1.id()) {
			switch (getNpcId()) {
				case 205682:
					TeleportService2.teleportTo(player, 300320000, instanceId, 357.10208f, 1662.702f, 95.9803f, (byte) 60);
					break;
				case 205683:
					TeleportService2.teleportTo(player, 300320000, instanceId, 1796.5513f, 306.9967f, 469.25f, (byte) 60);
					break;
				case 205684:
					TeleportService2.teleportTo(player, 300320000, instanceId, 1324.433f, 1738.2279f, 316.476f, (byte) 70);
					break;
				case 205663:
					TeleportService2.teleportTo(player, 300320000, instanceId, 1270.8877f, 237.93307f, 405.38028f, (byte) 60);
					break;
				case 205686:
					TeleportService2.teleportTo(player, 300320000, instanceId, 357.98798f, 349.19116f, 96.09108f, (byte) 60);
					break;
				case 205687:
					TeleportService2.teleportTo(player, 300320000, instanceId, 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
					break;
				case 205685:
					TeleportService2.teleportTo(player, 300320000, instanceId, 1283.1246f, 791.6683f, 436.6403f, (byte) 60);
					break;
			}
		}
		return true;
	}

}
