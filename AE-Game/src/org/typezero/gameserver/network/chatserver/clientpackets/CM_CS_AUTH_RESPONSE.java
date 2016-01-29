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

package org.typezero.gameserver.network.chatserver.clientpackets;

import com.aionemu.commons.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.network.chatserver.ChatServerConnection.State;
import org.typezero.gameserver.network.chatserver.CsClientPacket;
import org.typezero.gameserver.network.chatserver.serverpackets.SM_CS_AUTH;
import org.typezero.gameserver.services.ChatService;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class CM_CS_AUTH_RESPONSE extends CsClientPacket {

	/**
	 * Logger for this class.
	 */
	protected static final Logger log = LoggerFactory.getLogger(CM_CS_AUTH_RESPONSE.class);

	/**
	 * Response: 0=Authed,<br>
	 * 1=NotAuthed,<br>
	 * 2=AlreadyRegistered
	 */
	private int response;
	private byte[] ip;
	private int port;

	/**
	 * @param opcode
	 */
	public CM_CS_AUTH_RESPONSE(int opcode) {
		super(opcode);
	}

	@Override
	protected void readImpl() {
		response = readC();
		ip = readB(4);
		port = readH();
	}

	@Override
	protected void runImpl() {
		switch (response) {
			case 0: // Authed
				log.info("GameServer authed successfully IP : " + (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF)
					+ "." + (ip[3] & 0xFF) + " Port: " + port);
				getConnection().setState(State.AUTHED);
				ChatService.setIp(ip);
				ChatService.setPort(port);
				break;
			case 1: // NotAuthed
				log.error("GameServer is not authenticated at ChatServer side");
				System.exit(ExitCode.CODE_ERROR);
				break;
			case 2: // AlreadyRegistered
				log.info("GameServer is already registered at ChatServer side! trying again...");
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						CM_CS_AUTH_RESPONSE.this.getConnection().sendPacket(new SM_CS_AUTH());
					}

				}, 10000);
				break;
		}
	}
}
