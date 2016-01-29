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

package org.typezero.gameserver.network.loginserver.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.ExitCode;
import org.typezero.gameserver.network.NetworkController;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.LoginServerConnection.State;
import org.typezero.gameserver.network.loginserver.LsClientPacket;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_LIST;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_GS_AUTH;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * This packet is response for SM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 *
 * @author -Nemesiss-
 */
public class CM_GS_AUTH_RESPONSE extends LsClientPacket {

	public CM_GS_AUTH_RESPONSE(int opCode) {
		super(opCode);
	}

	/**
	 * Logger for this class.
	 */
	protected static final Logger log = LoggerFactory.getLogger(CM_GS_AUTH_RESPONSE.class);

	/**
	 * Response: 0=Authed,1=NotAuthed,2=AlreadyRegistered
	 */
	private int response;

	private byte serverCount;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readImpl() {
		response = readC();
		if(response == 0)
			serverCount = (byte)readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void runImpl() {
		/**
		 * Authed
		 */
		if (response == 0) {
			getConnection().setState(State.AUTHED);
			sendPacket(new SM_ACCOUNT_LIST(LoginServer.getInstance().getLoggedInAccounts()));
			NetworkController.getInstance().setServerCount(serverCount);
		}

		/**
		 * NotAuthed
		 */
		else if (response == 1) {
			log.error("GameServer is not authenticated at LoginServer side, shutting down!");
			System.exit(ExitCode.CODE_ERROR);
		}
		/**
		 * AlreadyRegistered
		 */
		else if (response == 2) {
			log.info("GameServer is already registered at LoginServer side! trying again...");
			/**
			 * try again after 10s
			 */
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					CM_GS_AUTH_RESPONSE.this.sendPacket(new SM_GS_AUTH());
				}

			}, 10000);
		}
	}
}
