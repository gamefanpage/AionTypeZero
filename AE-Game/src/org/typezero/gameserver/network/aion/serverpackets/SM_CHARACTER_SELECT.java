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

package org.typezero.gameserver.network.aion.serverpackets;


import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author cura
 */
public class SM_CHARACTER_SELECT extends AionServerPacket {

	private int type; // 0: new passkey input window, 1: passkey input window, 2: message window
	private int messageType; // 0: newpasskey complete, 2: passkey edit complete, 3: passkey input
	private int wrongCount;

	public SM_CHARACTER_SELECT(int type) {
		this.type = type;
	}

	public SM_CHARACTER_SELECT(int type, int messageType, int wrongCount) {
		this.type = type;
		this.messageType = messageType;
		this.wrongCount = wrongCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(type);

		switch (type) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				writeH(messageType); // 0: newpasskey complete, 2: passkey edit complete, 3: passkey input
				writeC(wrongCount > 0 ? 1 : 0); // 0: right passkey, 1: wrong passkey
				writeD(wrongCount); // wrong passkey input count
				writeD(SecurityConfig.PASSKEY_WRONG_MAXCOUNT); // Enter the number of possible wrong numbers (retail
				// server default value: 5)
				break;
		}
	}
}
