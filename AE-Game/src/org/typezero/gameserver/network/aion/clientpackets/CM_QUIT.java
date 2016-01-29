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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.services.player.PlayerLeaveWorldService;

/**
 * In this packets aion client is asking if may quit.
 *
 * @author -Nemesiss-
 */
public class CM_QUIT extends AionClientPacket {

	/**
	 * Logout - if true player is wanted to go to character selection.
	 */
	private boolean logout;

	/**
	 * Constructs new instance of <tt>CM_QUIT </tt> packet
	 *
	 * @param opcode
	 */
	public CM_QUIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		logout = readC() == 1;
	}

	@Override
	protected void runImpl() {
		AionConnection client = getConnection();

		Player player = null;
		if (client.getState() == State.IN_GAME) {
			player = client.getActivePlayer();
			// TODO! check if may quit
			if (!logout)
				LoginServer.getInstance().aionClientDisconnected(client.getAccount().getId());

			PlayerLeaveWorldService.startLeaveWorld(player);
			client.setActivePlayer(null);
		}

		if (logout) {
			if (player != null && player.isInEditMode()) {
				sendPacket(new SM_QUIT_RESPONSE(true));
				player.setEditMode(false);
			}
			else
				sendPacket(new SM_QUIT_RESPONSE());
		}
		else {
			client.close(new SM_QUIT_RESPONSE(), false);
		}
	}
}
