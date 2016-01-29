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

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.DialogService;
import org.typezero.gameserver.services.player.PlayerMailboxState;
import org.typezero.gameserver.network.aion.serverpackets.SM_HEADING_UPDATE;

public class CM_CLOSE_DIALOG extends AionClientPacket {

	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int targetObjectId;

	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 *
	 * @param opcode
	 */
	public CM_CLOSE_DIALOG(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetObjectId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		final VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		final AionConnection client = getConnection();
		if(obj == null) {
			return;
		}

		if (obj instanceof Npc) {
			Npc npc = (Npc)obj;
			npc.getAi2().onCreatureEvent(AIEventType.DIALOG_FINISH, player);
			DialogService.onCloseDialog(npc, player);

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					client.sendPacket(new SM_HEADING_UPDATE(targetObjectId, (byte)obj.getHeading()));
				}
			}, 1200);


		}

		if (player.getMailbox().mailBoxState != 0) {
			player.getMailbox().mailBoxState = PlayerMailboxState.CLOSED;
		}
	}
}
