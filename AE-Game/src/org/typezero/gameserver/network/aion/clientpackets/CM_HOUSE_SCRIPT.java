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
import org.typezero.gameserver.model.gameobjects.player.PlayerScripts;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_SCRIPTS;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class CM_HOUSE_SCRIPT extends AionClientPacket {

	int address;
	int scriptIndex;
	int totalSize;
	int compressedSize;
	int uncompressedSize;
	byte[] stream;

	public CM_HOUSE_SCRIPT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		address = readD();
		scriptIndex = readC();
		totalSize = readH();
		if (totalSize > 0) {
			compressedSize = readD();
			if (compressedSize < 8150) {
				uncompressedSize = readD();
				stream = readB(compressedSize);
			}
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		if (compressedSize > 8149) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_SCRIPT_OVERFLOW);
		}

		House house = player.getActiveHouse();
		if (house == null)
			return;

		PlayerScripts scripts = house.getPlayerScripts();

		if (totalSize <= 0) {
			// Deposit perhaps should send 0, while delete -1
			// But the client sends the same packets now
			scripts.addScript(scriptIndex, new byte[0], 0);
		} else {
			scripts.addScript(scriptIndex, stream, uncompressedSize);
		}
		
		PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(address, scripts, scriptIndex, scriptIndex));
	}

}
