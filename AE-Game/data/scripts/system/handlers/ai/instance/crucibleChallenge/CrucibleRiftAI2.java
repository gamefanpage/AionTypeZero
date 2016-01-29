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

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 *
 * @author xTz
 */
@AIName("cruciblerift")
public class CrucibleRiftAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
			case 730459:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			case 730460:
				TeleportService2.teleportTo(player, 300320000, getPosition().getInstanceId(), 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
				spawn(205679, 1765.522f, 1282.1051f, 389.11743f, (byte) 0);
				AI2Actions.deleteOwner(this);
				break;
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getNpcId() == 730459) {
			announceRift();
		}
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == DialogAction.SETPRO1.id() && getNpcId() == 730459) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			TeleportService2.teleportTo(player, 300320000, getPosition().getInstanceId(), 1759.5946f, 1768.6449f, 389.11758f, (byte) 16);
			spawn(218190, 1760.8701f, 1774.7711f, 389.11743f, (byte) 110);
			spawn(218185, 1762.6906f, 1773.863f, 389.11743f, (byte) 80);
			spawn(218191, 1763.9441f, 1775.2466f, 389.1175f, (byte) 80);
			AI2Actions.deleteOwner(this);
		}
		return true;
	}

	private void announceRift() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(false, 1111482, player.getObjectId(), 2));
				}
			}

		});
	}

}
