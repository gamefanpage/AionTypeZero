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

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.concurrent.Future;

/**
 * @author antness thx to Guapo for sniffing
 */
public class CM_READ_EXPRESS_MAIL extends AionClientPacket {

	private int action;

	public CM_READ_EXPRESS_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.action = readC();
	}

	@Override
	protected void runImpl() {

		final Player player = getConnection().getActivePlayer();
		boolean haveUnreadExpress = (player.getMailbox().haveUnreadByType(LetterType.EXPRESS) || player.getMailbox().haveUnreadByType(LetterType.BLACKCLOUD));
		switch (this.action) {
			case 0:
				// window is closed
				if (player.getPostman() != null) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
				break;
			case 1:
				// click on icon
				if (player.getPostman() != null) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_POSTMAN_ALREADY_SUMMONED);
				}
				else if (player.isFlying()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_POSTMAN_UNABLE_IN_FLIGHT);
				}
				else if (player.getController().hasTask(TaskId.EXPRESS_MAIL_USE)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_POSTMAN_UNABLE_IN_COOLTIME);
				}
				else if (haveUnreadExpress) {
					VisibleObjectSpawner.spawnPostman(player);
					Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
						}

					}, 600000); // 10 min
					player.getController().addTask(TaskId.EXPRESS_MAIL_USE, task);
				}
				break;
		}
	}

}
